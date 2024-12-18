import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Order {
    private List<MenuItem> items;

    public Order() {
        this.items = new ArrayList<>();
    }

    public void addMenuItem(MenuItem item) {
        items.add(item);
    }

    public List<MenuItem> getItems() {
        return items;
    }

    public double calculateTotalPrice() {
        double total = 0;
        for (MenuItem item : items) {
            total += item.getPrice();
        }
        return total;
    }

    public int calculateDrinkPoints(boolean isFreeDrinkRedeemed) {
        int points = 0;
        boolean freeDrinkApplied = false; // เพื่อตรวจสอบว่าเครื่องดื่มฟรีถูกใช้งานแล้วหรือไม่
        for (MenuItem item : items) {
            if (item.getType().equalsIgnoreCase("Drink")) {
                if (isFreeDrinkRedeemed && !freeDrinkApplied) {
                    freeDrinkApplied = true; // ระบุว่าเครื่องดื่มฟรีถูกใช้แล้ว
                } else {
                    points++; // เครื่องดื่มที่ไม่ฟรีจะสะสมคะแนน
                }
            }
        }
        return points;
    }

    public void printInvoice(Customer customer, boolean isFreeDrinkRedeemed) {
        System.out.println("===================================");
        System.out.println("           Euphoria Cafe           ");
        System.out.println("Date/Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("===================================\n");
    
        System.out.println("           --- Invoice ---          ");
        System.out.println("-----------------------------------");
    
        double drinkTotal = 0;
        double snackTotal = 0;
    
        // Drinks Section
        System.out.println("Drinks:");
        for (MenuItem item : items) {
            if (item.getType().equalsIgnoreCase("Drink")) {
                String priceText = isFreeDrinkRedeemed ? "Free" : String.format("%.2f Bath", item.getPrice());
                System.out.printf("%-5s %-20s %10s\n", "1.", item.getName(), priceText);
                if (!isFreeDrinkRedeemed) drinkTotal += item.getPrice();
            }
        }
    
        // Snacks Section
        System.out.println("\nSnacks:");
        for (MenuItem item : items) {
            if (item.getType().equalsIgnoreCase("Snack")) {
                System.out.printf("%-5s %-20s %10.2f Bath\n", "1.", item.getName(), item.getPrice());
                snackTotal += item.getPrice();
            }
        }
    
        // Discount Section
        double discountedSnackTotal = customer.applySnackDiscount(snackTotal);
        double snackDiscount = snackTotal - discountedSnackTotal;
        if (snackDiscount > 0) {
        System.out.printf("\n5%% discount applied to snacks: -%.2f Bath\n", snackDiscount);
        }

        // Summary Section
        System.out.println("-----------------------------------");
        System.out.printf("Subtotal (Drinks):      %.2f Bath\n", drinkTotal);
        System.out.printf("Subtotal (Snacks):      %.2f Bath\n", discountedSnackTotal);
        System.out.printf("Total Price:            %.2f Bath\n", drinkTotal + discountedSnackTotal);
        System.out.println("-----------------------------------");
    
        // Points Section
        System.out.printf("Points Earned:              %d\n", calculateDrinkPoints(isFreeDrinkRedeemed));
        System.out.printf("Remaining Points:           %d\n", customer.getPoints());
        System.out.println("===================================");
        System.out.println("Thank you for visiting Euphoria Cafe!");
        System.out.println("===================================");
    }            
}
