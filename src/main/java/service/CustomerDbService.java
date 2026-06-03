package service;

import database.DatabaseConnection;
import database.GenericDAO;
import model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDbService implements GenericDAO<Customer> {

    private static CustomerDbService instance;

    private CustomerDbService() {}

    public static CustomerDbService getInstance() {
        if (instance == null) {
            instance = new CustomerDbService();
        }
        return instance;
    }

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void save(Customer customer) {
        String sql = "INSERT INTO customers (customer_id, first_name, last_name, phone, email, address) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, customer.getId());
            stmt.setString(2, customer.getFirstName());
            stmt.setString(3, customer.getLastName());
            stmt.setString(4, customer.getPhone());
            stmt.setString(5, customer.getEmail());
            stmt.setString(6, customer.getAddress());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[DB ERROR] save customer: " + e.getMessage());
        }
    }

    @Override
    public Customer findById(String id) {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.out.println("[DB ERROR] findById customer: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Customer> findAll() {
        List<Customer> result = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY last_name";
        try (Statement stmt = getConn().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) result.add(mapRow(rs));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] findAll customers: " + e.getMessage());
        }
        return result;
    }

    @Override
    public void update(Customer customer) {
        String sql = "UPDATE customers SET first_name=?, last_name=?, phone=?, email=?, address=? WHERE customer_id=?";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, customer.getFirstName());
            stmt.setString(2, customer.getLastName());
            stmt.setString(3, customer.getPhone());
            stmt.setString(4, customer.getEmail());
            stmt.setString(5, customer.getAddress());
            stmt.setString(6, customer.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[DB ERROR] update customer: " + e.getMessage());
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[DB ERROR] delete customer: " + e.getMessage());
        }
    }

    private Customer mapRow(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getString("customer_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("phone"),
                rs.getString("email"),
                rs.getString("address")
        );
    }
}
