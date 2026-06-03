package model;

public class Salesperson extends Employee {

    private double commissionRate;

    public Salesperson(String id, String firstName, String lastName, String phone, String email,
                       String employeeId, double salary, String hireDate, double commissionRate) {
        super(id, firstName, lastName, phone, email, employeeId, salary, hireDate);
        this.commissionRate = commissionRate;
    }

    public double getCommissionRate() { return commissionRate; }
    public void setCommissionRate(double commissionRate) { this.commissionRate = commissionRate; }

    public double calculateCommission(double salePrice) {
        return salePrice * (commissionRate / 100.0);
    }

    @Override
    public String getRole() { return "Vanzator"; }

    @Override
    public String toString() {
        return String.format("[%s] %s | Rol: %s | Comision: %.1f%%",
                getId(), getFullName(), getRole(), commissionRate);
    }
}
