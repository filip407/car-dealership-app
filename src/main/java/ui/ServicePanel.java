package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.AudiCar;
import model.Employee;
import model.ServiceAppointment;
import service.DealershipService;

import java.time.LocalDate;

public class ServicePanel {

    private final DealershipService service;
    private final ObservableList<ServiceAppointment> items = FXCollections.observableArrayList();
    private final ObservableList<AudiCar> cars = FXCollections.observableArrayList();
    private final ObservableList<Employee> mechanics = FXCollections.observableArrayList();

    private ListView<ServiceAppointment> list;
    private ComboBox<AudiCar> cbCar;
    private ComboBox<Employee> cbMechanic;
    private DatePicker datePicker;
    private TextField tfDescription, tfCost;

    public ServicePanel(DealershipService service) {
        this.service = service;
    }

    public Node getView() {
        list = new ListView<>(items);
        list.setCellFactory(lv -> new ListCell<ServiceAppointment>() {
            @Override
            protected void updateItem(ServiceAppointment sa, boolean empty) {
                super.updateItem(sa, empty);
                if (empty || sa == null) { setText(null); setGraphic(null); return; }
                String carName;
                if (sa.getCarId() == null) {
                    String desc = sa.getDescription() != null ? sa.getDescription() : "";
                    int idx = desc.indexOf("Masina: ");
                    if (idx >= 0) {
                        String after = desc.substring(idx + 8);
                        int pipe = after.indexOf(" | ");
                        carName = pipe >= 0 ? after.substring(0, pipe) : after;
                    } else {
                        carName = "(cerere externa)";
                    }
                } else {
                    AudiCar car = service.getCarById(sa.getCarId());
                    carName = car != null
                            ? car.getModel() + " (" + sa.getCarId() + ")"
                            : sa.getCarId();
                }
                String mechanicDisplay = sa.getMechanicId() != null ? sa.getMechanicId() : "Neatribuit";
                Label top = new Label(sa.getDate() + "   |   " + carName);
                top.getStyleClass().add("cell-title");
                Label bot = new Label("ID: " + sa.getAppointmentId()
                        + "   |   Mecanic: " + mechanicDisplay
                        + "   |   Status: " + sa.getStatus()
                        + (sa.getCost() > 0 ? "   |   Cost: " + String.format("%.0f RON", sa.getCost()) : ""));
                bot.getStyleClass().add("cell-detail");
                setText(null);
                setGraphic(new VBox(3, top, bot));
            }
        });

        Label listTitle = new Label("Programari service");
        listTitle.getStyleClass().add("section-title");

        tfCost = new TextField();
        tfCost.setPromptText("Cost (RON)");
        Button complete = new Button("Finalizeaza (cu cost)");
        complete.getStyleClass().add("button-secondary");
        complete.setOnAction(e -> complete());
        HBox completeRow = new HBox(8, tfCost, complete);
        HBox.setHgrow(tfCost, Priority.ALWAYS);

        Button cancelBtn = new Button("Anuleaza programarea selectata");
        cancelBtn.getStyleClass().add("button-secondary");
        cancelBtn.setMaxWidth(Double.MAX_VALUE);
        cancelBtn.setOnAction(e -> cancel());

        VBox listCard = new VBox(10, listTitle, list, completeRow, cancelBtn);
        listCard.getStyleClass().add("card");
        VBox.setVgrow(list, Priority.ALWAYS);
        HBox.setHgrow(listCard, Priority.ALWAYS);

        VBox formCard = buildForm();

        HBox layout = new HBox(18, listCard, formCard);
        layout.setPadding(new Insets(4));
        refresh();
        return layout;
    }

    private VBox buildForm() {
        Label title = new Label("Programare noua");
        title.getStyleClass().add("section-title");

        cbCar = new ComboBox<>(cars);
        cbCar.setMaxWidth(Double.MAX_VALUE);
        cbMechanic = new ComboBox<>(mechanics);
        cbMechanic.setMaxWidth(Double.MAX_VALUE);
        datePicker = new DatePicker(LocalDate.now());
        datePicker.setMaxWidth(Double.MAX_VALUE);
        tfDescription = new TextField();

        Button addBtn = new Button("Adauga programare");
        addBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setOnAction(e -> addAppointment());

        VBox form = new VBox(12,
                title,
                labeled("Masina", cbCar),
                labeled("Mecanic", cbMechanic),
                labeled("Data", datePicker),
                labeled("Descriere", tfDescription),
                addBtn);
        form.getStyleClass().add("card");
        form.setPrefWidth(380);
        return form;
    }

    private void addAppointment() {
        AudiCar car = cbCar.getValue();
        Employee mech = cbMechanic.getValue();
        if (car == null || mech == null) {
            showError("Selecteaza masina si mecanicul.");
            return;
        }
        String id = service.generateServiceAppointmentId();
        ServiceAppointment appt = new ServiceAppointment(id, car.getCarId(), mech.getEmployeeId(),
                datePicker.getValue().toString(), tfDescription.getText().trim());
        service.addServiceAppointment(appt);
        refresh();
        tfDescription.clear();
        showInfo("Programare adaugata cu ID-ul " + id + ".");
    }

    private void complete() {
        ServiceAppointment sel = list.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showError("Selecteaza o programare din lista.");
            return;
        }
        try {
            double cost = Double.parseDouble(tfCost.getText().trim());
            service.completeServiceAppointment(sel.getAppointmentId(), cost);
            refresh();
            tfCost.clear();
        } catch (NumberFormatException ex) {
            showError("Introdu un cost valid.");
        }
    }

    private void cancel() {
        ServiceAppointment sel = list.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showError("Selecteaza o programare din lista.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Sigur anulezi programarea " + sel.getAppointmentId() + "?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirmare anulare");
        confirm.setHeaderText(null);
        confirm.showAndWait();
        if (confirm.getResult() == ButtonType.YES) {
            service.cancelServiceAppointment(sel.getAppointmentId());
            refresh();
        }
    }

    public void refresh() {
        items.setAll(service.getAllServiceAppointments());
        cars.setAll(service.getInventory());
        mechanics.setAll(service.getEmployeesByRole("Mecanic"));
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
