package model;

import java.util.ArrayList;

public class Customer extends Person {

    private String address;
    private ArrayList<String> orderIds;

    public Customer(String id, String firstName, String lastName,
                    String phone, String email, String address) {
        super(id, firstName, lastName, phone, email);
        this.address = address;
        this.orderIds = new ArrayList<>();
    }

    public String getAddress() { return address; }

    public void addOrderId(String orderId) { orderIds.add(orderId); }
    public ArrayList<String> getOrderIds() { return orderIds; }

    @Override
    public String toString() {
        return String.format("[%s] %s | Tel: %s | Email: %s | Adresa: %s",
                getId(), getFullName(), getPhone(), getEmail(), address);
    }
}
