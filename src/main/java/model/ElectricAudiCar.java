package model;

public class ElectricAudiCar extends AudiCar {

    private double batteryCapacity;
    private int range;
    private int chargingTime;

    public ElectricAudiCar(String carId, String model, int year, double price,
                            String color, double batteryCapacity, int range, int chargingTime) {
        super(carId, model, year, price, "Motor Electric", "Electric", "Automata", color);
        this.batteryCapacity = batteryCapacity;
        this.range = range;
        this.chargingTime = chargingTime;
    }

    public double getBatteryCapacity() { return batteryCapacity; }
    public void setBatteryCapacity(double batteryCapacity) { this.batteryCapacity = batteryCapacity; }

    public int getRange() { return range; }
    public void setRange(int range) { this.range = range; }

    public int getChargingTime() { return chargingTime; }
    public void setChargingTime(int chargingTime) { this.chargingTime = chargingTime; }

    @Override
    public String getCarDetails() {
        return String.format("Audi %s (%d) [ELECTRIC] | Baterie: %.0f kWh | Autonomie: %d km | Incarcare: %d ore | Culoare: %s | Pret: %.0f EUR",
                getModel(), getYear(), batteryCapacity, range, chargingTime, getColor(), getPrice());
    }
}
