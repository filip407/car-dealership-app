package service;

import exception.CarNotAvailableException;
import exception.CustomerNotFoundException;
import interfaces.Displayable;
import model.*;

import java.util.*;

public class DealershipService {

    private TreeSet<AudiCar> inventory;
    private HashMap<String, Customer> customers;
    private ArrayList<SaleOrder> orders;
    private ArrayList<Employee> employees;
    private ArrayList<TestDrive> testDrives;
    private ArrayList<ServiceAppointment> serviceAppointments;

    private final AuditService audit = AuditService.getInstance();

    private boolean dbAvailable = false;
    private CarDbService carDb;
    private CustomerDbService customerDb;
    private EmployeeDbService employeeDb;
    private SaleOrderDbService orderDb;
    private TestDriveDbService testDriveDb;
    private ServiceAppointmentDbService serviceAppDb;

    public DealershipService() {
        inventory = new TreeSet<>();
        customers = new HashMap<>();
        orders = new ArrayList<>();
        employees = new ArrayList<>();
        testDrives = new ArrayList<>();
        serviceAppointments = new ArrayList<>();

        try {
            carDb = CarDbService.getInstance();
            customerDb = CustomerDbService.getInstance();
            employeeDb = EmployeeDbService.getInstance();
            orderDb = SaleOrderDbService.getInstance();
            testDriveDb = TestDriveDbService.getInstance();
            serviceAppDb = ServiceAppointmentDbService.getInstance();
            loadFromDb();
            dbAvailable = true;
            System.out.println("[DB] Conectat. Date incarcate din baza de date.");
        } catch (Exception e) {
            System.out.println("[DB] Nu s-a putut conecta: " + e.getMessage());
            System.out.println("[DB] Aplicatia functioneaza cu date temporare (nesalvate).");
        }
    }

    private void loadFromDb() {
        for (AudiCar car : carDb.findAll()) {
            inventory.add(car);
        }
        for (Customer c : customerDb.findAll()) {
            customers.put(c.getId(), c);
        }
        for (Employee e : employeeDb.findAll()) {
            employees.add(e);
        }
        for (SaleOrder o : orderDb.findAll()) {
            orders.add(o);
            Customer c = customers.get(o.getCustomerId());
            if (c != null) c.addOrderId(o.getOrderId());
        }
        for (TestDrive td : testDriveDb.findAll()) {
            testDrives.add(td);
        }
        for (ServiceAppointment sa : serviceAppDb.findAll()) {
            serviceAppointments.add(sa);
        }
    }

    public void addCar(AudiCar car) {
        inventory.add(car);
        if (dbAvailable) carDb.save(car);
        audit.log("ADD_CAR");
        System.out.println("  [+] Adaugata: " + car);
    }

    public boolean removeCar(String carId) {
        for (SaleOrder order : orders) {
            if (order.getCarId().equals(carId) && !"Anulata".equals(order.getStatus())) {
                return false;
            }
        }
        AudiCar toRemove = findCarById(carId);
        if (toRemove != null) {
            cancelTestDrivesForCar(carId);
            inventory.remove(toRemove);
            if (dbAvailable) carDb.delete(carId);
            audit.log("REMOVE_CAR");
            return true;
        }
        return false;
    }

    public void updateStandardCar(String carId, String model, int year, double price,
                                  String engineSize, String fuelType, String transmission, String color) {
        AudiCar car = findCarById(carId);
        if (car == null) return;
        inventory.remove(car);
        car.setModel(model);
        car.setYear(year);
        car.setPrice(price);
        car.setEngineSize(engineSize);
        car.setFuelType(fuelType);
        car.setTransmission(transmission);
        car.setColor(color);
        inventory.add(car);
        if (dbAvailable) carDb.update(car);
        audit.log("UPDATE_CAR");
    }

    public void updateElectricCar(String carId, String model, int year, double price, String color,
                                  double batteryCapacity, int range, int chargingTime) {
        AudiCar car = findCarById(carId);
        if (!(car instanceof ElectricAudiCar)) return;
        ElectricAudiCar ec = (ElectricAudiCar) car;
        inventory.remove(ec);
        ec.setModel(model);
        ec.setYear(year);
        ec.setPrice(price);
        ec.setColor(color);
        ec.setBatteryCapacity(batteryCapacity);
        ec.setRange(range);
        ec.setChargingTime(chargingTime);
        inventory.add(ec);
        if (dbAvailable) carDb.update(ec);
        audit.log("UPDATE_CAR");
    }

    public synchronized String generateCarId() {
        int n = 1;
        while (findCarById(String.format("CAR%03d", n)) != null) n++;
        return String.format("CAR%03d", n);
    }

    public List<AudiCar> getAvailableCars() {
        audit.log("GET_AVAILABLE_CARS");
        List<AudiCar> result = new ArrayList<>();
        for (AudiCar car : inventory) {
            if (car.isAvailable()) result.add(car);
        }
        return result;
    }

