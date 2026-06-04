package service;

import database.DatabaseConnection;
import database.GenericDAO;
import model.ServiceAppointment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceAppointmentDbService implements GenericDAO<ServiceAppointment> {

    private static ServiceAppointmentDbService instance;

    private ServiceAppointmentDbService() {}

    public static ServiceAppointmentDbService getInstance() {
        if (instance == null) {
            instance = new ServiceAppointmentDbService();
        }
        return instance;
    }

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void save(ServiceAppointment appt) {
        String sql = "INSERT INTO service_appointments (appointment_id, car_id, mechanic_id, appointment_date, description, cost, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, appt.getAppointmentId());
            stmt.setString(2, appt.getCarId());
            String mechId = appt.getMechanicId();
            if (mechId == null || mechId.isEmpty() || mechId.equals("N/A")) {
                stmt.setNull(3, Types.VARCHAR);
            } else {
                stmt.setString(3, mechId);
            }
            stmt.setString(4, appt.getDate());
            stmt.setString(5, appt.getDescription());
            stmt.setDouble(6, appt.getCost());
            stmt.setString(7, appt.getStatus());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[DB ERROR] save appointment: " + e.getMessage());
        }
    }

    @Override
    public ServiceAppointment findById(String id) {
        String sql = "SELECT * FROM service_appointments WHERE appointment_id = ?";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.out.println("[DB ERROR] findById appointment: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<ServiceAppointment> findAll() {
        List<ServiceAppointment> result = new ArrayList<>();
        String sql = "SELECT * FROM service_appointments ORDER BY appointment_date DESC";
        try (Statement stmt = getConn().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) result.add(mapRow(rs));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] findAll appointments: " + e.getMessage());
        }
        return result;
    }

    @Override
    public void update(ServiceAppointment appt) {
        String sql = "UPDATE service_appointments SET status=?, cost=?, description=? WHERE appointment_id=?";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, appt.getStatus());
            stmt.setDouble(2, appt.getCost());
            stmt.setString(3, appt.getDescription());
            stmt.setString(4, appt.getAppointmentId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[DB ERROR] update appointment: " + e.getMessage());
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM service_appointments WHERE appointment_id = ?";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[DB ERROR] delete appointment: " + e.getMessage());
        }
    }

    private ServiceAppointment mapRow(ResultSet rs) throws SQLException {
        ServiceAppointment appt = new ServiceAppointment(
                rs.getString("appointment_id"),
                rs.getString("car_id"),
                rs.getString("mechanic_id"),
                rs.getString("appointment_date"),
                rs.getString("description")
        );
        appt.setCost(rs.getDouble("cost"));
        appt.setStatus(rs.getString("status"));
        return appt;
    }
}
