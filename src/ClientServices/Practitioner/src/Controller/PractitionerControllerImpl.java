package Controller;

import HelperObjects.FacilityVisitLogger;
import HelperObjects.SpentCapsuleLogger;
import Messages.InfectedUserMessage;
import Objects.FacilityVisitLog;
import Objects.InfectedUser;
import Connection.ConnectionController;
import Connection.ConnectionControllerImpl;
import GUI.AppController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;

public class PractitionerControllerImpl extends Application implements PractitionerController{
    private static final ConnectionController connectionController = new ConnectionControllerImpl();
    private static final FacilityVisitLogger facilityVisitLogger = new FacilityVisitLogger();
    private static final SpentCapsuleLogger spentCapsuleLogger = new SpentCapsuleLogger();
    private static Stage primaryStage;
    private static AppController appController;
    private static Pane appPane;
    private static PrivateKey privateKey;


    ///////////////////////////////////////////////////////////////////
    ///         INTERNAL PRACTITIONER LOGIC
    ///////////////////////////////////////////////////////////////////

    public static void main(String[] args) {
        launch(args);
    }

    private static void handleException(Exception e) {
        e.printStackTrace();
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            //Set the primary stage
            this.primaryStage = primaryStage;

            //Connect to the services
            connectionController.startClientConnections();

            //load the GUI
            loadController();

            //Set the private key
            privateKey = readPrivateKey();

            //show the app screen
            showApp();
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void loadController() throws IOException {
        //Load the login controller
        FXMLLoader appLoader = new FXMLLoader(getClass().getResource("../GUI/App.fxml"));
        appPane = appLoader.load();

        //load the controller and pass the chatRoom and listener
        appController = (AppController) appLoader.getController();

        //Set the ClientSide.GUI Controller
        appController.setPractitionerController(this);
    }


    private static PublicKey readPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, ClassNotFoundException {
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

    private static PrivateKey readPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, ClassNotFoundException {
        InputStream in = new FileInputStream("Resources/private/keys/practitioner/practitionerPrivate.txt");
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

    public void showApp() throws IOException {
        //Display the chat fxml file
        Scene scene = new Scene(appPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void sendInfectedUserLogs() throws Exception {
        try {
            //Get both log files
            File facilityVisitLogFile = getLogFile("Open the visited facilities log file");
            File spentCapsuleLogFile = getLogFile("Open the spent token log file");

            //Read the both log files
            ArrayList<FacilityVisitLog> facilityVisitLogs = facilityVisitLogger.readFacilityVisitLogsFromFile(facilityVisitLogFile);
            ArrayList<byte[]> spentTokensLogs = spentCapsuleLogger.readOnlySpentTokensFromFile(spentCapsuleLogFile);

            //Create a new infected user
            InfectedUser infectedUser = new InfectedUser(spentTokensLogs, facilityVisitLogs);

            //Sign the infected user object
            Signature sign = Signature.getInstance("SHA256withRSA");
            sign.initSign(privateKey);
            ByteArrayOutputStream out = new ByteArrayOutputStream(); //Turn the object into byte array
            ObjectOutputStream os = new ObjectOutputStream(out);
            os.writeObject(infectedUser);
            sign.update(out.toByteArray());
            byte[] signature = sign.sign();

            //Create an infected user message to send to the matching service
            InfectedUserMessage infectedUserMessage = new InfectedUserMessage(infectedUser, signature);

            //Send the infected user to the matching service
            connectionController.addInfectedUser(infectedUserMessage);
        } catch (Exception e) {
            handleException(e);
            throw new Exception("Couldn't send infected user logs: Something went wrong");
        }

    }

    private File getLogFile(String fileChooserTitle) {
        //Open the File chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("log files", "*.txt")
        );
        fileChooser.setTitle(fileChooserTitle);
        return fileChooser.showOpenDialog(primaryStage);
    }
}
