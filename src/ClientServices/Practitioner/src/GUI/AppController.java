package GUI;

import Controller.PractitionerController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;

public class AppController {
    private PractitionerController practitionerController;

    @FXML
    Button sendInfectedLogsButton;


    @FXML
    public void handleSendInfectedLogsButton(javafx.event.ActionEvent actionEvent) throws Exception {
        try {
            practitionerController.sendInfectedUserLogs();

            //Show a confirmation popup
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("SUCCESS");
            alert.setHeaderText(null);
            alert.setContentText("Infected logs uploaded");
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


    public void setPractitionerController(PractitionerController practitionerController) {
        this.practitionerController = practitionerController;
    }
}