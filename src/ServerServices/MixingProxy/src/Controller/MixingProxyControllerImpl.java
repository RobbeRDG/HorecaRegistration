package Controller;

import Exceptions.NotValidException;
import GUI.AppController;
import Messages.CapsuleVerification;
import Objects.CapsuleLog;
import Connection.ConnectionController;
import Connection.ConnectionControllerImpl;
import Controller.HelperObjects.FlushCapsulesCaller;
import Data.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;

public class MixingProxyControllerImpl extends Application implements MixingProxyController{
    private static DBConnection dbConnection;
    private static ConnectionController connectionController;
    private static PrivateKey mixingProxyPrivateKey;
    private static PublicKey mixingProxyPublicKey;
    private static final int flushPeriodInSeconds = 60;
    private static Stage primaryStage;
    private static AppController appController;
    private static Pane appPane;

    ///////////////////////////////////////////////////////////////////
    ///         MIXING PROXY INTERNAL LOGIC
    ///////////////////////////////////////////////////////////////////
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            System.out.println("Starting Mixing proxy service...");

            this.primaryStage = primaryStage;

            //Setup variables
            setUpControllerVariables();

            //load the app gui controller
            loadControllers();

            //Connect to the database
            dbConnection.connectToDatabase();

            //Start the connection server
            connectionController.startServerConnections();

            //Sleep for 10 sec
            Thread.sleep(10000);

            //Start the clientConnections
            connectionController.startClientConnections();

            //Flush capsules
            flushCapsules();

            //Show the app screen
            showApp();

            //load the db capsules in the GUI
            appController.showCapsules(dbConnection.getAllCapsules());

