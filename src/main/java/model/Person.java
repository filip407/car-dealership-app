package model;

import interfaces.Displayable;

public abstract class Person implements Displayable {

    private String id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;

    public Person(String id, String firstName, String lastName, String phone, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
    }

    public String getId() { return id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() { return firstName + " " + lastName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String getDisplayInfo() {
        return toString();
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Tel: %s | Email: %s",
                id, getFullName(), phone, email);
    }
}
