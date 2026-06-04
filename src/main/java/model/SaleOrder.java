package model;

public class SaleOrder {

    private String orderId;
    private String customerId;
    private String carId;
    private String salespersonId;
    private String date;
    private double finalPrice;
    private String status;

    public SaleOrder(String orderId, String customerId, String carId,
                     String salespersonId, String date, double finalPrice) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.carId = carId;
        this.salespersonId = salespersonId;
        this.date = date;
        this.finalPrice = finalPrice;
        this.status = "In procesare";
    }

    public String getOrderId() { return orderId; }
    public String getCustomerId() { return customerId; }
    public String getCarId() { return carId; }
    public String getSalespersonId() { return salespersonId; }
    public String getDate() { return date; }

    public double getFinalPrice() { return finalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("[Comanda %s] Client: %s | Masina: %s | Data: %s | Pret: %.0f EUR | Status: %s",
                orderId, customerId, carId, date, finalPrice, status);
    }

    public static class Builder {
        private String orderId;
        private String customerId;
        private String carId;
        private String salespersonId;
        private String date;
        private double finalPrice;
        private String status = "In procesare";

        public Builder orderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder customerId(String customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder carId(String carId) {
            this.carId = carId;
            return this;
        }

        public Builder salespersonId(String salespersonId) {
            this.salespersonId = salespersonId;
            return this;
        }

        public Builder date(String date) {
            this.date = date;
            return this;
        }

        public Builder finalPrice(double finalPrice) {
            this.finalPrice = finalPrice;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public SaleOrder build() {
            SaleOrder order = new SaleOrder(orderId, customerId, carId, salespersonId, date, finalPrice);
            order.setStatus(status);
            return order;
        }
    }
}