            System.out.println("Mixing proxy ready");
        } catch (Exception e){
            handleException(e);
        }
    }

    private static PublicKey readPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, ClassNotFoundException {
        InputStream in = new FileInputStream("Resources/private/keys/mixingProxy/mixingProxyPublic.txt");
        ObjectInputStream oin = new ObjectInputStream(new BufferedInputStream(in));
        try {
            BigInteger m = (BigInteger) oin.readObject();
            BigInteger e = (BigInteger) oin.readObject();
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PublicKey pubKey = fact.generatePublic(keySpec);
            return pubKey;
        } catch (Exception e) {
            throw e;
        } finally {
            oin.close();
        }
    }

    private static PrivateKey readPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, ClassNotFoundException {
        InputStream in = new FileInputStream("Resources/private/keys/mixingProxy/mixingProxyPublic.txt");
        ObjectInputStream oin = new ObjectInputStream(new BufferedInputStream(in));
        try {
            BigInteger m = (BigInteger) oin.readObject();
            BigInteger e = (BigInteger) oin.readObject();
            RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(m, e);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = fact.generatePrivate(keySpec);
            return privateKey;
        } catch (Exception e) {
            throw e;
        } finally {
            oin.close();
        }
    }

    public static void main(String[] args){
        try {
            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
            }

            launch(args);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void showApp() throws IOException {
        //Display the chat fxml file
        Scene scene = new Scene(appPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadControllers() throws IOException {
        //Load the app controller
        FXMLLoader appLoader = new FXMLLoader(getClass().getResource("../GUI/App.fxml"));
        appPane = appLoader.load();

        //load the controller and pass the chatRoom and listener
        appController = (AppController) appLoader.getController();
        //Set the ClientSide.GUI Controller
        appController.setMixingProxyController(this);
    }

    private void setUpControllerVariables() throws InvalidKeySpecException, ClassNotFoundException, NoSuchAlgorithmException, IOException {
        if (dbConnection == null) dbConnection = new DBConnection();
        if (connectionController == null) connectionController = new ConnectionControllerImpl(this);
        if (mixingProxyPrivateKey == null || mixingProxyPublicKey == null) {
            mixingProxyPublicKey = readPublicKey();
            mixingProxyPrivateKey = readPrivateKey();
        }
    }


    @Override
    public void refreshPrimaryStage() {
        primaryStage.show();
    }

    @Override
    public void stop() {
        int status = 0;
        try {
            System.out.println("Stopping Mixing proxy service...");
            dbConnection.closeConnection();
        } catch (Exception e) {
            System.out.println("Shutdown failed: " + e.getMessage());
            status = -1;
        } finally {
            System.exit(status);
        }
    }

    public void flushCapsules() {
        try {
            //Get all capsules
            ArrayList<CapsuleLog> capsuleLogs = dbConnection.extractCapsules();

            //Send all capsules to the matching service
            Collections.shuffle(capsuleLogs);
            connectionController.flushCapsules(capsuleLogs);

            //Delete all the recently fetched capsules
            dbConnection.deleteCapsules(capsuleLogs);

            //Set a new timer for the next flush
            java.util.Date flushDate = Date.from(LocalDateTime.now().plusSeconds(flushPeriodInSeconds).atZone(ZoneId.systemDefault()).toInstant());
            Timer nextFlush = new Timer();
            nextFlush.schedule(new FlushCapsulesCaller(this), flushDate);

            System.out.println("Flushed the current capsules");
        } catch (Exception e) {
            handleException(e);
        }

    }



    private static void handleException(Exception e) {
        e.printStackTrace();
    }

    ///////////////////////////////////////////////////////////////////
    ///         TOKEN REGISTRATION
    ///////////////////////////////////////////////////////////////////
    @Override
    public CapsuleVerification registerToken(CapsuleLog capsuleLog) throws Exception {
        try {
            //Check if the token is valid
            if (!validateCapsule(capsuleLog)) throw new NotValidException("The send capsule is not a valid capsule");

            //Check if the capsule is not already registered
            if (dbConnection.containsCapsule(capsuleLog)) throw new NotValidException("Capsule is already registered");

            //Place the capsule in the db
            dbConnection.addCapsule(capsuleLog);

            //Return the signed facility key
            return generateCapsuleVerification(capsuleLog);
        } catch (NotValidException e) {
            throw e;
        } catch (Exception e) {
            handleException(e);
            throw new Exception("Couldn't register tokens : Something went wrong");
        }

    }

    private CapsuleVerification generateCapsuleVerification(CapsuleLog capsuleLog) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initSign(mixingProxyPrivateKey);

        //Get the facility key
        byte[] facilityKey = capsuleLog.getFacilityKey();

        //Sign the facility key
        sign.update(facilityKey);
        byte[] keySignature = sign.sign();

        return new CapsuleVerification(facilityKey, keySignature);
    }

    @Override
    public void addTokens(LocalDate date, ArrayList<byte[]> tokens) throws Exception {
        try {
            dbConnection.addTokens(date, tokens);
        } catch (Exception e) {
            handleException(e);;
        }
    }

    private boolean validateCapsule(CapsuleLog capsuleLog) throws SQLException {
        //Extract the capsule contents
        byte[] token = capsuleLog.getToken();
        LocalDateTime startTime = capsuleLog.getStartTime();
        LocalDateTime stopTime = capsuleLog.getStopTime();
        byte[] facilityKey = capsuleLog.getFacilityKey();

        //Get the tokenbytes and date from the valid tokens table
        ResultSet rs = dbConnection.getValidToken(token, startTime.toLocalDate());

        if (rs.next()) {
            //Test if the startTime is on the day of the valid token
            LocalDate validDate = rs.getDate("date").toLocalDate();
            LocalDate startDate = startTime.toLocalDate();

            return startDate.isEqual(validDate);
        } else return false;

    }

    ///////////////////////////////////////////////////////////////////
    ///         INFECTED USER LOGIC
    ///////////////////////////////////////////////////////////////////
    @Override
    public void acknowledgeTokens(ArrayList<byte[]> acknowledgeTokens) throws Exception {
        try {
            connectionController.forwardAcknowledgeTokens(acknowledgeTokens);
        } catch (Exception e) {
            handleException(e);
            throw new Exception("Couldn't acknowledge infected tokens: Something went wrong");
        }
    }


}
