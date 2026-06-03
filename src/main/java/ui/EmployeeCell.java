package ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import model.Employee;
import model.Mechanic;
import model.Salesperson;

public class EmployeeCell extends ListCell<Employee> {

    @Override
    protected void updateItem(Employee emp, boolean empty) {
        super.updateItem(emp, empty);
        if (empty || emp == null) {
            setText(null);
            setGraphic(null);
            return;
        }

        Label name = new Label(emp.getFullName());
        name.getStyleClass().add("cell-title");
        Label detail = new Label(emp.getId() + "   |   Cod: " + emp.getEmployeeId() + "   |   " + emp.getEmail());
        detail.getStyleClass().add("cell-detail");
        VBox left = new VBox(3, name, detail);

        String extra;
        if (emp instanceof Salesperson) {
            Salesperson sp = (Salesperson) emp;
            extra = String.format("Comision %.1f%%", sp.getCommissionRate());
        } else if (emp instanceof Mechanic) {
            extra = ((Mechanic) emp).getSpecialization();
        } else {
            extra = "";
        }

        Label role = new Label(emp.getRole());
        role.getStyleClass().add(emp instanceof Salesperson ? "badge-available" : "badge-sold");
        Label extraLabel = new Label(extra);
        extraLabel.getStyleClass().add("cell-detail");
        VBox right = new VBox(3, role, extraLabel);
        right.setAlignment(Pos.CENTER_RIGHT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(12, left, spacer, right);
        row.setAlignment(Pos.CENTER_LEFT);

        setText(null);
        setGraphic(row);
    }
}
