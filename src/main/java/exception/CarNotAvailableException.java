package exception;

public class CarNotAvailableException extends Exception {

    public CarNotAvailableException(String carId) {
        super("Masina cu ID '" + carId + "' nu este disponibila sau nu exista in stoc.");
    }
}
