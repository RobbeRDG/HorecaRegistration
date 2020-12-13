package GUI;

import Controller.HelperObjects.MixingProxyCapsuleDBEntry;
import Controller.MixingProxyController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class AppController {
    private MixingProxyController mixingProxyController;

    @FXML
    TableView capsuleTableView;


    public void setMixingProxyController(MixingProxyController mixingProxyController) {
        this.mixingProxyController = mixingProxyController;
    }

    public void showCapsules(ArrayList <MixingProxyCapsuleDBEntry> dbEntries) {
        try {
            Platform.runLater(new Runnable() {
                @Override public void run() {
                    TableColumn tokenColumn = new TableColumn("Token");
                    tokenColumn.setMinWidth(100);
                    tokenColumn.setCellValueFactory(
                            new PropertyValueFactory<MixingProxyCapsuleDBEntry, String>("token"));

                    TableColumn facilityKeyColumn = new TableColumn("FacilityKey");
                    facilityKeyColumn.setMinWidth(100);
                    facilityKeyColumn.setCellValueFactory(
                            new PropertyValueFactory<MixingProxyCapsuleDBEntry, String>("facilityKey"));

                    TableColumn startTimeColumn = new TableColumn("Start time");
                    startTimeColumn.setMinWidth(200);
                    startTimeColumn.setCellValueFactory(
                            new PropertyValueFactory<MixingProxyCapsuleDBEntry, LocalDateTime>("startTime"));

                    TableColumn stopTimeColumn = new TableColumn("Stop Time");
                    stopTimeColumn.setMinWidth(100);
                    stopTimeColumn.setCellValueFactory(
                            new PropertyValueFactory<MixingProxyCapsuleDBEntry, LocalDateTime>("stopTime"));

                    ObservableList entries = FXCollections.observableArrayList(dbEntries);
                    capsuleTableView.getItems().clear();
                    capsuleTableView.getColumns().clear();
                    capsuleTableView.getColumns().addAll(tokenColumn, facilityKeyColumn, startTimeColumn, stopTimeColumn);
                    capsuleTableView.getItems().addAll(entries);
                }
            });
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
    public void handleFlushQueueButton(javafx.event.ActionEvent actionEvent) throws Exception {
        try {
            mixingProxyController.flushCapsules();
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
