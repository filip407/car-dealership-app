package ui;

import exception.CarNotAvailableException;
import exception.CustomerNotFoundException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.AudiCar;
import model.Customer;
import model.ServiceAppointment;
import model.TestDrive;
import service.DealershipService;

import java.time.LocalDate;

public class CustomerPanel {

    private final DealershipService service;

    private final ObservableList<AudiCar> availableItems = FXCollections.observableArrayList();
    private final ObservableList<AudiCar> availableForTd = FXCollections.observableArrayList();

    private ListView<AudiCar> purchaseCarList;
    private Label selectedLabel;
    private TextField tfFirst, tfLast, tfPhone, tfEmail, tfAddress;

    private ComboBox<AudiCar> tdCarCombo;
    private ComboBox<String> tdTimeCombo;
    private TextField tfTdFirst, tfTdLast, tfTdPhone;
    private DatePicker tdDatePicker;

    private TextField tfSvcCar;
    private TextField tfSvcFirst, tfSvcLast, tfSvcPhone;
    private DatePicker svcDatePicker;
    private TextField tfSvcDescription;

    private int webCounter = 1;
    private int tdCounter = 1;

    public CustomerPanel(DealershipService service) {
        this.service = service;
    }

    public Node getView() {
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab purchase = new Tab("Achizitie", buildPurchaseView());
        Tab testDrive = new Tab("Test Drive", buildTestDriveView());
        Tab svcTab = new Tab("Programare Service", buildServiceView());

        tabs.getTabs().addAll(purchase, testDrive, svcTab);

        tabs.getSelectionModel().selectedItemProperty().addListener((o, old, sel) -> {
            if (sel == testDrive) refreshTdCars();
        });

        refreshAvailable();
        refreshTdCars();

        StackPane wrap = new StackPane(tabs);
        wrap.getStyleClass().add("content-pane");
        return wrap;
    }

    private Node buildPurchaseView() {
        purchaseCarList = new ListView<>(availableItems);
        purchaseCarList.setCellFactory(lv -> new CarCell());
        purchaseCarList.getSelectionModel().selectedItemProperty().addListener((o, old, sel) -> {
            if (sel != null) {
                selectedLabel.setText(sel.getModel() + "  -  " + String.format("%,.0f EUR", sel.getPrice()));
            }
        });

        Label listTitle = new Label("Masini disponibile in showroom");
        listTitle.getStyleClass().add("section-title");
        VBox listCard = new VBox(10, listTitle, purchaseCarList);
        listCard.getStyleClass().add("card");
        VBox.setVgrow(purchaseCarList, Priority.ALWAYS);
        HBox.setHgrow(listCard, Priority.ALWAYS);

        VBox formCard = buildPurchaseForm();

        HBox layout = new HBox(18, listCard, formCard);
        layout.setPadding(new Insets(4));
        return layout;
    }

    private VBox buildPurchaseForm() {
        Label title = new Label("Formular de achizitie");
        title.getStyleClass().add("section-title");

        selectedLabel = new Label("Nicio masina selectata");
        selectedLabel.getStyleClass().add("cell-price");

        tfFirst = new TextField();
        tfLast = new TextField();
        tfPhone = new TextField();
        tfEmail = new TextField();
        tfAddress = new TextField();

        VBox fields = new VBox(10,
                labeled("Prenume", tfFirst),
                labeled("Nume", tfLast),
                labeled("Telefon", tfPhone),
                labeled("Email", tfEmail),
                labeled("Adresa", tfAddress));

        Button submit = new Button("Trimite cererea de achizitie");
        submit.setMaxWidth(Double.MAX_VALUE);
        submit.setOnAction(e -> submitPurchase());

        Label hint = new Label("Selecteaza o masina din stanga, apoi completeaza datele.");
        hint.getStyleClass().add("field-label");
        hint.setWrapText(true);

        VBox form = new VBox(14, title, selectedLabel, fields, submit, hint);
        form.getStyleClass().add("card");
        form.setPrefWidth(380);
        return form;
    }

