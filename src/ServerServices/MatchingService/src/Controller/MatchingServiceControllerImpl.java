package Controller;

import Exceptions.NotValidException;
import GUI.AppController;
import Messages.InfectedUserMessage;
import Objects.CapsuleLog;
import Objects.FacilityVisitLog;
import Connection.ConnectionController;
import Connection.ConnectionControllerImpl;
import Controller.HelperObjects.DeleteExpiredCaller;
import Controller.HelperObjects.SendUnacknowledgedTokensCaller;
import Data.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;

public class MatchingServiceControllerImpl extends Application implements MatchingServiceController{
    private static DBConnection dbConnection;
    private static ConnectionController connectionController;
    private static PrivateKey mixingProxyPrivateKey;
    private static PublicKey mixingProxyPublicKey;
    private static PublicKey practitionerPublicKey;
    private static final int uninformedRevealPeriodInDays = 1;
    private static final int expirationPeriodInDays = 30;
    private static Stage primaryStage;
    private static AppController appController;
    private static Pane appPane;

    ///////////////////////////////////////////////////////////////////
    ///         MIXING PROXY INTERNAL LOGIC
    ///////////////////////////////////////////////////////////////////
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            System.out.println("Starting Matching service...");

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

            //Start the client connections
            connectionController.startClientConnections();

            //Delete the expired capsules
            deleteExpiredCapsules();

            //Show the app screen
            showApp();

            //load the db capsules in the GUI
            appController.showCapsules(dbConnection.getAllCapsules());

