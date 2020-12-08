package GUI.Login;

import Common.Exceptions.AlreadyRegisteredException;
import Controller.UserController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {
    private UserController userController;

    @FXML
    TextField phoneNumberField;

    @FXML
    Button loginButton;

    @FXML
    Button registerButton;


    @FXML
    public void handleLoginButton(javafx.event.ActionEvent actionEvent) throws Exception {
        String phoneNumber = phoneNumberField.getText();

        //if the text input is empty, show a warning
        if(phoneNumber.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please write a user name");
            alert.show();
            return;
        } else if (phoneNumber.length() != 10) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("The phone number should be 10 digits long");
            alert.show();
            return;
        }
        try {
            Integer.parseInt(phoneNumber);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("The phone number is not a number");
            alert.show();
            return;
        }


        //test if the username is already registered
        try {
            userController.registerUSer(phoneNumber);
            userController.setUserIdentifier(phoneNumber);
            userController.showApp();
            userController.getTodaysTokens();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("A new user has been created");
            alert.show();
        } catch (AlreadyRegisteredException e) {
            userController.setUserIdentifier(phoneNumber);
            userController.showApp();
            userController.getTodaysTokens();
            return;
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Already registered");
            alert.setHeaderText(null);
            alert.setContentText("Something went wrong");
            alert.show();
            return;
        }
    }


    public void setUserController(UserController userController) {
        this.userController = userController;
    }
}