    public List<AudiCar> searchByModel(String model) {
        audit.log("SEARCH_BY_MODEL");
        List<AudiCar> result = new ArrayList<>();
        for (AudiCar car : inventory) {
            if (car.getModel().toLowerCase().contains(model.toLowerCase())) result.add(car);
        }
        return result;
    }

    public TreeSet<AudiCar> getInventory() {
        return inventory;
    }

    public AudiCar getCarById(String carId) {
        return findCarById(carId);
    }

    public void addCustomer(Customer customer) {
        customers.put(customer.getId(), customer);
        if (dbAvailable) customerDb.save(customer);
        audit.log("ADD_CUSTOMER");
    }

    public Collection<Customer> getAllCustomers() {
        audit.log("GET_ALL_CUSTOMERS");
        return customers.values();
    }

    public void addEmployee(Employee employee) {
        employees.add(employee);
        if (dbAvailable) employeeDb.save(employee);
        audit.log("ADD_EMPLOYEE");
    }

    public List<Employee> getAllEmployees() {
        return employees;
    }

    public List<Employee> getEmployeesByRole(String role) {
        audit.log("GET_EMPLOYEES_BY_ROLE");
        List<Employee> result = new ArrayList<>();
        for (Employee emp : employees) {
            if (emp.getRole().equalsIgnoreCase(role)) result.add(emp);
        }
        return result;
    }

