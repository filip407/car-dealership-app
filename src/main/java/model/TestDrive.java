package model;

public class TestDrive {

    private String testDriveId;
    private String customerId;
    private String carId;
    private String date;
    private String time;
    private String status;
    private String feedback;

    public TestDrive(String testDriveId, String customerId, String carId, String date, String time) {
        this.testDriveId = testDriveId;
        this.customerId = customerId;
        this.carId = carId;
        this.date = date;
        this.time = time;
        this.status = "Programat";
        this.feedback = "";
    }

    public String getTestDriveId() { return testDriveId; }
    public String getCustomerId() { return customerId; }
    public String getCarId() { return carId; }
    public String getDate() { return date; }
    public String getTime() { return time; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    @Override
    public String toString() {
        return String.format("[%s] %s  %s | Masina: %s | Client: %s | Status: %s",
                testDriveId, date, time, carId, customerId, status);
    }
}
