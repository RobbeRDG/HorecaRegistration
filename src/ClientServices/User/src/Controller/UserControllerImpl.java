package Controller;

import Common.Messages.TokenUpdate;
import Connection.ConnectionController;
import Connection.ConnectionControllerImpl;
import GUI.App.AppController;
import GUI.Login.LoginController;
import Common.Objects.TokenWallet;
import QRReader.QRReader;
import com.google.zxing.NotFoundException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

public class UserControllerImpl extends Application implements UserController{
    private static final ConnectionController connectionController = new ConnectionControllerImpl();
    private static Stage primaryStage;
    private static LoginController loginController;
    private static AppController appController;
    private static Pane loginPane;
    private static Pane appPane;
    private static String userIdentifier;
    private static final TokenWallet tokenWallet = new TokenWallet();
    private static final QRReader qrReader = new QRReader();

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
            connectionController.connectToServices();

            //load the chat and login controller
            loadControllers();

            //Set the tokenwallet's public key
            tokenWallet.setRegistrarPublicKey(readRegistrarPublicKey());

            //show the login screen
            showLogin();
        } catch (Exception e) {
          handleException(e);
        }

    }

    private void loadControllers() throws IOException {
        //Load the login controller
        System.out.println(System.getProperty("user.dir"));
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
    }

    @Override
    public void getTodaysTokens() throws Exception {
        try {
            TokenUpdate update = connectionController.getTodaysTokens(userIdentifier);

            //Place the tokens in the Token Wallet
            tokenWallet.updateTokens(update);

            //Test if the signatures match
            if (!tokenWallet.signaturesMatch()) throw new Exception("Couldn't get today's tokens: signatures don't match");

            //Update the token count in the GUI
            appController.updateTokenCount(tokenWallet.getNumberOfTokens());
        } catch (Exception e) {
            handleException(e);
            throw e;
        }

    }

    private void scanQR() throws NotFoundException, IOException {
        //Open the File chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG Files", "*.png")
        );
        fileChooser.setTitle("Open QR code png File");
        File QRCodeFile = fileChooser.showOpenDialog(primaryStage);

        //Get the QR code string from the image
        String QRCodeString = qrReader.readQRCodeString(QRCodeFile);

        //Decode the QR string into the Random key, facility identifier and pseudonym
        decodeQRString(QRCodeString);
    }

    @Override
    public void registerToFacility() throws Exception {
        try {
            //Scan the facility QR code
            scanQR();


        } catch ( NotFoundException | IOException e) {
            handleException(e);
            throw new Exception("Can't register to restaurant: QR code readable");
        }


    }

    private void decodeQRString(String qrCodeString) {
        String[] QRStringArray = qrCodeString.split(",");

        byte[] randomKey = Base64.getDecoder().decode(QRStringArray[0]);
        String facilityIdentifier = new String(Base64.getDecoder().decode(QRStringArray[1]));
        byte[] facilityPseudonym = Base64.getDecoder().decode(QRStringArray[2]);
    }

}
