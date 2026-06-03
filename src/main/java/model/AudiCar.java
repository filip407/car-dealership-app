package model;

public class AudiCar extends Car implements Comparable<AudiCar> {

    private String engineSize;
    private String fuelType;
    private String transmission;
    private String color;

    public AudiCar(String carId, String model, int year, double price,
                   String engineSize, String fuelType, String transmission, String color) {
        super(carId, model, year, price);
        this.engineSize = engineSize;
        this.fuelType = fuelType;
        this.transmission = transmission;
        this.color = color;
    }

    public String getEngineSize() { return engineSize; }
    public void setEngineSize(String engineSize) { this.engineSize = engineSize; }

    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }

    public String getTransmission() { return transmission; }
    public void setTransmission(String transmission) { this.transmission = transmission; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    @Override
    public String getCarDetails() {
        return String.format("Audi %s (%d) | Motor: %s | Combustibil: %s | Transmisie: %s | Culoare: %s | Pret: %.0f EUR",
                getModel(), getYear(), engineSize, fuelType, transmission, color, getPrice());
    }

    @Override
    public int compareTo(AudiCar other) {
        int cmp = Double.compare(this.getPrice(), other.getPrice());
        if (cmp != 0) return cmp;
        return this.getCarId().compareTo(other.getCarId());
    }
}
