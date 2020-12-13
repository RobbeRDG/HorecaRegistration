package GUI.App;

import Controller.UserController;
import Controller.UserControllerImpl;
import com.google.zxing.NotFoundException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class AppController {
    private UserController userController;
    private boolean visitingFacility;

    @FXML
    Button registerToFacilityButton;

    @FXML
    AnchorPane activeTokenPane;

    public void setUserController(UserControllerImpl userController) {
        this.userController = userController;
    }

    @FXML
    public void handleCheckInfectionStatusButton(javafx.event.ActionEvent actionEvent) {
        try {
            boolean possibleInfection = userController.checkInfectionStatus();

            //Show an alert
            String headerText = "";
            if (possibleInfection) headerText = "High risk: an infected token has been found in your logs";
            else headerText = "No Risk: no infected tokens have been found in your logs";
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("STATUS");
            alert.setHeaderText(null);
            alert.setContentText(headerText);
            alert.show();
            return;
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.show();
            return;
        }
    }

    @FXML
    public void handleRegisterToFacilityButton(javafx.event.ActionEvent actionEvent)  {
        if (visitingFacility) {
            try {
                userController.leaveFacility();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText(null);
                alert.setContentText(e.getMessage());
                alert.show();
                return;
            }
        } else {
            try {
                userController.registerToFacility();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText(null);
                alert.setContentText(e.getMessage());
                alert.show();
                return;
            }
        }
    }

    public void tokenUp(LocalDateTime startTime, LocalDateTime stopTime, ImageView symbol) {
        //format the dates to a string notation
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String startTimeString = startTime.format(formatter);
        String stopTimeString = stopTime.format(formatter);

        //Create the layout for the remaining times
        Label activeTokenHeader = new Label("Current active token");
        activeTokenHeader.setFont(new Font("Arial", 24));
        Label startTimeLabel = new Label("Valid from: " + startTimeString);
        Label stopTimeLabel = new Label("Valid until: " + stopTimeString);
        Label ExpireInformationLabel = new Label("A new token will be fetched when the current token expires");

        //Place the elements in a vbox
        VBox container = new VBox(activeTokenHeader, startTimeLabel, stopTimeLabel, ExpireInformationLabel, symbol);
        container.setAlignment(Pos.CENTER);
        container.setSpacing(5);

        //Clear the active token pane and place the vbox in it
        activeTokenPane.getChildren().clear();
        activeTokenPane.getChildren().add(container);
        AnchorPane.setTopAnchor(container, 0.0);
        AnchorPane.setLeftAnchor(container, 0.0);
        AnchorPane.setRightAnchor(container, 0.0);

        //change the register to facility button text
        registerToFacilityButton.setText("Leave registered facility");

        //Tell the app controller that a token is active
        visitingFacility = true;
    }

    public void tokenDown() {
        //Clear the active token pane and add label
        activeTokenPane.getChildren().clear();
        activeTokenPane.getChildren().add(new Label("No active token at the moment"));
        registerToFacilityButton.setText("Register to facility");

        //Tell the app controller that no token is active
        visitingFacility = false;
    }
}
