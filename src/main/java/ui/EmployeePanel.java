package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.geometry.Insets;
import javafx.scene.layout.*;
import model.SaleOrder;
import service.DealershipService;

public class EmployeePanel {

    private final DealershipService service;
    private final ObservableList<SaleOrder> orderItems = FXCollections.observableArrayList();
    private Label lblTotal, lblAvailable, lblSold, lblOrders, lblRevenue;
    private CarCatalogPanel catalogPanel;

    public EmployeePanel(DealershipService service) {
        this.service = service;
    }

    public Node getView() {
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        catalogPanel = new CarCatalogPanel(service);
        EmployeeManagerPanel employeePanel = new EmployeeManagerPanel(service);
        CustomerHistoryPanel histPanel = new CustomerHistoryPanel(service);
        TestDrivePanel tdPanel = new TestDrivePanel(service);
        ServicePanel servicePanel = new ServicePanel(service);

        Tab addCar = new Tab("Adauga Masina", new AddCarPanel(service).getView());
        Tab catalog = new Tab("Catalog / Editare", catalogPanel.getView());
        Tab orders = new Tab("Comenzi", ordersTab());
        Tab stats = new Tab("Statistici", statsTab());
        Tab employees = new Tab("Angajati", employeePanel.getView());
        Tab history = new Tab("Istoric Clienti", histPanel.getView());
        Tab testDrives = new Tab("Test Drive", tdPanel.getView());
        Tab serviceTab = new Tab("Programari Service", servicePanel.getView());

        tabs.getTabs().addAll(addCar, catalog, orders, stats, employees, history, testDrives, serviceTab);

        tabs.getSelectionModel().selectedItemProperty().addListener((o, old, sel) -> {
            if (sel == orders) refreshOrders();
            if (sel == stats) refreshStats();
            if (sel == catalog) catalogPanel.refresh();
            if (sel == history) histPanel.refresh();
            if (sel == testDrives) tdPanel.refresh();
            if (sel == serviceTab) servicePanel.refresh();
            if (sel == employees) employeePanel.refresh();
        });

        StackPane wrap = new StackPane(tabs);
        wrap.getStyleClass().add("content-pane");
        return wrap;
    }

    private Node ordersTab() {
        Label title = new Label("Comenzi inregistrate");
        title.getStyleClass().add("section-title");

        ListView<SaleOrder> list = new ListView<>(orderItems);
        list.setCellFactory(lv -> new ListCell<SaleOrder>() {
            @Override
            protected void updateItem(SaleOrder order, boolean empty) {
                super.updateItem(order, empty);
                if (empty || order == null) { setText(null); setGraphic(null); return; }
                model.AudiCar car = service.getCarById(order.getCarId());
                String carName = car != null ? car.getModel() + " (" + order.getCarId() + ")" : order.getCarId();
                Label top = new Label(carName + "   |   Client: " + order.getCustomerId() + "   |   " + order.getDate());
                top.getStyleClass().add("cell-title");
                Label bot = new Label("Comanda: " + order.getOrderId() + "   |   Pret: " + String.format("%,.0f EUR", order.getFinalPrice()) + "   |   Status: " + order.getStatus());
                bot.getStyleClass().add("cell-detail");
                setText(null);
                setGraphic(new javafx.scene.layout.VBox(3, top, bot));
            }
        });
        refreshOrders();

        Button confirmBtn = new Button("Confirma comanda selectata");
        confirmBtn.setOnAction(e -> {
            SaleOrder sel = list.getSelectionModel().getSelectedItem();
            if (sel == null) { showError("Selecteaza o comanda din lista."); return; }
            if (!"In asteptare".equals(sel.getStatus())) {
                showError("Doar comenzile cu statusul 'In asteptare' pot fi confirmate.");
                return;
            }
            service.confirmOrder(sel.getOrderId());
            refreshOrders();
            catalogPanel.refresh();
        });

        Button cancel = new Button("Anuleaza comanda selectata");
        cancel.getStyleClass().add("button-secondary");
        cancel.setOnAction(e -> {
            SaleOrder sel = list.getSelectionModel().getSelectedItem();
            if (sel == null) { showError("Selecteaza o comanda din lista."); return; }
            service.cancelOrder(sel.getOrderId());
            refreshOrders();
            catalogPanel.refresh();
        });

        HBox buttons = new HBox(10, confirmBtn, cancel);
        VBox card = new VBox(12, title, list, buttons);
        card.getStyleClass().add("card");
        VBox.setVgrow(list, Priority.ALWAYS);
        return card;
    }

    private Node statsTab() {
        lblTotal = new Label("0");
        lblAvailable = new Label("0");
        lblSold = new Label("0");
        lblOrders = new Label("0");
        lblRevenue = new Label("0 EUR");

        GridPane grid = new GridPane();
        grid.setHgap(18);
        grid.setVgap(18);
        grid.add(statCard(lblTotal, "Masini in catalog"), 0, 0);
        grid.add(statCard(lblAvailable, "Disponibile"), 1, 0);
        grid.add(statCard(lblSold, "Vandute"), 2, 0);
        grid.add(statCard(lblOrders, "Comenzi totale"), 0, 1);
        grid.add(statCard(lblRevenue, "Venit total"), 1, 1, 2, 1);

        refreshStats();

        VBox card = new VBox(18, sectionLabel("Statistici vanzari"), grid);
        card.getStyleClass().add("card");
        return card;
    }

    private void refreshOrders() {
        orderItems.setAll(service.getAllOrders());
    }

    private void refreshStats() {
        int total = service.getInventory().size();
        int available = service.getAvailableCars().size();
        int sold = 0;
        for (model.AudiCar car : service.getInventory()) {
            if ("VANDUTA".equals(car.getCarStatus())) sold++;
        }
        int ordersCount = service.getAllOrders().size();
        double revenue = service.getTotalRevenue();

        lblTotal.setText(String.valueOf(total));
        lblAvailable.setText(String.valueOf(available));
        lblSold.setText(String.valueOf(sold));
        lblOrders.setText(String.valueOf(ordersCount));
        lblRevenue.setText(String.format("%,.0f EUR", revenue));
    }

    private VBox statCard(Label value, String text) {
        value.getStyleClass().add("stat-value");
        Label label = new Label(text);
        label.getStyleClass().add("stat-label");
        VBox box = new VBox(6, value, label);
        box.getStyleClass().add("stat-card");
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPrefWidth(200);
        return box;
    }

    private Label sectionLabel(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("section-title");
        return l;
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle("Atentie");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
