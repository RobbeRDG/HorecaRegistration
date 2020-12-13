package GUI;

import Controller.HelperObjects.RegistrarFacilityDBEntry;
import Controller.HelperObjects.RegistrarUserDBEntry;
import Controller.RegistrarController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;

public class AppController {
    private RegistrarController registrarController;

    @FXML
    TableView authenticatedUsersTableView;

    @FXML
    TableView authenticatedFacilitiesTableView;


    public void setRegistrarController(RegistrarController registrarController) {
        this.registrarController = registrarController;
    }

    public void showAuthenticatedUsers(ArrayList<RegistrarUserDBEntry> allRegisteredUsers) {
        try {
            Platform.runLater(new Runnable() {
                @Override public void run() {
                    TableColumn userIdentifierColumn = new TableColumn("User identifier");
                    userIdentifierColumn.setMinWidth(100);
                    userIdentifierColumn.setCellValueFactory(
                            new PropertyValueFactory<RegistrarUserDBEntry, String>("userIdentifier"));


                    ObservableList entries = FXCollections.observableArrayList(allRegisteredUsers);
                    authenticatedUsersTableView.getColumns().clear();
                    authenticatedUsersTableView.getItems().clear();
                    authenticatedUsersTableView.getColumns().addAll(userIdentifierColumn);
                    authenticatedUsersTableView.getItems().addAll(entries);
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

    public void showAuthenticatedFacilities(ArrayList<RegistrarFacilityDBEntry> allRegisteredFacilities) {
        TableColumn facilityIdentifierColumn = new TableColumn("Facility identifier");
        facilityIdentifierColumn.setMinWidth(100);
        facilityIdentifierColumn.setCellValueFactory(
                new PropertyValueFactory<RegistrarUserDBEntry, String>("facilityIdentifier"));


        ObservableList entries = FXCollections.observableArrayList(allRegisteredFacilities);
        authenticatedFacilitiesTableView.getColumns().clear();
        authenticatedFacilitiesTableView.getItems().clear();
        authenticatedFacilitiesTableView.getColumns().addAll(facilityIdentifierColumn);
        authenticatedFacilitiesTableView.getItems().addAll(entries);

        registrarController.refreshPrimaryStage();
    }
}
