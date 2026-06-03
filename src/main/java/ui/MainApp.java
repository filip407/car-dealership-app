package ui;

import exception.CarNotAvailableException;
import exception.CustomerNotFoundException;
import factory.CarFactory;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Customer;
import model.Mechanic;
import model.Salesperson;
import service.DealershipService;

public class MainApp extends Application {

    private DealershipService service;
    private BorderPane root;
    private Label subtitle;

    @Override
    public void start(Stage stage) {
        service = new DealershipService();
        seedData();

        root = new BorderPane();

        MenuBar menuBar = buildMenuBar();
        VBox header = buildHeader();
        VBox top = new VBox(menuBar, header);
        root.setTop(top);

        showCustomerView();

        Scene scene = new Scene(root, 1150, 760);
        var css = getClass().getResource("/styles.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }

        stage.setTitle("Audi Center - Sistem de Management");
        stage.setScene(scene);
        stage.show();
    }

    private MenuBar buildMenuBar() {
        Menu nav = new Menu("Navigare");
        MenuItem angajat = new MenuItem("Modul Angajat");
        MenuItem client = new MenuItem("Modul Client");
        angajat.setOnAction(e -> promptEmployeeLogin());
        client.setOnAction(e -> showCustomerView());
        nav.getItems().addAll(angajat, client);

        Menu help = new Menu("Ajutor");
        MenuItem about = new MenuItem("Despre");
        about.setOnAction(e -> showAbout());
        MenuItem exit = new MenuItem("Iesire");
        exit.setOnAction(e -> Platform.exit());
        help.getItems().addAll(about, new SeparatorMenuItem(), exit);

        return new MenuBar(nav, help);
    }

    private VBox buildHeader() {
        Label title = new Label("AUDI CENTER");
        title.getStyleClass().add("header-title");
        subtitle = new Label();
        subtitle.getStyleClass().add("header-subtitle");
        VBox header = new VBox(4, title, subtitle);
        header.getStyleClass().add("header");
        return header;
    }

    private void promptEmployeeLogin() {
        javafx.scene.control.PasswordField pf = new javafx.scene.control.PasswordField();
        pf.setPromptText("Parola angajat");

        VBox content = new VBox(8, new Label("Introdu parola pentru modul angajat:"), pf);
        content.setPadding(new Insets(10));

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Autentificare");
        dialog.setHeaderText(null);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Node okBtn = dialog.getDialogPane().lookupButton(ButtonType.OK);
        okBtn.addEventFilter(javafx.event.ActionEvent.ACTION, ev -> {
            if (!"Admin123!".equals(pf.getText())) {
                ev.consume();
                pf.clear();
                pf.setStyle("-fx-border-color: red;");
            }
        });

        dialog.setResultConverter(btn -> btn == ButtonType.OK ? pf.getText() : null);
        dialog.showAndWait().ifPresent(pass -> {
            if ("Admin123!".equals(pass)) showEmployeeView();
        });
    }

    private void showEmployeeView() {
        subtitle.setText("Mod Angajat  -  gestiune catalog, comenzi si statistici");
        root.setCenter(new EmployeePanel(service).getView());
    }

    private void showCustomerView() {
        subtitle.setText("Mod Client  -  vezi masinile din stoc si trimite o cerere de achizitie");
        root.setCenter(new CustomerPanel(service).getView());
    }

    private void showAbout() {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Despre aplicatie");
        a.setHeaderText("Audi Center Management System");
        a.setContentText("Proiect POO II\n\nSistem de gestiune pentru un centru auto Audi.\n"
                + "Tehnologii: Java, JavaFX, JDBC + PostgreSQL.\n\n"
                + "Doua moduri: Angajat (administrare) si Client (vizualizare + achizitie).");
        a.showAndWait();
    }

    private void seedData() {
        if (!service.getInventory().isEmpty()) return;

        service.addEmployee(new Salesperson("EMP001", "Andrei", "Popescu", "0722111222", "andrei@audicenter.ro", "V001", 4000, "2020-03-15", 3.5));
        service.addEmployee(new Salesperson("EMP002", "Maria", "Ionescu", "0733222333", "maria@audicenter.ro", "V002", 4200, "2019-07-01", 4.0));
        service.addEmployee(new Mechanic("EMP003", "Ion", "Dumitrescu", "0744333444", "ion@audicenter.ro", "M001", 3500, "2021-01-10", "Motor si transmisie"));

        service.addCar(CarFactory.createStandardCar("CAR001", "A3 Sportback", 2023, 35000, "2.0 TDI", "Diesel", "Automata", "Negru"));
        service.addCar(CarFactory.createStandardCar("CAR002", "A4 Avant", 2022, 45000, "2.0 TFSI", "Benzina", "Automata", "Alb"));
        service.addCar(CarFactory.createStandardCar("CAR003", "Q5", 2024, 62000, "3.0 TDI", "Diesel", "Automata", "Gri"));
        service.addCar(CarFactory.createStandardCar("CAR004", "A6", 2023, 58000, "2.0 TDI", "Diesel", "Automata", "Albastru"));
        service.addCar(CarFactory.createStandardCar("CAR005", "RS6 Avant", 2024, 120000, "4.0 TFSI", "Benzina", "Automata", "Negru"));
        service.addCar(CarFactory.createStandardCar("CAR006", "Q3", 2022, 38000, "1.5 TFSI", "Benzina", "Manuala", "Rosu"));
        service.addCar(CarFactory.createElectricCar("CAR007", "e-tron GT", 2024, 95000, "Gri Quantum", 93.4, 488, 9));
        service.addCar(CarFactory.createElectricCar("CAR008", "Q8 e-tron", 2024, 85000, "Albastru Navarra", 114, 600, 10));

        service.addCustomer(new Customer("CLI001", "Alexandru", "Marin", "0755123456", "alex@gmail.com", "Str. Florilor 10, Bucuresti"));
        service.addCustomer(new Customer("CLI002", "Elena", "Constantin", "0766234567", "elena@yahoo.com", "Bd. Unirii 25, Cluj"));

        try {
            service.createOrder("CLI001", "CAR001", "V001", "2024-04-25");
            service.createOrder("CLI002", "CAR004", "V002", "2024-04-26");
        } catch (CustomerNotFoundException | CarNotAvailableException e) {
            System.out.println("[SEED] " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
