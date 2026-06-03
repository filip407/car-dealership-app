package ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import model.AudiCar;
import model.ElectricAudiCar;

public class CarCell extends ListCell<AudiCar> {

    @Override
    protected void updateItem(AudiCar car, boolean empty) {
        super.updateItem(car, empty);
        if (empty || car == null) {
            setText(null);
            setGraphic(null);
            return;
        }

        Label title = new Label(car.getModel() + "  (" + car.getYear() + ")");
        title.getStyleClass().add("cell-title");

        boolean electric = car instanceof ElectricAudiCar;
        String engine = electric ? "Electric" : car.getFuelType();
        Label detail = new Label(car.getCarId() + "   |   " + engine + "   |   " + car.getColor());
        detail.getStyleClass().add("cell-detail");

        String specs;
        if (electric) {
            ElectricAudiCar ec = (ElectricAudiCar) car;
            specs = String.format("Baterie %.0f kWh   •   Autonomie %d km   •   Incarcare %d h",
                    ec.getBatteryCapacity(), ec.getRange(), ec.getChargingTime());
        } else {
            specs = "Motor " + car.getEngineSize() + "   •   Transmisie " + car.getTransmission();
        }
        Label specsLabel = new Label(specs);
        specsLabel.getStyleClass().add("cell-detail");

        VBox left = new VBox(3, title, detail, specsLabel);

        Label price = new Label(String.format("%,.0f EUR", car.getPrice()));
        price.getStyleClass().add("cell-price");
        String carStatus = car.getCarStatus() != null ? car.getCarStatus() : "DISPONIBILA";
        Label status = new Label(carStatus);
        String badgeStyle = switch (carStatus) {
            case "IN ASTEPTARE" -> "badge-pending";
            case "VANDUTA"      -> "badge-sold";
            default             -> "badge-available";
        };
        status.getStyleClass().add(badgeStyle);
        VBox right = new VBox(3, price, status);
        right.setAlignment(Pos.CENTER_RIGHT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(12, left, spacer, right);
        row.setAlignment(Pos.CENTER_LEFT);

        setText(null);
        setGraphic(row);
    }
}
