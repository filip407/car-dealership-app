package service;

import database.DatabaseConnection;
import database.GenericDAO;
import model.Employee;
import model.Mechanic;
import model.Salesperson;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDbService implements GenericDAO<Employee> {

    private static EmployeeDbService instance;

    private EmployeeDbService() {}

    public static EmployeeDbService getInstance() {
        if (instance == null) {
            instance = new EmployeeDbService();
        }
        return instance;
    }

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void save(Employee employee) {
        String sql = "INSERT INTO employees (person_id, first_name, last_name, phone, email, employee_code, salary, hire_date, employee_type, commission_rate, specialization) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, employee.getId());
            stmt.setString(2, employee.getFirstName());
            stmt.setString(3, employee.getLastName());
            stmt.setString(4, employee.getPhone());
            stmt.setString(5, employee.getEmail());
            stmt.setString(6, employee.getEmployeeId());
            stmt.setDouble(7, employee.getSalary());
            stmt.setString(8, employee.getHireDate());
            if (employee instanceof Salesperson) {
                Salesperson sp = (Salesperson) employee;
                stmt.setString(9, "SALESPERSON");
                stmt.setDouble(10, sp.getCommissionRate());
                stmt.setNull(11, Types.VARCHAR);
            } else {
                Mechanic m = (Mechanic) employee;
                stmt.setString(9, "MECHANIC");
                stmt.setNull(10, Types.DOUBLE);
                stmt.setString(11, m.getSpecialization());
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[DB ERROR] save employee: " + e.getMessage());
        }
    }

    @Override
    public Employee findById(String id) {
        String sql = "SELECT * FROM employees WHERE person_id = ?";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.out.println("[DB ERROR] findById employee: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Employee> findAll() {
        List<Employee> result = new ArrayList<>();
        String sql = "SELECT * FROM employees ORDER BY last_name";
        try (Statement stmt = getConn().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) result.add(mapRow(rs));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] findAll employees: " + e.getMessage());
        }
        return result;
    }

    @Override
    public void update(Employee employee) {
        String sql = "UPDATE employees SET first_name=?, last_name=?, phone=?, email=?, salary=? WHERE person_id=?";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, employee.getFirstName());
            stmt.setString(2, employee.getLastName());
            stmt.setString(3, employee.getPhone());
            stmt.setString(4, employee.getEmail());
            stmt.setDouble(5, employee.getSalary());
            stmt.setString(6, employee.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[DB ERROR] update employee: " + e.getMessage());
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM employees WHERE person_id = ?";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[DB ERROR] delete employee: " + e.getMessage());
        }
    }

    private Employee mapRow(ResultSet rs) throws SQLException {
        String personId   = rs.getString("person_id");
        String firstName  = rs.getString("first_name");
        String lastName   = rs.getString("last_name");
        String phone      = rs.getString("phone");
        String email      = rs.getString("email");
        String empCode    = rs.getString("employee_code");
        double salary     = rs.getDouble("salary");
        String hireDate   = rs.getString("hire_date");
        String type       = rs.getString("employee_type");

        if ("SALESPERSON".equals(type)) {
            return new Salesperson(personId, firstName, lastName, phone, email,
                    empCode, salary, hireDate, rs.getDouble("commission_rate"));
        } else {
            return new Mechanic(personId, firstName, lastName, phone, email,
                    empCode, salary, hireDate, rs.getString("specialization"));
        }
    }
}