            System.out.println("Matching service ready");
        } catch (Exception e){
            handleException(e);
        }
    }

    public void refreshPrimaryStage() {
        primaryStage.show();
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
        appController.setMatchingServiceController(this);
    }

    private void setUpControllerVariables() throws InvalidKeySpecException, ClassNotFoundException, NoSuchAlgorithmException, IOException {
        if (dbConnection == null) dbConnection = new DBConnection();
        if (connectionController == null) connectionController = new ConnectionControllerImpl(this);
        if (mixingProxyPrivateKey == null || mixingProxyPublicKey == null || practitionerPublicKey == null) {
            mixingProxyPublicKey = readPublicKey();
            mixingProxyPrivateKey = readPrivateKey();
            practitionerPublicKey = readPractitionerPublicKey();
        }
    }

    private static PublicKey readPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, ClassNotFoundException {
        InputStream in = new FileInputStream("Resources/private/keys/matchingService/matchingServicePublic.txt");
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
        InputStream in = new FileInputStream("Resources/private/keys/matchingService/matchingServicePrivate.txt");
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

    private static PublicKey readPractitionerPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, ClassNotFoundException {
        InputStream in = new FileInputStream("Resources/private/keys/practitioner/practitionerPublic.txt");
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


    public void deleteExpiredCapsules() {
        try {
            //set the expirationDate
            LocalDate expirationDate = LocalDate.now().minusDays(expirationPeriodInDays);

            //Remove the expired capsules from the db
            dbConnection.deleteExpiredCapsules(expirationDate);

            //Plan the next db flush
            java.util.Date taskDate = Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant());
            Timer deleteExpiredTimer = new Timer();
            deleteExpiredTimer.schedule(new DeleteExpiredCaller(this), taskDate );

            //Show the new capsule contents
            appController.showCapsules(dbConnection.getAllCapsules());
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void stop() {
        int status = 0;
        try {
            System.out.println("Stopping Matching service...");
            dbConnection.closeConnection();
        } catch (Exception e) {
            System.out.println("Shutdown failed: " + e.getMessage());
            status = -1;
        } finally {
            System.exit(status);
        }
    }

    private boolean containsValidFacilityVisits(InfectedUserMessage infectedUserMessage) throws Exception {
        ArrayList<FacilityVisitLog> facilityVisitLogs = infectedUserMessage.getInfectedUser().getInfectedFacilityIntervals();

        for (FacilityVisitLog facilityVisitLog : facilityVisitLogs) {
            //Extract the information from the log
            String facilityIdentifier = facilityVisitLog.getVisitedFacility().getFacilityIdentifier();
            LocalDate visitedDate = facilityVisitLog.getEntryTime().toLocalDate();
            byte [] randomKey = facilityVisitLog.getVisitedFacility().getRandomKey();
            byte [] facilityKey = facilityVisitLog.getVisitedFacility().getFacilityKey();

            //Get the pseudonym from the visited facility on the visit day
            byte[] facilityPseudonym = connectionController.getFacilityPseudonym(facilityIdentifier , visitedDate );

            //Hash the random key and facility pseudonym
            byte[] hashedPseudonym = hashPseudonym(facilityPseudonym, randomKey);

            //The log is correct if the hash from the randomKey and pseudonym equals the facilityKey
            if (!Arrays.equals(hashedPseudonym, facilityKey)) return false;
        }

        return true;
    }

    private byte[] hashPseudonym(byte[] facilityPseudonym, byte[] randomKey) throws NoSuchAlgorithmException {
        byte[] temp = new byte[facilityPseudonym.length + randomKey.length];

        ByteBuffer buff = ByteBuffer.wrap(temp);
        buff.put(facilityPseudonym);
        buff.put(randomKey);

        byte[] combinedPreHash = buff.array();

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(combinedPreHash);
    }

    private boolean isValidMessageSignature(InfectedUserMessage infectedUserMessage) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException, IOException {
        byte[] signature = infectedUserMessage.getSignature();

        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initVerify(practitionerPublicKey);
        sign.update(infectedUserMessage.getInfectedUserBytes());
        return sign.verify(signature);
    }

    private static void handleException(Exception e) {
        e.printStackTrace();
    }



    ///////////////////////////////////////////////////////////////////
    ///         PRACTITIONER LOGIC
    ///////////////////////////////////////////////////////////////////
    @Override
    public void addInfectedUser(InfectedUserMessage infectedUserMessage) throws Exception {
        try {
            //Test if the message signature is valid
            if (!isValidMessageSignature(infectedUserMessage)) throw new SignatureException(
                    "Couldn't insert infected user: message signature doesn't match");

            //Test if the facility visits in the message itself are valid
            if (!containsValidFacilityVisits(infectedUserMessage)) throw new NotValidException(
                    "Couldn't insert infected user: logs contain invalid facility");

            //Set the critical capsules with the visited facility logs
            dbConnection.markCriticalCapsules(infectedUserMessage.getInfectedUser().getInfectedFacilityIntervals());


            //Set the infected user tokens to informed
            dbConnection.markInformed(infectedUserMessage.getInfectedUser().getInfectedTokens());

            //Show the new capsule contents
            appController.showCapsules(dbConnection.getAllCapsules());
        } catch (SignatureException e) {
            throw e;
        } catch (Exception e) {
            handleException(e);
            throw new Exception("Couldn't insert infected user: something went wrong");
        }
    }



    @Override
    public void submitAcknowledgements(ArrayList<byte[]> acknowledgementTokens) throws Exception {
        try {
            dbConnection.markInformed(acknowledgementTokens);

            //Show the new capsule contents
            appController.showCapsules(dbConnection.getAllCapsules());
        } catch (Exception e) {
            handleException(e);
            throw new Exception("Couldn't acknowledge tokens: Something went wrong");
        }
    }


    ///////////////////////////////////////////////////////////////////
    ///         MIXING PROXY LOGIC
    ///////////////////////////////////////////////////////////////////
    @Override
    public void addCapsules(ArrayList<CapsuleLog> capsules) throws Exception {
        try {
            dbConnection.addCapsules(capsules);

            //Show the new capsule contents
            appController.showCapsules(dbConnection.getAllCapsules());
        } catch (Exception e) {
            handleException(e);
            throw new Exception("Couldn't upload the capsule logs: Something went wrong");
        }
    }



    ///////////////////////////////////////////////////////////////////
    ///         REGISTRAR LOGIC
    ///////////////////////////////////////////////////////////////////
    public void sendUnacknowledgedTokens() {
        try {
            ArrayList<byte[]> unacknowledgedTokens = dbConnection.getUnacknowledgedTokens(LocalDate.now().minusDays(uninformedRevealPeriodInDays));
            connectionController.addUnacknowledgedTokens(unacknowledgedTokens);

            //Set the send tokens as acknowledged
            dbConnection.markInformed(unacknowledgedTokens);

            //Set a timer task to send the unacknowledged tokens to the registrar
            java.util.Date taskDate = Date.from(LocalDateTime.now().plusDays(uninformedRevealPeriodInDays).atZone(ZoneId.systemDefault()).toInstant());
            Timer sendUninformedTimer = new Timer();
            sendUninformedTimer.schedule(new SendUnacknowledgedTokensCaller(this), taskDate );
        } catch (Exception e) {
            handleException(e);
        }
    }



    ///////////////////////////////////////////////////////////////////
    ///         USER LOGIC
    ///////////////////////////////////////////////////////////////////
    @Override
    public ArrayList<CapsuleLog> getInfectedCapsules() throws Exception {
        try {
            return dbConnection.getInfectedCapsules();
        } catch (Exception e) {
            throw new Exception("Couldn't fetch infected capsules: something went wrong");
        }
    }



}
