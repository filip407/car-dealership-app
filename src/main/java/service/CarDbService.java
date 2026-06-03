package service;

import database.DatabaseConnection;
import database.GenericDAO;
import model.AudiCar;
import model.ElectricAudiCar;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarDbService implements GenericDAO<AudiCar> {

    private static CarDbService instance;

    private CarDbService() {}

    public static CarDbService getInstance() {
        if (instance == null) {
            instance = new CarDbService();
        }
        return instance;
    }

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void save(AudiCar car) {
        String sql = "INSERT INTO cars (car_id, model, year, price, available, car_status, engine_size, fuel_type, transmission, color, car_type, battery_capacity, range_km, charging_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, car.getCarId());
            stmt.setString(2, car.getModel());
            stmt.setInt(3, car.getYear());
            stmt.setDouble(4, car.getPrice());
            stmt.setBoolean(5, car.isAvailable());
            stmt.setString(6, car.getCarStatus());
            stmt.setString(7, car.getEngineSize());
            stmt.setString(8, car.getFuelType());
            stmt.setString(9, car.getTransmission());
            stmt.setString(10, car.getColor());
            if (car instanceof ElectricAudiCar) {
                ElectricAudiCar e = (ElectricAudiCar) car;
                stmt.setString(11, "ELECTRIC");
                stmt.setDouble(12, e.getBatteryCapacity());
                stmt.setInt(13, e.getRange());
                stmt.setInt(14, e.getChargingTime());
            } else {
                stmt.setString(11, "STANDARD");
                stmt.setNull(12, Types.DOUBLE);
                stmt.setNull(13, Types.INTEGER);
                stmt.setNull(14, Types.INTEGER);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[DB ERROR] save car: " + e.getMessage());
        }
    }

    @Override
    public AudiCar findById(String id) {
        String sql = "SELECT * FROM cars WHERE car_id = ?";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.out.println("[DB ERROR] findById car: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<AudiCar> findAll() {
        List<AudiCar> result = new ArrayList<>();
        String sql = "SELECT * FROM cars ORDER BY price";
        try (Statement stmt = getConn().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) result.add(mapRow(rs));
        } catch (SQLException e) {
            System.out.println("[DB ERROR] findAll cars: " + e.getMessage());
        }
        return result;
    }

    @Override
    public void update(AudiCar car) {
        String sql = "UPDATE cars SET model=?, year=?, price=?, available=?, car_status=?, engine_size=?, fuel_type=?, transmission=?, color=?, battery_capacity=?, range_km=?, charging_time=? WHERE car_id=?";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, car.getModel());
            stmt.setInt(2, car.getYear());
            stmt.setDouble(3, car.getPrice());
            stmt.setBoolean(4, car.isAvailable());
            stmt.setString(5, car.getCarStatus());
            stmt.setString(6, car.getEngineSize());
            stmt.setString(7, car.getFuelType());
            stmt.setString(8, car.getTransmission());
            stmt.setString(9, car.getColor());
            if (car instanceof ElectricAudiCar) {
                ElectricAudiCar e = (ElectricAudiCar) car;
                stmt.setDouble(10, e.getBatteryCapacity());
                stmt.setInt(11, e.getRange());
                stmt.setInt(12, e.getChargingTime());
            } else {
                stmt.setNull(10, Types.DOUBLE);
                stmt.setNull(11, Types.INTEGER);
                stmt.setNull(12, Types.INTEGER);
            }
            stmt.setString(13, car.getCarId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[DB ERROR] update car: " + e.getMessage());
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM cars WHERE car_id = ?";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[DB ERROR] delete car: " + e.getMessage());
        }
    }

    private AudiCar mapRow(ResultSet rs) throws SQLException {
        String type = rs.getString("car_type");
        String carId = rs.getString("car_id");
        String model = rs.getString("model");
        int year = rs.getInt("year");
        double price = rs.getDouble("price");
        String color = rs.getString("color");
        boolean available = rs.getBoolean("available");

        AudiCar car;
        if ("ELECTRIC".equals(type)) {
            car = new ElectricAudiCar(carId, model, year, price, color,
                    rs.getDouble("battery_capacity"),
                    rs.getInt("range_km"),
                    rs.getInt("charging_time"));
        } else {
            car = new AudiCar(carId, model, year, price,
                    rs.getString("engine_size"),
                    rs.getString("fuel_type"),
                    rs.getString("transmission"),
                    color);
        }
        car.setAvailable(available);
        String carStatus = rs.getString("car_status");
        if (carStatus != null) car.setCarStatus(carStatus);
        return car;
    }
}
