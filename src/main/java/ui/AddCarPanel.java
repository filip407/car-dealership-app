package ui;

import factory.CarFactory;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.AudiCar;
import service.DealershipService;

public class AddCarPanel {

    private final DealershipService service;

    private TextField tfModel, tfYear, tfPrice, tfEngine, tfFuel, tfTransmission, tfColor;
    private TextField tfBattery, tfRange, tfCharging;
    private CheckBox cbElectric;
    private Label idHint;

    public AddCarPanel(DealershipService service) {
        this.service = service;
    }

    public Node getView() {
        Label title = new Label("Adauga o masina noua in catalog");
        title.getStyleClass().add("section-title");

        idHint = new Label("ID generat automat: " + service.generateCarId());
        idHint.getStyleClass().add("cell-price");

        tfModel = new TextField();
        tfYear = new TextField();
        tfPrice = new TextField();
        tfEngine = new TextField();
        tfFuel = new TextField();
        tfTransmission = new TextField();
        tfColor = new TextField();
        tfBattery = new TextField();
        tfRange = new TextField();
        tfCharging = new TextField();

        cbElectric = new CheckBox("Masina electrica");
        tfEngine.disableProperty().bind(cbElectric.selectedProperty());
        tfFuel.disableProperty().bind(cbElectric.selectedProperty());
        tfTransmission.disableProperty().bind(cbElectric.selectedProperty());
        tfBattery.disableProperty().bind(cbElectric.selectedProperty().not());
        tfRange.disableProperty().bind(cbElectric.selectedProperty().not());
        tfCharging.disableProperty().bind(cbElectric.selectedProperty().not());

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.add(labeled("Model", tfModel), 0, 0);
        grid.add(labeled("An fabricatie", tfYear), 1, 0);
        grid.add(labeled("Pret (EUR)", tfPrice), 0, 1);
        grid.add(labeled("Culoare", tfColor), 1, 1);
        grid.add(labeled("Motor", tfEngine), 0, 2);
        grid.add(labeled("Combustibil", tfFuel), 1, 2);
        grid.add(labeled("Transmisie", tfTransmission), 0, 3);
        grid.add(cbElectric, 0, 4, 2, 1);
        grid.add(labeled("Baterie (kWh)", tfBattery), 0, 5);
        grid.add(labeled("Autonomie (km)", tfRange), 1, 5);
        grid.add(labeled("Incarcare (ore)", tfCharging), 0, 6);

        Button addBtn = new Button("Adauga in catalog");
        addBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setOnAction(e -> addCar());

        Label hint = new Label("ID-ul masinii se genereaza automat. Bifeaza 'Masina electrica' pentru a completa specificatiile bateriei.");
        hint.getStyleClass().add("field-label");
        hint.setWrapText(true);

        VBox form = new VBox(14, title, idHint, grid, addBtn, hint);
        form.getStyleClass().add("card");
        form.setPrefWidth(520);
        form.setMaxWidth(560);

        HBox wrap = new HBox(form);
        wrap.setPadding(new Insets(4));
        return wrap;
    }

    private void addCar() {
        String model = tfModel.getText().trim();
        if (model.isEmpty()) {
            showError("Campul Model este obligatoriu.");
            return;
        }
        try {
            int year = Integer.parseInt(tfYear.getText().trim());
            double price = Double.parseDouble(tfPrice.getText().trim());
            String id = service.generateCarId();
            AudiCar car;
            if (cbElectric.isSelected()) {
                double battery = Double.parseDouble(tfBattery.getText().trim());
                int range = Integer.parseInt(tfRange.getText().trim());
                int charging = Integer.parseInt(tfCharging.getText().trim());
                car = CarFactory.createElectricCar(id, model, year, price, tfColor.getText().trim(), battery, range, charging);
            } else {
                car = CarFactory.createStandardCar(id, model, year, price,
                        tfEngine.getText().trim(), tfFuel.getText().trim(),
                        tfTransmission.getText().trim(), tfColor.getText().trim());
            }
            service.addCar(car);
            showInfo("Masina a fost adaugata cu ID-ul " + id + ".");
            clearForm();
            idHint.setText("ID generat automat: " + service.generateCarId());
        } catch (NumberFormatException ex) {
            showError("An, pret si campurile electrice trebuie sa fie numere valide.");
        }
    }

    private void clearForm() {
        tfModel.clear();
        tfYear.clear();
        tfPrice.clear();
        tfEngine.clear();
        tfFuel.clear();
        tfTransmission.clear();
        tfColor.clear();
        tfBattery.clear();
        tfRange.clear();
        tfCharging.clear();
        cbElectric.setSelected(false);
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
