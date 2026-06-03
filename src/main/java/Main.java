import exception.CarNotAvailableException;
import exception.CustomerNotFoundException;
import factory.CarFactory;
import model.*;
import service.DealershipService;

import java.util.Collection;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        DealershipService service = new DealershipService();

        System.out.println("==========================================");
        System.out.println("   SISTEM MANAGEMENT AUDI CENTER");
        System.out.println("==========================================");

        System.out.println("\n--- Adaugam angajati ---");
        Salesperson andrei = new Salesperson("EMP001", "Andrei", "Popescu", "0722111222", "andrei@audicenter.ro", "V001", 4000, "2020-03-15", 3.5);
        Salesperson maria  = new Salesperson("EMP002", "Maria",  "Ionescu",  "0733222333", "maria@audicenter.ro",  "V002", 4200, "2019-07-01", 4.0);
        Mechanic   ion     = new Mechanic   ("EMP003", "Ion",    "Dumitrescu","0744333444", "ion@audicenter.ro",   "M001", 3500, "2021-01-10", "Motor si transmisie");
        service.addEmployee(andrei);
        service.addEmployee(maria);
        service.addEmployee(ion);

        System.out.println("\n--- Adaugam masini (Factory pattern) ---");
        AudiCar a3    = CarFactory.createStandardCar("CAR001", "A3 Sportback", 2023, 35000, "2.0 TDI",  "Diesel",  "Automata", "Negru");
        AudiCar a4    = CarFactory.createStandardCar("CAR002", "A4 Avant",     2022, 45000, "2.0 TFSI", "Benzina", "Automata", "Alb");
        AudiCar q5    = CarFactory.createStandardCar("CAR003", "Q5",           2024, 62000, "3.0 TDI",  "Diesel",  "Automata", "Gri");
        AudiCar a6    = CarFactory.createStandardCar("CAR004", "A6",           2023, 58000, "2.0 TDI",  "Diesel",  "Automata", "Albastru");
        AudiCar rs6   = CarFactory.createStandardCar("CAR005", "RS6 Avant",    2024, 120000,"4.0 TFSI", "Benzina", "Automata", "Negru");
        AudiCar q3    = CarFactory.createStandardCar("CAR006", "Q3",           2022, 38000, "1.5 TFSI", "Benzina", "Manuala",  "Rosu");
        ElectricAudiCar etronGT = CarFactory.createElectricCar("CAR007", "e-tron GT", 2024, 95000, "Gri Quantum",    93.4, 488, 9);
        ElectricAudiCar q8etron = CarFactory.createElectricCar("CAR008", "Q8 e-tron", 2024, 85000, "Albastru Navarra", 114, 600, 10);
        service.addCar(a3);
        service.addCar(a4);
        service.addCar(q5);
        service.addCar(a6);
        service.addCar(rs6);
        service.addCar(q3);
        service.addCar(etronGT);
        service.addCar(q8etron);

        service.printInventory();

        System.out.println("\n--- Adaugam clienti ---");
        Customer alex  = new Customer("CLI001", "Alexandru", "Marin",      "0755123456", "alex@gmail.com",    "Str. Florilor 10, Bucuresti");
        Customer elena = new Customer("CLI002", "Elena",     "Constantin", "0766234567", "elena@yahoo.com",   "Bd. Unirii 25, Cluj");
        Customer mihai = new Customer("CLI003", "Mihai",     "Popa",       "0777345678", "mihai@hotmail.com", "Calea Victoriei 5, Brasov");
        service.addCustomer(alex);
        service.addCustomer(elena);
        service.addCustomer(mihai);

        System.out.println("\n--- Cautari ---");
        List<AudiCar> rezultatA = service.searchByModel("A");
        System.out.println("  Modele cu 'A': " + rezultatA.size());

        List<AudiCar> bugetMediu = service.searchByPriceRange(30000, 65000);
        System.out.println("  Intre 30.000-65.000 EUR: " + bugetMediu.size());

        List<AudiCar> masini2024 = service.getCarsByYear(2024);
        System.out.println("  Fabricate in 2024: " + masini2024.size());

        System.out.println("\n--- Programam test drive-uri ---");
        TestDrive td1 = new TestDrive("TD001", "CLI001", "CAR007", "2024-04-22", "10:00");
        TestDrive td2 = new TestDrive("TD002", "CLI002", "CAR003", "2024-04-23", "14:00");
        service.scheduleTestDrive(td1);
        service.scheduleTestDrive(td2);
        td1.setFeedback("Masina superba, silentioasa si extrem de rapida!");

        System.out.println("\n--- Programare service ---");
        ServiceAppointment srv1 = new ServiceAppointment("SRV001", "CAR002", "M001", "2024-05-10", "Revizie anuala + schimb ulei");
        service.addServiceAppointment(srv1);
        service.completeServiceAppointment("SRV001", 850);

        System.out.println("\n--- Cream comenzi de vanzare ---");
        try {
            service.createOrder("CLI001", "CAR007", "V001", "2024-04-25");
            service.createOrder("CLI002", "CAR003", "V002", "2024-04-26");
            service.createOrder("CLI003", "CAR001", "V001", "2024-04-27");
        } catch (CustomerNotFoundException | CarNotAvailableException e) {
            System.out.println("  [EROARE] " + e.getMessage());
        }

        System.out.println("\n--- Demo Builder pattern (SaleOrder.Builder) ---");
        SaleOrder comandaManual = new SaleOrder.Builder()
                .orderId("ORD999")
                .customerId("CLI001")
                .carId("CAR005")
                .salespersonId("V001")
                .date("2024-05-01")
                .finalPrice(120000)
                .status("Confirmata")
                .build();
        System.out.println("  Comanda construita cu Builder: " + comandaManual);

        System.out.println("\n--- Test exceptii ---");
        try {
            service.createOrder("CLI002", "CAR007", "V002", "2024-04-28");
        } catch (CarNotAvailableException e) {
            System.out.println("  [EROARE] " + e.getMessage());
        } catch (CustomerNotFoundException e) {
            System.out.println("  [EROARE] " + e.getMessage());
        }

        try {
            service.createOrder("CLI999", "CAR002", "V001", "2024-04-29");
        } catch (CustomerNotFoundException e) {
            System.out.println("  [EROARE] " + e.getMessage());
        } catch (CarNotAvailableException e) {
            System.out.println("  [EROARE] " + e.getMessage());
        }

        service.printAllOrders();

        System.out.println("\n--- Anulam o comanda ---");
        service.cancelOrder("ORD002");

        System.out.println("\n--- Masini disponibile dupa vanzari ---");
        List<AudiCar> disponibile = service.getAvailableCars();
        System.out.println("  Ramase: " + disponibile.size() + " masini");

        System.out.println("\n--- Angajati vanzatori ---");
        List<Employee> vanzatori = service.getEmployeesByRole("Vanzator");
        for (Employee e : vanzatori) System.out.println("  " + e);

        System.out.println("\n--- Raport financiar ---");
        double venit = service.getTotalRevenue();
        System.out.printf("  Venit total: %.0f EUR%n", venit);
        System.out.printf("  Comision Andrei: %.2f EUR%n", andrei.calculateCommission(venit));
        System.out.printf("  Comision Maria: %.2f EUR%n", maria.calculateCommission(venit));

        System.out.println("\n  [i] Actiunile au fost logate in fisierul audit.csv");
        System.out.println("==========================================");
        System.out.println("   PROGRAM FINALIZAT CU SUCCES");
        System.out.println("==========================================");
    }
}