    private Node buildTestDriveView() {
        Label title = new Label("Programare Test Drive");
        title.getStyleClass().add("section-title");

        tdCarCombo = new ComboBox<>(availableForTd);
        tdCarCombo.setMaxWidth(Double.MAX_VALUE);
        tdCarCombo.setPromptText("Selecteaza masina...");
        tdCarCombo.setCellFactory(lv -> new ListCell<AudiCar>() {
            @Override
            protected void updateItem(AudiCar car, boolean empty) {
                super.updateItem(car, empty);
                setText(empty || car == null ? null
                        : car.getModel() + " (" + car.getYear() + ")  -  " + String.format("%,.0f EUR", car.getPrice()));
            }
        });
        tdCarCombo.setButtonCell(new ListCell<AudiCar>() {
            @Override
            protected void updateItem(AudiCar car, boolean empty) {
                super.updateItem(car, empty);
                setText(empty || car == null ? "Selecteaza masina..." : car.getModel() + " (" + car.getYear() + ")");
            }
        });

        tdTimeCombo = new ComboBox<>(FXCollections.observableArrayList(
                "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"));
        tdTimeCombo.getSelectionModel().select("10:00");
        tdTimeCombo.setMaxWidth(Double.MAX_VALUE);

        tfTdFirst = new TextField();
        tfTdLast = new TextField();
        tfTdPhone = new TextField();
        tdDatePicker = new DatePicker(LocalDate.now().plusDays(1));
        tdDatePicker.setMaxWidth(Double.MAX_VALUE);

        Button submit = new Button("Programeaza Test Drive");
        submit.setMaxWidth(Double.MAX_VALUE);
        submit.setOnAction(e -> submitTestDrive());

        Label hint = new Label("Selecteaza masina dorita si completeaza datele. Vei fi contactat pentru confirmare.");
        hint.getStyleClass().add("field-label");
        hint.setWrapText(true);

        VBox form = new VBox(12, title,
                labeled("Masina dorita", tdCarCombo),
                labeled("Prenume", tfTdFirst),
                labeled("Nume", tfTdLast),
                labeled("Telefon", tfTdPhone),
                labeled("Data dorita", tdDatePicker),
                labeled("Ora dorita", tdTimeCombo),
                submit, hint);
        form.getStyleClass().add("card");
        form.setPrefWidth(460);

        HBox wrap = new HBox(form);
        wrap.setPadding(new Insets(4));
        return wrap;
    }

    private Node buildServiceView() {
        Label title = new Label("Programare Service");
        title.getStyleClass().add("section-title");

        tfSvcCar = new TextField();
        tfSvcCar.setPromptText("ex: Audi A4 2019, nr. B-123-ABC");
        tfSvcFirst = new TextField();
        tfSvcLast = new TextField();
        tfSvcPhone = new TextField();
        svcDatePicker = new DatePicker(LocalDate.now().plusDays(1));
        svcDatePicker.setMaxWidth(Double.MAX_VALUE);
        tfSvcDescription = new TextField();
        tfSvcDescription.setPromptText("Descrie problema...");

        Button submit = new Button("Trimite cererea de service");
        submit.setMaxWidth(Double.MAX_VALUE);
        submit.setOnAction(e -> submitService());

        Label hint = new Label("Completeaza datele si vei fi contactat de un mecanic pentru confirmare.");
        hint.getStyleClass().add("field-label");
        hint.setWrapText(true);

        VBox form = new VBox(12, title,
                labeled("Masina (marca, model, an, numar)", tfSvcCar),
                labeled("Prenume", tfSvcFirst),
                labeled("Nume", tfSvcLast),
                labeled("Telefon", tfSvcPhone),
                labeled("Data dorita", svcDatePicker),
                labeled("Descriere problema", tfSvcDescription),
                submit, hint);
        form.getStyleClass().add("card");
        form.setPrefWidth(460);

        HBox wrap = new HBox(form);
        wrap.setPadding(new Insets(4));
        return wrap;
    }

    private void submitPurchase() {
        AudiCar sel = purchaseCarList.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert(Alert.AlertType.WARNING, "Atentie", "Selecteaza o masina din lista din stanga.");
            return;
        }
        if (tfFirst.getText().trim().isEmpty() || tfLast.getText().trim().isEmpty() || tfPhone.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Atentie", "Completeaza prenumele, numele si telefonul.");
            return;
        }

        String custId = "WEB" + String.format("%03d", webCounter++);
        Customer customer = new Customer(custId, tfFirst.getText().trim(), tfLast.getText().trim(),
                tfPhone.getText().trim(), tfEmail.getText().trim(), tfAddress.getText().trim());
        service.addCustomer(customer);

