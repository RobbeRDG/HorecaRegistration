package Controller;

import Common.Messages.TokenUpdate;
import Connection.ConnectionController;
import Connection.ConnectionControllerImpl;
import GUI.App.AppController;
import GUI.Login.LoginController;
import Objects.TokenWallet;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class UserControllerImpl extends Application implements UserController{
    private static final ConnectionController connectionController = new ConnectionControllerImpl();
    private static Stage primaryStage;
    private static LoginController loginController;
    private static AppController appController;
    private static Pane loginPane;
    private static Pane appPane;
    private static String userIdentifier;
    private static final TokenWallet tokenWallet = new TokenWallet();


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
        TokenUpdate update = connectionController.getTodaysTokens(userIdentifier);

        //Place the tokens in the Token Wallet
        tokenWallet.updateTokens(update);

        //Update the token count in the GUI
        appController.updateTokenCount(tokenWallet.getNumberOfTokens());
    }

    @Override
    public void scanQR() {
        //Open the File chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.showOpenDialog(primaryStage);
    }

}
