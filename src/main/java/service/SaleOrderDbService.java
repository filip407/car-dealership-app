package service;

import database.DatabaseConnection;
import database.GenericDAO;
import model.SaleOrder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SaleOrderDbService implements GenericDAO<SaleOrder> {

    private static SaleOrderDbService instance;

    private SaleOrderDbService() {}

    public static SaleOrderDbService getInstance() {
        if (instance == null) {
            instance = new SaleOrderDbService();
        }
        return instance;
    }

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void save(SaleOrder order) {
        String sql = "INSERT INTO sale_orders (order_id, customer_id, car_id, salesperson_id, order_date, final_price, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, order.getOrderId());
            stmt.setString(2, order.getCustomerId());
            stmt.setString(3, order.getCarId());
            stmt.setString(4, order.getSalespersonId());
            stmt.setString(5, order.getDate());
            stmt.setDouble(6, order.getFinalPrice());
            stmt.setString(7, order.getStatus());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[DB ERROR] save order: " + e.getMessage());
        }
    }

    @Override
    public SaleOrder findById(String id) {
        String sql = "SELECT * FROM sale_orders WHERE order_id = ?";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.out.println("[DB ERROR] findById order: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<SaleOrder> findAll() {
        List<SaleOrder> result = new ArrayList<>();
        String sql = "SELECT * FROM sale_orders ORDER BY order_date DESC";
        try (Statement stmt = getConn().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) result.add(mapRow(rs));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] findAll orders: " + e.getMessage());
        }
        return result;
    }

    @Override
    public void update(SaleOrder order) {
        String sql = "UPDATE sale_orders SET status=?, final_price=? WHERE order_id=?";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, order.getStatus());
            stmt.setDouble(2, order.getFinalPrice());
            stmt.setString(3, order.getOrderId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[DB ERROR] update order: " + e.getMessage());
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM sale_orders WHERE order_id = ?";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[DB ERROR] delete order: " + e.getMessage());
        }
    }

    private SaleOrder mapRow(ResultSet rs) throws SQLException {
        SaleOrder order = new SaleOrder(
                rs.getString("order_id"),
                rs.getString("customer_id"),
                rs.getString("car_id"),
                rs.getString("salesperson_id"),
                rs.getString("order_date"),
                rs.getDouble("final_price")
        );
        order.setStatus(rs.getString("status"));
        return order;
    }
}