    public boolean removeEmployee(String personId) {
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getId().equals(personId)) {
                employees.remove(i);
                if (dbAvailable) employeeDb.delete(personId);
                audit.log("REMOVE_EMPLOYEE");
                return true;
            }
        }
        return false;
    }

    public String generateEmployeeId() {
        int n = 1;
        while (findEmployeeById(String.format("EMP%03d", n)) != null) n++;
        return String.format("EMP%03d", n);
    }

    public String generateSalespersonCode() {
        int n = 1;
        while (employeeCodeExists(String.format("V%03d", n))) n++;
        return String.format("V%03d", n);
    }

    public String generateMechanicCode() {
        int n = 1;
        while (employeeCodeExists(String.format("M%03d", n))) n++;
        return String.format("M%03d", n);
    }

    public String generateCustomerId() {
        int n = 1;
        while (customers.containsKey(String.format("CLI%03d", n))) n++;
        return String.format("CLI%03d", n);
    }

    public SaleOrder createOrder(String customerId, String carId,
                                 String salespersonEmployeeId, String date)
            throws CustomerNotFoundException, CarNotAvailableException {

        Customer customer = customers.get(customerId);
        if (customer == null) throw new CustomerNotFoundException(customerId);

        AudiCar car = findCarById(carId);
        if (car == null || !car.isAvailable()) throw new CarNotAvailableException(carId);

        String orderId = "ORD" + String.format("%03d", orders.size() + 1);
        SaleOrder order = new SaleOrder.Builder()
                .orderId(orderId)
                .customerId(customerId)
                .carId(carId)
                .salespersonId(salespersonEmployeeId)
                .date(date)
                .finalPrice(car.getPrice())
                .status("In asteptare")
                .build();

        car.setAvailable(false);
        car.setCarStatus("IN ASTEPTARE");
        customer.addOrderId(orderId);
        orders.add(order);

        if (dbAvailable) {
            carDb.update(car);
            orderDb.save(order);
        }

        audit.log("CREATE_ORDER");
        return order;
    }

    public boolean confirmOrder(String orderId) {
        for (SaleOrder order : orders) {
            if (order.getOrderId().equals(orderId) && "In asteptare".equals(order.getStatus())) {
                order.setStatus("Confirmata");
                AudiCar car = findCarById(order.getCarId());
                if (car != null) {
                    car.setCarStatus("VANDUTA");
                    if (dbAvailable) carDb.update(car);
                }
                if (dbAvailable) orderDb.update(order);
                cancelTestDrivesForCar(order.getCarId());
                audit.log("CONFIRM_ORDER");
                return true;
            }
        }
        return false;
    }

    public boolean cancelOrder(String orderId) {
        for (SaleOrder order : orders) {
            if (order.getOrderId().equals(orderId)) {
                if ("Confirmata".equals(order.getStatus()) || "Anulata".equals(order.getStatus())) {
                    return false;
                }
                order.setStatus("Anulata");
                AudiCar car = findCarById(order.getCarId());
                if (car != null) {
                    car.setAvailable(true);
                    car.setCarStatus("DISPONIBILA");
                    if (dbAvailable) carDb.update(car);
                }
                if (dbAvailable) orderDb.update(order);
                audit.log("CANCEL_ORDER");
                return true;
            }
        }
        return false;
    }

    private void cancelTestDrivesForCar(String carId) {
        List<TestDrive> toRemove = new ArrayList<>();
        for (TestDrive td : testDrives) {
            if (td.getCarId().equals(carId)) {
                toRemove.add(td);
                if (dbAvailable) testDriveDb.delete(td.getTestDriveId());
            }
        }
        testDrives.removeAll(toRemove);
    }

    public List<SaleOrder> getOrdersByCustomer(String customerId) {
        audit.log("GET_ORDERS_BY_CUSTOMER");
        List<SaleOrder> result = new ArrayList<>();
        for (SaleOrder order : orders) {
            if (order.getCustomerId().equals(customerId)) result.add(order);
        }
        return result;
    }

    public List<SaleOrder> getAllOrders() {
        return orders;
    }

    public void scheduleTestDrive(TestDrive testDrive) {
        testDrives.add(testDrive);
        if (dbAvailable) testDriveDb.save(testDrive);
        audit.log("SCHEDULE_TEST_DRIVE");
    }

    public List<TestDrive> getAllTestDrives() {
        return testDrives;
    }

    public boolean confirmTestDrive(String testDriveId) {
        for (TestDrive td : testDrives) {
            if (td.getTestDriveId().equals(testDriveId)) {
                td.setStatus("Confirmat");
                if (dbAvailable) testDriveDb.update(td);
                audit.log("CONFIRM_TEST_DRIVE");
                return true;
            }
        }
        return false;
    }

    public boolean cancelTestDrive(String testDriveId) {
        for (int i = 0; i < testDrives.size(); i++) {
            if (testDrives.get(i).getTestDriveId().equals(testDriveId)) {
                testDrives.remove(i);
                if (dbAvailable) testDriveDb.delete(testDriveId);
                audit.log("CANCEL_TEST_DRIVE");
                return true;
            }
        }
        return false;
    }

    public String generateTestDriveId() {
        int n = 1;
        while (findTestDriveById(String.format("TD%03d", n)) != null) n++;
        return String.format("TD%03d", n);
    }

    private TestDrive findTestDriveById(String id) {
        for (TestDrive td : testDrives) {
            if (td.getTestDriveId().equals(id)) return td;
        }
        return null;
    }

    public void addServiceAppointment(ServiceAppointment appointment) {
        serviceAppointments.add(appointment);
        if (dbAvailable) serviceAppDb.save(appointment);
        audit.log("ADD_SERVICE_APPOINTMENT");
    }

    public boolean cancelServiceAppointment(String appointmentId) {
        for (int i = 0; i < serviceAppointments.size(); i++) {
            if (serviceAppointments.get(i).getAppointmentId().equals(appointmentId)) {
                serviceAppointments.remove(i);
                if (dbAvailable) serviceAppDb.delete(appointmentId);
                audit.log("CANCEL_SERVICE_APPOINTMENT");
                return true;
            }
        }
        return false;
    }

    public boolean completeServiceAppointment(String appointmentId, double cost) {
        for (ServiceAppointment appt : serviceAppointments) {
            if (appt.getAppointmentId().equals(appointmentId)) {
                appt.setStatus("Finalizat");
                appt.setCost(cost);
                if (dbAvailable) serviceAppDb.update(appt);
                audit.log("COMPLETE_SERVICE_APPOINTMENT");
                return true;
            }
        }
        return false;
    }

    public List<ServiceAppointment> getAllServiceAppointments() {
        return serviceAppointments;
    }

    public String generateServiceAppointmentId() {
        int n = 1;
        while (findServiceAppointmentById(String.format("SVC%03d", n)) != null) n++;
        return String.format("SVC%03d", n);
    }

    private ServiceAppointment findServiceAppointmentById(String id) {
        for (ServiceAppointment sa : serviceAppointments) {
            if (sa.getAppointmentId().equals(id)) return sa;
        }
        return null;
    }

    public double getTotalRevenue() {
        audit.log("GET_TOTAL_REVENUE");
        double total = 0;
        for (SaleOrder order : orders) {
            if ("Confirmata".equals(order.getStatus())) total += order.getFinalPrice();
        }
        return total;
    }

    public void printInventory() {
        System.out.println("\n=== STOC COMPLET (sortat dupa pret) ===");
        for (Displayable item : inventory) System.out.println("  " + item.getDisplayInfo());
    }

    private AudiCar findCarById(String carId) {
        for (AudiCar car : inventory) {
            if (car.getCarId().equals(carId)) return car;
        }
        return null;
    }

    private Employee findEmployeeById(String personId) {
        for (Employee emp : employees) {
            if (emp.getId().equals(personId)) return emp;
        }
        return null;
    }

    private boolean employeeCodeExists(String code) {
        for (Employee emp : employees) {
            if (emp.getEmployeeId().equals(code)) return true;
        }
        return false;
    }
}
