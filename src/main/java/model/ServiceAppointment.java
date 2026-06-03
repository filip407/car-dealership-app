package model;

public class ServiceAppointment {

    private String appointmentId;
    private String carId;
    private String mechanicId;
    private String date;
    private String description;
    private double cost;
    private String status;

    public ServiceAppointment(String appointmentId, String carId, String mechanicId,
                               String date, String description) {
        this.appointmentId = appointmentId;
        this.carId = carId;
        this.mechanicId = mechanicId;
        this.date = date;
        this.description = description;
        this.cost = 0;
        this.status = "Programat";
    }

    public String getAppointmentId() { return appointmentId; }
    public String getCarId() { return carId; }
    public String getMechanicId() { return mechanicId; }
    public String getDate() { return date; }
    public String getDescription() { return description; }

    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("[Service %s] Masina: %s | Mecanic: %s | Data: %s | Status: %s | Cost: %.0f RON",
                appointmentId, carId, mechanicId, date, status, cost);
    }
}
