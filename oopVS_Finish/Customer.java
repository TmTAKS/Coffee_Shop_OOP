public class Customer {
    private String name;
    private String phoneNumber;
    private int points;

    public Customer(String name, String phoneNumber, int points) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.points = points;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public boolean canRedeemFreeDrink() {
        return this.points >= 10;
    }

    public void redeemFreeDrink() {
        if (canRedeemFreeDrink()) {
            this.points -= 10;
            System.out.println("You have redeemed a free drink!");
        } else {
            System.out.println("Not enough points to redeem a free drink.");
        }
    }

    public double applySnackDiscount(double totalSnackPrice) {
        if (this.points > 0) {
            return totalSnackPrice * 0.95;
        }
        return totalSnackPrice;
    }

    public int getPoints() {
        return points;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
