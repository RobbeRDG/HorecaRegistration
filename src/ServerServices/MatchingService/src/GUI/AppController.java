package GUI;

import Controller.HelperObjects.MatchingServiceCapsuleDBEntry;
import Controller.MatchingServiceController;
import Controller.MatchingServiceControllerImpl;
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
    private MatchingServiceController matchingServiceController;

    @FXML
    TableView capsuleTableView;


    public void setMatchingServiceController(MatchingServiceControllerImpl matchingServiceController) {
        this.matchingServiceController = matchingServiceController;
    }

    public void showCapsules(ArrayList <MatchingServiceCapsuleDBEntry> dbEntries) {
        try {
            Platform.runLater(new Runnable() {
                @Override public void run() {
                    TableColumn tokenColumn = new TableColumn("Token");
                    tokenColumn.setMinWidth(100);
                    tokenColumn.setCellValueFactory(
                            new PropertyValueFactory<MatchingServiceCapsuleDBEntry, String>("token"));

                    TableColumn facilityKeyColumn = new TableColumn("FacilityKey");
                    facilityKeyColumn.setMinWidth(100);
                    facilityKeyColumn.setCellValueFactory(
                            new PropertyValueFactory<MatchingServiceCapsuleDBEntry, String>("facilityKey"));

                    TableColumn startTimeColumn = new TableColumn("Start time");
                    startTimeColumn.setMinWidth(200);
                    startTimeColumn.setCellValueFactory(
                            new PropertyValueFactory<MatchingServiceCapsuleDBEntry, LocalDateTime>("startTime"));

                    TableColumn stopTimeColumn = new TableColumn("Stop Time");
                    stopTimeColumn.setMinWidth(100);
                    stopTimeColumn.setCellValueFactory(
                            new PropertyValueFactory<MatchingServiceCapsuleDBEntry, LocalDateTime>("stopTime"));

                    TableColumn criticalColumn = new TableColumn("Critical");
                    criticalColumn.setMinWidth(100);
                    criticalColumn.setCellValueFactory(
                            new PropertyValueFactory<MatchingServiceCapsuleDBEntry, Boolean>("critical"));

                    TableColumn informedColumn = new TableColumn("Informed");
                    informedColumn.setMinWidth(200);
                    informedColumn.setCellValueFactory(
                            new PropertyValueFactory<MatchingServiceCapsuleDBEntry, Boolean>("informed"));


                    ObservableList entries = FXCollections.observableArrayList(dbEntries);
                    capsuleTableView.getItems().clear();
                    capsuleTableView.getColumns().clear();
                    capsuleTableView.getColumns().addAll(tokenColumn, facilityKeyColumn, startTimeColumn, stopTimeColumn, criticalColumn, informedColumn);
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
}
