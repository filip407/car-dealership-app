package factory;

import model.AudiCar;
import model.ElectricAudiCar;

public class CarFactory {

    public static AudiCar createStandardCar(String carId, String model, int year, double price,
                                             String engineSize, String fuelType,
                                             String transmission, String color) {
        return new AudiCar(carId, model, year, price, engineSize, fuelType, transmission, color);
    }

    public static ElectricAudiCar createElectricCar(String carId, String model, int year, double price,
                                                     String color, double batteryCapacity,
                                                     int range, int chargingTime) {
        return new ElectricAudiCar(carId, model, year, price, color, batteryCapacity, range, chargingTime);
    }
}