        try {
            service.createOrder(custId, sel.getCarId(), "V001", LocalDate.now().toString());
            showAlert(Alert.AlertType.INFORMATION, "Cerere inregistrata",
                    "Multumim, " + customer.getFullName() + "!\n\n"
                            + "Cererea pentru " + sel.getModel() + " (" + String.format("%,.0f EUR", sel.getPrice()) + ") a fost inregistrata.\n"
                            + "Un consultant te va contacta la numarul " + customer.getPhone() + ".");
            refreshAvailable();
            clearPurchaseForm();
        } catch (CustomerNotFoundException | CarNotAvailableException ex) {
            showAlert(Alert.AlertType.ERROR, "Eroare", ex.getMessage());
        }
    }

    private void submitTestDrive() {
        AudiCar car = tdCarCombo.getValue();
        if (car == null) {
            showAlert(Alert.AlertType.WARNING, "Atentie", "Selecteaza o masina pentru test drive.");
            return;
        }
        if (tfTdFirst.getText().trim().isEmpty() || tfTdPhone.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Atentie", "Completeaza prenumele si telefonul.");
            return;
        }

        String custId = "TD" + String.format("%03d", tdCounter++);
        service.addCustomer(new Customer(custId, tfTdFirst.getText().trim(), tfTdLast.getText().trim(),
                tfTdPhone.getText().trim(), "", ""));

        String time = tdTimeCombo.getValue() != null ? tdTimeCombo.getValue() : "10:00";
        TestDrive td = new TestDrive(service.generateTestDriveId(), custId,
                car.getCarId(), tdDatePicker.getValue().toString(), time);
        service.scheduleTestDrive(td);

        showAlert(Alert.AlertType.INFORMATION, "Test Drive programat",
                "Test drive-ul pentru " + car.getModel() + " a fost programat pe "
                        + tdDatePicker.getValue() + " la ora " + time + ".\n"
                        + "Te vom contacta la " + tfTdPhone.getText().trim() + " pentru confirmare.");

        tfTdFirst.clear();
        tfTdLast.clear();
        tfTdPhone.clear();
        tdDatePicker.setValue(LocalDate.now().plusDays(1));
        tdTimeCombo.getSelectionModel().select("10:00");
        tdCarCombo.setValue(null);
    }

    private void submitService() {
        if (tfSvcCar.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Atentie", "Completeaza datele masinii.");
            return;
        }
        if (tfSvcFirst.getText().trim().isEmpty() || tfSvcPhone.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Atentie", "Completeaza prenumele si telefonul.");
            return;
        }

        String desc = "Client: " + tfSvcFirst.getText().trim() + " " + tfSvcLast.getText().trim()
                + " | Tel: " + tfSvcPhone.getText().trim()
                + " | Masina: " + tfSvcCar.getText().trim()
                + (tfSvcDescription.getText().trim().isEmpty() ? "" : " | " + tfSvcDescription.getText().trim());
        ServiceAppointment appt = new ServiceAppointment(service.generateServiceAppointmentId(),
                tfSvcCar.getText().trim(), "N/A", svcDatePicker.getValue().toString(), desc);
        service.addServiceAppointment(appt);

        showAlert(Alert.AlertType.INFORMATION, "Programare inregistrata",
                "Cererea de service a fost inregistrata pentru " + svcDatePicker.getValue() + ".\n"
                        + "Vei fi contactat la " + tfSvcPhone.getText().trim() + ".");

        tfSvcCar.clear();
        tfSvcFirst.clear();
        tfSvcLast.clear();
        tfSvcPhone.clear();
        svcDatePicker.setValue(LocalDate.now().plusDays(1));
        tfSvcDescription.clear();
    }

    private void refreshAvailable() {
        availableItems.setAll(service.getAvailableCars());
        if (selectedLabel != null) selectedLabel.setText("Nicio masina selectata");
    }

    private void refreshTdCars() {
        availableForTd.setAll(service.getAvailableCars());
    }

    private void clearPurchaseForm() {
        tfFirst.clear();
        tfLast.clear();
        tfPhone.clear();
        tfEmail.clear();
        tfAddress.clear();
    }

    private VBox labeled(String text, Control field) {
        Label l = new Label(text);
        l.getStyleClass().add("field-label");
        return new VBox(3, l, field);
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
