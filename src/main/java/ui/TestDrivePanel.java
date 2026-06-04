package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.AudiCar;
import model.TestDrive;
import service.DealershipService;

public class TestDrivePanel {

    private final DealershipService service;
    private final ObservableList<TestDrive> items = FXCollections.observableArrayList();
    private ListView<TestDrive> list;

    public TestDrivePanel(DealershipService service) {
        this.service = service;
    }

    public Node getView() {
        Label title = new Label("Programari Test Drive");
        title.getStyleClass().add("section-title");

        list = new ListView<>(items);
        list.setCellFactory(lv -> new ListCell<TestDrive>() {
            @Override
            protected void updateItem(TestDrive td, boolean empty) {
                super.updateItem(td, empty);
                if (empty || td == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }
                AudiCar car = service.getCarById(td.getCarId());
                String carName = car != null ? car.getModel() + " (" + td.getCarId() + ")" : td.getCarId();
                Label top = new Label(td.getDate() + "  la ora  " + td.getTime() + "   |   " + carName);
                top.getStyleClass().add("cell-title");
                Label bot = new Label("ID: " + td.getTestDriveId() + "   |   Client: " + td.getCustomerId());
                bot.getStyleClass().add("cell-detail");
                VBox left = new VBox(3, top, bot);

                Label status = new Label(td.getStatus());
                status.getStyleClass().add("Confirmat".equals(td.getStatus()) ? "badge-available" : "badge-sold");

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                HBox row = new HBox(12, left, spacer, status);
                row.setAlignment(Pos.CENTER_LEFT);
                setText(null);
                setGraphic(row);
            }
        });

        Button confirmBtn = new Button("Confirma selectata");
        confirmBtn.setOnAction(e -> confirm());

        Button deleteBtn = new Button("Sterge selectata");
        deleteBtn.getStyleClass().add("button-secondary");
        deleteBtn.setOnAction(e -> delete());

        HBox buttons = new HBox(10, confirmBtn, deleteBtn);

        VBox card = new VBox(12, title, list, buttons);
        card.getStyleClass().add("card");
        VBox.setVgrow(list, Priority.ALWAYS);
        refresh();
        return card;
    }

    private void confirm() {
        TestDrive sel = list.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showError("Selecteaza o programare din lista.");
            return;
        }
        service.confirmTestDrive(sel.getTestDriveId());
        refresh();
    }

    private void delete() {
        TestDrive sel = list.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showError("Selecteaza o programare din lista.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Sigur stergi programarea " + sel.getTestDriveId() + "?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirmare stergere");
        confirm.setHeaderText(null);
        confirm.showAndWait();
        if (confirm.getResult() == ButtonType.YES) {
            service.cancelTestDrive(sel.getTestDriveId());
            refresh();
        }
    }

    public void refresh() {
        items.setAll(service.getAllTestDrives());
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle("Atentie");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
