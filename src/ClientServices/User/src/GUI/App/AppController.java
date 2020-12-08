package GUI.App;

import Controller.UserController;
import Controller.UserControllerImpl;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class AppController {
    private UserController userController;

    @FXML
    Label tokenCountLabel;

    public void setUserController(UserControllerImpl userController) {
        this.userController = userController;
    }

    @FXML
    public void handleQRScanButton(javafx.event.ActionEvent actionEvent) {
        userController.scanQR();
    }

    public void updateTokenCount(int numberOfTokens) {
        tokenCountLabel.setText(numberOfTokens + " Token(s) remaining today");
    }
}
