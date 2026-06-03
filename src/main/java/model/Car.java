package model;

import interfaces.Displayable;

public abstract class Car implements Displayable {

    private String carId;
    private String model;
    private int year;
    private double price;
    private boolean available;
    private String carStatus;

    public Car(String carId, String model, int year, double price) {
        this.carId = carId;
        this.model = model;
        this.year = year;
        this.price = price;
        this.available = true;
        this.carStatus = "DISPONIBILA";
    }

    public String getCarId() { return carId; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public String getCarStatus() { return carStatus; }
    public void setCarStatus(String carStatus) { this.carStatus = carStatus; }

    public abstract String getCarDetails();

    @Override
    public String getDisplayInfo() {
        return getCarDetails();
    }

    @Override
    public String toString() {
        return String.format("[%s] Audi %s (%d) - %.0f EUR - %s",
                carId, model, year, price,
                available ? "Disponibila" : "Vanduta");
    }
}
