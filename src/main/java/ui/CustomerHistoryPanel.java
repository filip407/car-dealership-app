package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.Customer;
import model.SaleOrder;
import service.DealershipService;

public class CustomerHistoryPanel {

    private final DealershipService service;
    private final ObservableList<Customer> customers = FXCollections.observableArrayList();
    private final ObservableList<SaleOrder> orders = FXCollections.observableArrayList();
    private ListView<Customer> customerList;
    private ListView<SaleOrder> orderList;
    private Label info;

    public CustomerHistoryPanel(DealershipService service) {
        this.service = service;
    }

    public Node getView() {
        customerList = new ListView<>(customers);
        customerList.setCellFactory(lv -> new CustomerCell());
        customerList.getSelectionModel().selectedItemProperty().addListener((o, old, sel) -> showHistory(sel));

        Label listTitle = new Label("Clienti");
        listTitle.getStyleClass().add("section-title");
        VBox listCard = new VBox(10, listTitle, customerList);
        listCard.getStyleClass().add("card");
        VBox.setVgrow(customerList, Priority.ALWAYS);
        HBox.setHgrow(listCard, Priority.ALWAYS);

        Label histTitle = new Label("Istoric comenzi");
        histTitle.getStyleClass().add("section-title");
        info = new Label("Selecteaza un client din stanga.");
        info.getStyleClass().add("field-label");
        info.setWrapText(true);
        orderList = new ListView<>(orders);
        VBox histCard = new VBox(10, histTitle, info, orderList);
        histCard.getStyleClass().add("card");
        VBox.setVgrow(orderList, Priority.ALWAYS);
        HBox.setHgrow(histCard, Priority.ALWAYS);

        HBox layout = new HBox(18, listCard, histCard);
        layout.setPadding(new Insets(4));
        refresh();
        return layout;
    }

    private void showHistory(Customer c) {
        if (c == null) {
            info.setText("Selecteaza un client din stanga.");
            orders.clear();
            return;
        }
        info.setText(c.getFullName() + "   |   " + c.getPhone() + "   |   " + c.getEmail()
                + "\nAdresa: " + c.getAddress());
        orders.setAll(service.getOrdersByCustomer(c.getId()));
    }

    public void refresh() {
        customers.setAll(service.getAllCustomers());
    }
}
