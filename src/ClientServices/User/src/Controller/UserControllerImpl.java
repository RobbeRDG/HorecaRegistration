package Controller;

import Exceptions.NotValidException;
import HelperObjects.FacilityVisitLogger;
import HelperObjects.SpentCapsuleLogger;
import Messages.CapsuleVerification;
import Messages.TokenUpdate;
import Objects.CapsuleLog;
import Objects.FacilityRegisterInformation;
import Connection.ConnectionController;
import Connection.ConnectionControllerImpl;
import Controller.HelperObjects.*;
import GUI.App.AppController;
import GUI.Login.LoginController;
import com.google.zxing.NotFoundException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.sql.Date;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;

public class UserControllerImpl extends Application implements UserController{
    private static final ConnectionController connectionController = new ConnectionControllerImpl();
    private static Stage primaryStage;
    private static LoginController loginController;
    private static AppController appController;
    private static Pane loginPane;
    private static Pane appPane;
    private static String userIdentifier;
    private static TokenWallet tokenWallet;
    private static final QRReader qrReader = new QRReader();
    private static final SymbolGenerator symbolGenerator = new SymbolGenerator();
    private static final FacilityVisitLogger facilityVisitLogger = new FacilityVisitLogger();
    private static final SpentCapsuleLogger spentCapsuleLogger = new SpentCapsuleLogger();
    private static Timer replaceTokenTimer;
    private static PublicKey registrarPublicKey;

    ///////////////////////////////////////////////////////////////////
    ///         INTERNAL USER LOGIC
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

            //load the chat and login controller
            loadControllers();

            //Initialize the token wallet
            tokenWallet = new TokenWallet(this);

            //read the registrar public key
            registrarPublicKey = readRegistrarPublicKey();

