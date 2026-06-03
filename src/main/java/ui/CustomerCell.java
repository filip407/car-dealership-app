package ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import model.Customer;

public class CustomerCell extends ListCell<Customer> {

    @Override
    protected void updateItem(Customer customer, boolean empty) {
        super.updateItem(customer, empty);
        if (empty || customer == null) {
            setText(null);
            setGraphic(null);
            return;
        }

        Label name = new Label(customer.getFullName());
        name.getStyleClass().add("cell-title");
        Label detail = new Label(customer.getId() + "   |   " + customer.getPhone());
        detail.getStyleClass().add("cell-detail");
        VBox left = new VBox(3, name, detail);

        Label count = new Label(customer.getOrderIds().size() + " comenzi");
        count.getStyleClass().add("cell-detail");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(12, left, spacer, count);
        row.setAlignment(Pos.CENTER_LEFT);

        setText(null);
        setGraphic(row);
    }
}
