package exception;

public class CustomerNotFoundException extends Exception {

    public CustomerNotFoundException(String customerId) {
        super("Clientul cu ID '" + customerId + "' nu a fost gasit in sistem.");
    }
}