            //show the login screen
            showLogin();
        } catch (Exception e) {
          handleException(e);
        }

    }

    private void loadControllers() throws IOException {
        //Load the login controller
        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("../GUI/Login/login.fxml"));
        loginPane = loginLoader.load();

        //load the controller and pass the chatRoom and listener
        loginController = (LoginController)loginLoader.getController();

        //Set the ClientSide.GUI Controller
        loginController.setUserController(this);

        //Load the app controller
        FXMLLoader appLoader = new FXMLLoader(getClass().getResource("../GUI/App/App.fxml"));
        appPane = appLoader.load();

        //load the controller and pass the chatRoom and listener
        appController = (AppController) appLoader.getController();
        //Set the ClientSide.GUI Controller
        appController.setUserController(this);
    }

    @Override
    public void showLogin() throws IOException {
        //Display the login fxml file
        Scene scene = new Scene(loginPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void showApp() throws IOException {
        //Display the chat fxml file
        Scene scene = new Scene(appPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static PublicKey readRegistrarPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, ClassNotFoundException {
        InputStream in = new FileInputStream("Resources/private/keys/registrar/registrarPublic.txt");
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

    @Override
    public void stop() {
        try {
            System.out.println("stopping user...");
            System.exit(0);
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public PublicKey getRegistrarPublicKey() {
        return registrarPublicKey;
    }

    @Override
    public boolean checkInfectionStatus() throws Exception {
        try {
            //Get all infected capsule logs from matching server
            ArrayList<CapsuleLog> infectedCapsules = connectionController.getInfectedCapsules();

            //Read all capsule logs from log file
            ArrayList<CapsuleLog> loggedCapsules = spentCapsuleLogger.readCapsules();

            //Compare if the user log contains infected capsules
            ArrayList<byte[]> acknowledgeTokens = compareInfectedCapsules(infectedCapsules, loggedCapsules);

            //Send the acknowledgement tokens to the matching server
            connectionController.acknowledgeTokens(acknowledgeTokens);

            if (acknowledgeTokens.isEmpty()) return false;
            else return true;
        } catch (Exception e) {
            handleException(e);
            throw e;
        }
    }

    private ArrayList<byte[]> compareInfectedCapsules(ArrayList<CapsuleLog> infectedCapsules, ArrayList<CapsuleLog> loggedCapsules) {
        ArrayList<byte[]> acknowledgeTokens = new ArrayList<>();

        //Get all the tokens from the infected and logged capsules
        ArrayList<byte[]> loggedTokes = new ArrayList<>();
        ArrayList<byte[]> infectedTokens = new ArrayList<>();
        for (CapsuleLog loggedCapsule : loggedCapsules) {
           loggedTokes.add(loggedCapsule.getToken());
        }
        for (CapsuleLog infectedCapsule : infectedCapsules) {
            infectedTokens.add(infectedCapsule.getToken());
        }

        //Test if the logged tokens contain infected tokens
        for ( byte[] loggedToken : loggedTokes) {
            for ( byte[] infectedToken : infectedTokens) {
                if (Arrays.equals(loggedToken, infectedToken)) acknowledgeTokens.add(loggedToken);
            }
        }

        return acknowledgeTokens;
    }


    ///////////////////////////////////////////////////////////////////
    ///         USER LOGIC
    ///////////////////////////////////////////////////////////////////

    @Override
    public void registerUSer(String userIdentifier) throws Exception {
        connectionController.registerUSer(userIdentifier);
    }

    @Override
    public void setUserIdentifier(String phoneNumber) {
        userIdentifier = phoneNumber;
        facilityVisitLogger.setLogFileName(phoneNumber);
        spentCapsuleLogger.setLogFileName(phoneNumber);
    }

    @Override
    public void getTodaysTokens() throws Exception {
        try {
            TokenUpdate update = connectionController.getTodaysTokens(userIdentifier);

            //Test if the signatures match
            if (!tokenWallet.signaturesMatch(update)) throw new SignatureException("Couldn't get today's tokens: signatures don't match");

            //Place the tokens in the Token Wallet
            tokenWallet.updateTokens(update);
        } catch (Exception e) {
            handleException(e);
            throw e;
        }

    }

    @Override
    public void refreshToken() {
        try {
            //Run through all capsule register logic
            registerCapsuleLogic();
        } catch (Exception e) {
            handleException(e);
        }
    }



    @Override
    public void registerToFacility() throws Exception {
        try {
            //Scan the facility QR code
            FacilityRegisterInformation currentFacilityRegisterInformation = scanQR();

            //place the facility information in the wallet
            tokenWallet.setCurrentFacility(currentFacilityRegisterInformation);

            //Initialize the facility visit logger
            facilityVisitLogger.startVisit(currentFacilityRegisterInformation);

            //Run through all capsule register logic
            registerCapsuleLogic();
        } catch ( NotFoundException | IOException e) {
            handleException(e);
            throw new Exception("Can't register to restaurant: QR code not readable");
        } catch (Exception e) {
            handleException(e);
            throw new Exception("Can't register to restaurant: Something went wrong");
        }
    }

    private void registerCapsuleLogic() throws Exception {
        //Verify a capsule to the mixing proxy
        CapsuleVerification verification = registerToMixingProxy();

        //Log the new Capsule
        spentCapsuleLogger.logCapsule(tokenWallet.getCurrentCapsule());

        //Generate symbol from the verification bytes
        ImageView symbol = symbolGenerator.generateConfirmationSymbol(verification.getKeySignature());

        //Update the GUI
        appController.tokenUp(tokenWallet.getCurrentCapsule().getStartTime(), tokenWallet.getCurrentCapsule().getStopTime(), symbol);

        //Set a timer to update the token when current one expires
        java.util.Date refreshDate = Date.from(tokenWallet.getCurrentCapsule().getStopTime().atZone(ZoneId.systemDefault()).toInstant());
        replaceTokenTimer = new Timer();
        replaceTokenTimer.schedule(new RefreshTokenCaller(this), refreshDate);
    }



    private FacilityRegisterInformation scanQR() throws NotFoundException, IOException {
        //Open the File chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG Files", "*.png")
        );
        fileChooser.setTitle("Open QR code png File");
        File QRCodeFile = fileChooser.showOpenDialog(primaryStage);

        //Get the QR code string from the image
        String QRCodeString = qrReader.readQRCodeString(QRCodeFile);

        //Extract the facility from the QR string
        return FacilityRegisterInformation.fromBase64String(QRCodeString);
    }

    @Override
    public void leaveFacility() throws Exception {
        try {
            //Cancel the token refresh task
            replaceTokenTimer.cancel();

            //Conclude the visit in the tokenwallet
            tokenWallet.leaveFacility();

            //Conclude the visit in the visit logger
            facilityVisitLogger.stopVisit();

            //Update the GUI
            appController.tokenDown();
        } catch (Exception e) {
            handleException(e);
            throw new Exception("Can't execute leave facility logic: Something went wrong");
        }
    }



    public CapsuleVerification registerToMixingProxy() throws Exception {
        while (true) {
            try {
                //Get a capsule from the tokenwallet
                CapsuleLog capsuleLog = tokenWallet.getCapsule();
                //send the generated capsule to the mixing proxy
                CapsuleVerification verification = connectionController.registerCapsule(capsuleLog);
                //Set the accepted capsule as active capsule in the tokenwallet
                tokenWallet.setCurrentCapsule(capsuleLog);

                return verification;
            } catch (NotValidException e) {
                //Start over
            }
        }
    }


}
