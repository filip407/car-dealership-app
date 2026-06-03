package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.Employee;
import model.Mechanic;
import model.Salesperson;
import service.DealershipService;

public class EmployeeManagerPanel {

    private final DealershipService service;
    private final ObservableList<Employee> items = FXCollections.observableArrayList();
    private ListView<Employee> empList;

    private TextField tfFirst, tfLast, tfPhone, tfEmail, tfSalary, tfHireDate;
    private TextField tfCommission, tfSpecialization;
    private ComboBox<String> cbRole;
    private VBox salesBox, mechBox;

    public EmployeeManagerPanel(DealershipService service) {
        this.service = service;
    }

    public Node getView() {
        empList = new ListView<>(items);
        empList.setCellFactory(lv -> new EmployeeCell());

        Label listTitle = new Label("Angajati");
        listTitle.getStyleClass().add("section-title");
        Button deleteBtn = new Button("Sterge angajatul selectat");
        deleteBtn.getStyleClass().add("button-secondary");
        deleteBtn.setOnAction(e -> deleteSelected());
        VBox listCard = new VBox(10, listTitle, empList, deleteBtn);
        listCard.getStyleClass().add("card");
        VBox.setVgrow(empList, Priority.ALWAYS);
        HBox.setHgrow(listCard, Priority.ALWAYS);

        VBox formCard = buildForm();

        HBox layout = new HBox(18, listCard, formCard);
        layout.setPadding(new Insets(4));
        refresh();
        return layout;
    }

    private VBox buildForm() {
        Label title = new Label("Adauga angajat");
        title.getStyleClass().add("section-title");

        tfFirst = new TextField();
        tfLast = new TextField();
        tfPhone = new TextField();
        tfEmail = new TextField();
        tfSalary = new TextField();
        tfHireDate = new TextField();
        tfHireDate.setPromptText("AAAA-LL-ZZ");
        tfCommission = new TextField();
        tfSpecialization = new TextField();

        cbRole = new ComboBox<>(FXCollections.observableArrayList("Vanzator", "Mecanic"));
        cbRole.getSelectionModel().select("Vanzator");
        cbRole.setMaxWidth(Double.MAX_VALUE);
        cbRole.valueProperty().addListener((o, old, val) -> updateRoleFields(val));

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.add(labeled("Prenume", tfFirst), 0, 0);
        grid.add(labeled("Nume", tfLast), 1, 0);
        grid.add(labeled("Telefon", tfPhone), 0, 1);
        grid.add(labeled("Email", tfEmail), 1, 1);
        grid.add(labeled("Salariu (EUR)", tfSalary), 0, 2);
        grid.add(labeled("Data angajarii", tfHireDate), 1, 2);
        grid.add(labeled("Rol", cbRole), 0, 3, 2, 1);

        salesBox = labeled("Comision (%)", tfCommission);
        mechBox = labeled("Specializare", tfSpecialization);

        Button addBtn = new Button("Adauga angajat");
        addBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setOnAction(e -> addEmployee());

        VBox form = new VBox(14, title, grid, salesBox, mechBox, addBtn);
        form.getStyleClass().add("card");
        form.setPrefWidth(420);
        updateRoleFields("Vanzator");
        return form;
    }

    private void updateRoleFields(String role) {
        boolean sales = "Vanzator".equals(role);
        salesBox.setVisible(sales);
        salesBox.setManaged(sales);
        mechBox.setVisible(!sales);
        mechBox.setManaged(!sales);
    }

    private void addEmployee() {
        String first = tfFirst.getText().trim();
        String last = tfLast.getText().trim();
        if (first.isEmpty() || last.isEmpty()) {
            showError("Prenumele si numele sunt obligatorii.");
            return;
        }
        try {
            double salary = Double.parseDouble(tfSalary.getText().trim());
            String id = service.generateEmployeeId();
            String hire = tfHireDate.getText().trim();
            Employee emp;
            if ("Vanzator".equals(cbRole.getValue())) {
                double commission = Double.parseDouble(tfCommission.getText().trim());
                emp = new Salesperson(id, first, last, tfPhone.getText().trim(), tfEmail.getText().trim(),
                        service.generateSalespersonCode(), salary, hire, commission);
            } else {
                emp = new Mechanic(id, first, last, tfPhone.getText().trim(), tfEmail.getText().trim(),
                        service.generateMechanicCode(), salary, hire, tfSpecialization.getText().trim());
            }
            service.addEmployee(emp);
            refresh();
            clearForm();
            showInfo("Angajat adaugat cu ID-ul " + id + ".");
        } catch (NumberFormatException ex) {
            showError("Salariul si comisionul trebuie sa fie numere valide.");
        }
    }

    private void deleteSelected() {
        Employee sel = empList.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showError("Selecteaza un angajat din lista.");
            return;
        }
        service.removeEmployee(sel.getId());
        refresh();
    }

    public void refresh() {
        items.setAll(service.getAllEmployees());
    }

    private void clearForm() {
        tfFirst.clear();
        tfLast.clear();
        tfPhone.clear();
        tfEmail.clear();
        tfSalary.clear();
        tfHireDate.clear();
        tfCommission.clear();
        tfSpecialization.clear();
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
