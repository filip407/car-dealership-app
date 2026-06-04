package model;

public abstract class Employee extends Person {

    private String employeeId;
    private double salary;
    private String hireDate;

    public Employee(String id, String firstName, String lastName, String phone, String email,
                    String employeeId, double salary, String hireDate) {
        super(id, firstName, lastName, phone, email);
        this.employeeId = employeeId;
        this.salary = salary;
        this.hireDate = hireDate;
    }

    public String getEmployeeId() { return employeeId; }

    public double getSalary() { return salary; }

    public String getHireDate() { return hireDate; }

    public abstract String getRole();
}
