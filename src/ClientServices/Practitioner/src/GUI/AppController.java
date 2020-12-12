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
        practitionerController.sendInfectedUserLogs();
    }


    public void setPractitionerController(PractitionerController practitionerController) {
        this.practitionerController = practitionerController;
    }
}