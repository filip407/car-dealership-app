package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.AudiCar;
import model.ElectricAudiCar;
import service.DealershipService;

public class CarCatalogPanel {

    private final DealershipService service;
    private final ObservableList<AudiCar> items = FXCollections.observableArrayList();
    private ListView<AudiCar> carList;
    private TextField tfSearch;

    private Label lblId, lblType;
    private TextField tfModel, tfYear, tfPrice, tfColor;
    private TextField tfEngine, tfFuel, tfTransmission;
    private TextField tfBattery, tfRange, tfCharging;
    private VBox standardBox, electricBox;
    private Button saveBtn, deleteBtn;

    public CarCatalogPanel(DealershipService service) {
        this.service = service;
    }

    public Node getView() {
        carList = new ListView<>(items);
        carList.setCellFactory(lv -> new CarCell());
        carList.getSelectionModel().selectedItemProperty().addListener((o, old, sel) -> populate(sel));

        tfSearch = new TextField();
        tfSearch.setPromptText("Cauta dupa model...");
        tfSearch.textProperty().addListener((o, old, val) -> applyFilter(val));

        Label listTitle = new Label("Catalog complet");
        listTitle.getStyleClass().add("section-title");
        VBox listCard = new VBox(10, listTitle, tfSearch, carList);
        listCard.getStyleClass().add("card");
        VBox.setVgrow(carList, Priority.ALWAYS);
        HBox.setHgrow(listCard, Priority.ALWAYS);

        VBox editCard = buildEditForm();

        HBox layout = new HBox(18, listCard, editCard);
        layout.setPadding(new Insets(4));
        refresh();
        return layout;
    }

    private VBox buildEditForm() {
        Label title = new Label("Editeaza masina selectata");
        title.getStyleClass().add("section-title");

        lblId = new Label("Nicio masina selectata");
        lblId.getStyleClass().add("cell-price");
        lblType = new Label("");
        lblType.getStyleClass().add("field-label");

        tfModel = new TextField();
        tfYear = new TextField();
        tfPrice = new TextField();
        tfColor = new TextField();
        tfEngine = new TextField();
        tfFuel = new TextField();
        tfTransmission = new TextField();
        tfBattery = new TextField();
        tfRange = new TextField();
        tfCharging = new TextField();

        GridPane common = new GridPane();
        common.setHgap(12);
        common.setVgap(10);
        common.add(labeled("Model", tfModel), 0, 0);
        common.add(labeled("An fabricatie", tfYear), 1, 0);
        common.add(labeled("Pret (EUR)", tfPrice), 0, 1);
        common.add(labeled("Culoare", tfColor), 1, 1);

        GridPane sg = new GridPane();
        sg.setHgap(12);
        sg.setVgap(10);
        sg.add(labeled("Motor", tfEngine), 0, 0);
        sg.add(labeled("Combustibil", tfFuel), 1, 0);
        sg.add(labeled("Transmisie", tfTransmission), 0, 1);
        standardBox = new VBox(sg);

        GridPane eg = new GridPane();
        eg.setHgap(12);
        eg.setVgap(10);
        eg.add(labeled("Baterie (kWh)", tfBattery), 0, 0);
        eg.add(labeled("Autonomie (km)", tfRange), 1, 0);
        eg.add(labeled("Incarcare (ore)", tfCharging), 0, 1);
        electricBox = new VBox(eg);

        saveBtn = new Button("Salveaza modificarile");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setOnAction(e -> save());
        deleteBtn = new Button("Sterge masina din catalog");
        deleteBtn.getStyleClass().add("button-secondary");
        deleteBtn.setMaxWidth(Double.MAX_VALUE);
        deleteBtn.setOnAction(e -> delete());

        setFormEnabled(false);

        VBox form = new VBox(14, title, lblId, lblType, common, standardBox, electricBox, saveBtn, deleteBtn);
        form.getStyleClass().add("card");
        form.setPrefWidth(460);
        return form;
    }

    private void populate(AudiCar car) {
        if (car == null) {
            lblId.setText("Nicio masina selectata");
            lblType.setText("");
            setFormEnabled(false);
            return;
        }
        lblId.setText("ID: " + car.getCarId());
        tfModel.setText(car.getModel());
        tfYear.setText(String.valueOf(car.getYear()));
        tfPrice.setText(String.format("%.0f", car.getPrice()));
        tfColor.setText(car.getColor());

        boolean electric = car instanceof ElectricAudiCar;
        lblType.setText(electric ? "Tip: Electric" : "Tip: Standard");
        standardBox.setVisible(!electric);
        standardBox.setManaged(!electric);
        electricBox.setVisible(electric);
        electricBox.setManaged(electric);

        if (electric) {
            ElectricAudiCar ec = (ElectricAudiCar) car;
            tfBattery.setText(String.format("%.0f", ec.getBatteryCapacity()));
            tfRange.setText(String.valueOf(ec.getRange()));
            tfCharging.setText(String.valueOf(ec.getChargingTime()));
        } else {
            tfEngine.setText(car.getEngineSize());
            tfFuel.setText(car.getFuelType());
            tfTransmission.setText(car.getTransmission());
        }
        setFormEnabled(true);
    }

    private void save() {
        AudiCar sel = carList.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        try {
            int year = Integer.parseInt(tfYear.getText().trim());
            double price = Double.parseDouble(tfPrice.getText().trim());
            if (sel instanceof ElectricAudiCar) {
                double battery = Double.parseDouble(tfBattery.getText().trim());
                int range = Integer.parseInt(tfRange.getText().trim());
                int charging = Integer.parseInt(tfCharging.getText().trim());
                service.updateElectricCar(sel.getCarId(), tfModel.getText().trim(), year, price,
                        tfColor.getText().trim(), battery, range, charging);
            } else {
                service.updateStandardCar(sel.getCarId(), tfModel.getText().trim(), year, price,
                        tfEngine.getText().trim(), tfFuel.getText().trim(),
                        tfTransmission.getText().trim(), tfColor.getText().trim());
            }
            refresh();
            showInfo("Modificarile au fost salvate.");
        } catch (NumberFormatException ex) {
            showError("An, pret si campurile electrice trebuie sa fie numere valide.");
        }
    }

    private void delete() {
        AudiCar sel = carList.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Sigur stergi masina " + sel.getCarId() + " (" + sel.getModel() + ")?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirmare stergere");
        confirm.setHeaderText(null);
        confirm.showAndWait();
        if (confirm.getResult() == ButtonType.YES) {
            boolean removed = service.removeCar(sel.getCarId());
            if (removed) {
                refresh();
                populate(null);
            } else {
                showError("Masina nu poate fi stearsa deoarece are comenzi active asociate.");
            }
        }
    }

    private void applyFilter(String text) {
        if (text == null || text.isBlank()) {
            items.setAll(service.getInventory());
        } else {
            items.setAll(service.searchByModel(text.trim()));
        }
    }

    public void refresh() {
        applyFilter(tfSearch == null ? "" : tfSearch.getText());
    }

    private void setFormEnabled(boolean on) {
        saveBtn.setDisable(!on);
        deleteBtn.setDisable(!on);
    }

    private VBox labeled(String text, Control field) {
        Label l = new Label(text);
        l.getStyleClass().add("field-label");
        return new VBox(3, l, field);
    }

    private void showError(String msg) {
        alert(Alert.AlertType.WARNING, "Atentie", msg);
    }

    private void showInfo(String msg) {
        alert(Alert.AlertType.INFORMATION, "Succes", msg);
    }

    private void alert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
