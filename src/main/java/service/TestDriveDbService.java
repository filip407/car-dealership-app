package service;

import database.DatabaseConnection;
import database.GenericDAO;
import model.TestDrive;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TestDriveDbService implements GenericDAO<TestDrive> {

    private static TestDriveDbService instance;

    private TestDriveDbService() {}

    public static TestDriveDbService getInstance() {
        if (instance == null) {
            instance = new TestDriveDbService();
        }
        return instance;
    }

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void save(TestDrive td) {
        String sql = "INSERT INTO test_drives (test_drive_id, customer_id, car_id, drive_date, drive_time, status, feedback) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, td.getTestDriveId());
            stmt.setString(2, td.getCustomerId());
            stmt.setString(3, td.getCarId());
            stmt.setString(4, td.getDate());
            stmt.setString(5, td.getTime());
            stmt.setString(6, td.getStatus());
            stmt.setString(7, td.getFeedback());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[DB ERROR] save test drive: " + e.getMessage());
        }
    }

    @Override
    public TestDrive findById(String id) {
        String sql = "SELECT * FROM test_drives WHERE test_drive_id = ?";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.out.println("[DB ERROR] findById test drive: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<TestDrive> findAll() {
        List<TestDrive> result = new ArrayList<>();
        String sql = "SELECT * FROM test_drives ORDER BY drive_date DESC";
        try (Statement stmt = getConn().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) result.add(mapRow(rs));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] findAll test drives: " + e.getMessage());
        }
        return result;
    }

    @Override
    public void update(TestDrive td) {
        String sql = "UPDATE test_drives SET status=?, feedback=? WHERE test_drive_id=?";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, td.getStatus());
            stmt.setString(2, td.getFeedback());
            stmt.setString(3, td.getTestDriveId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[DB ERROR] update test drive: " + e.getMessage());
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM test_drives WHERE test_drive_id = ?";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[DB ERROR] delete test drive: " + e.getMessage());
        }
    }

    private TestDrive mapRow(ResultSet rs) throws SQLException {
        String time = rs.getString("drive_time");
        TestDrive td = new TestDrive(
                rs.getString("test_drive_id"),
                rs.getString("customer_id"),
                rs.getString("car_id"),
                rs.getString("drive_date"),
                time != null ? time : ""
        );
        String status = rs.getString("status");
        if (status != null) td.setStatus(status);
        td.setFeedback(rs.getString("feedback"));
        return td;
    }
}
