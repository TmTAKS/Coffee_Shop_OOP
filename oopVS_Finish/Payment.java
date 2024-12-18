public class Payment {
    private String paymentMethod;
    private double amount;

    public Payment(String paymentMethod, double amount) {
        this.paymentMethod = paymentMethod;
        this.amount = amount;
    }

    public void processPayment() {
        System.out.println("Payment of $" + amount + " processed using " + paymentMethod + ".");
    }
}
