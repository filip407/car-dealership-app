package model;

public class Mechanic extends Employee {

    private String specialization;

    public Mechanic(String id, String firstName, String lastName, String phone, String email,
                    String employeeId, double salary, String hireDate, String specialization) {
        super(id, firstName, lastName, phone, email, employeeId, salary, hireDate);
        this.specialization = specialization;
    }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    @Override
    public String getRole() { return "Mecanic"; }

    @Override
    public String toString() {
        return String.format("[%s] %s | Rol: %s | Specializare: %s",
                getId(), getFullName(), getRole(), specialization);
    }
}
