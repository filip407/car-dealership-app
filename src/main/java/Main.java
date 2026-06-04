import exception.CarNotAvailableException;
import exception.CustomerNotFoundException;
import factory.CarFactory;
import interfaces.Displayable;
import model.*;
import service.DealershipService;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        System.out.println("==========================================");
        System.out.println("   SISTEM MANAGEMENT AUDI CENTER");
        System.out.println("==========================================");

        DealershipService service = new DealershipService();

        System.out.println("\n[Factory Pattern] Creare obiecte AudiCar:");
        AudiCar standard = CarFactory.createStandardCar(
                "DEMO001", "A3 Sportback", 2024, 35000, "2.0 TDI", "Diesel", "Automata", "Negru");
        ElectricAudiCar electric = CarFactory.createElectricCar(
                "DEMO002", "e-tron GT", 2024, 95000, "Gri Quantum", 93.4, 488, 9);
        System.out.println("  Standard : " + standard.getCarDetails());
        System.out.println("  Electric : " + electric.getCarDetails());

        System.out.println("\n[Builder Pattern] Creare comanda de vanzare:");
        SaleOrder comanda = new SaleOrder.Builder()
                .orderId("DEMO-ORD")
                .customerId("CLI001")
                .carId("DEMO001")
                .salespersonId("V001")
                .date("2026-06-03")
                .finalPrice(35000)
                .status("Demo")
                .build();
        System.out.println("  Comanda: " + comanda);

        System.out.println("\n[Colectii] Statistici din baza de date:");
        List<AudiCar> disponibile = service.getAvailableCars();
        System.out.println("  Masini disponibile (TreeSet sortat dupa pret): " + disponibile.size());
        System.out.println("  Angajati (ArrayList): " + service.getAllEmployees().size());
        System.out.println("  Clienti   (HashMap) : " + service.getAllCustomers().size());
        System.out.println("  Comenzi   (ArrayList): " + service.getAllOrders().size());

        System.out.println("\n[Interfata Displayable] Polimorfism prin interfata - obiecte de tipuri diferite:");
        List<Displayable> displayables = new ArrayList<>();
        displayables.add(standard);   // AudiCar  -> Car implements Displayable
        displayables.add(electric);   // ElectricAudiCar -> AudiCar -> Car implements Displayable
        displayables.add(new Customer("DEMO_CLI", "Test", "Client", "0700000000", "test@demo.ro", "Str. Demo 1"));
        for (Displayable d : displayables) {
            System.out.println("  " + d.getDisplayInfo());
        }

        System.out.println("\n[Mostenire + Interfata Displayable] Inventar complet (sortat dupa pret):");
        service.printInventory();

        System.out.println("\n[Exceptii Custom] Scenarii de eroare:");
        try {
            service.createOrder("CLIENT_INEXISTENT", "CAR001", "V001", "2026-06-03");
        } catch (CustomerNotFoundException e) {
            System.out.println("  CustomerNotFoundException : " + e.getMessage());
        } catch (CarNotAvailableException e) {
            System.out.println("  CarNotAvailableException  : " + e.getMessage());
        }

        try {
            service.createOrder("CLI001", "MASINA_INEXISTENTA", "V001", "2026-06-03");
        } catch (CustomerNotFoundException e) {
            System.out.println("  CustomerNotFoundException : " + e.getMessage());
        } catch (CarNotAvailableException e) {
            System.out.println("  CarNotAvailableException  : " + e.getMessage());
        }

        System.out.println("\n[Audit] Actiunile sunt logate automat in audit.csv");
        System.out.println("[UI]    Interfata grafica se porneste prin ui.Launcher");
        System.out.println("==========================================");
    }
}
