import java.io.*;
import java.util.*;

public class CoffeeShopApp {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        String customerFile = "customers.txt"; // ไฟล์สำหรับเก็บข้อมูลลูกค้า
        Map<String, Customer> customerData = loadCustomerData(customerFile);

        while (true) {
            printHeader("Euphoria Cafe");
            showMainMenu();

            System.out.print("Choose an option: ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    handleCustomerOrder(scanner, customerData, customerFile);
                    break;
                case "2":
                    viewCustomerDetails(scanner, customerData);
                    break;
                case "3":
                    printSuccess("Thank you for visiting Euphoria Cafe!");
                    return;
                default:
                    printError("Invalid choice. Please enter a number between 1 and 3.");
            }
        }
    }

    // แสดงเมนูหลัก
    public static void showMainMenu() {
        System.out.println("1. Place an order");
        System.out.println("2. View customer details");
        System.out.println("3. Exit");
        System.out.println();
    }

    // แสดงหัวข้อ
    public static void printHeader(String title) {
        System.out.println("\u001B[34m===================================\u001B[0m");
        System.out.println("\u001B[34m           " + title + "           \u001B[0m");
        System.out.println("\u001B[34m===================================\u001B[0m\n");
    }
    
    public static void printSuccess(String message) {
        System.out.println("\u001B[32m" + message + "\u001B[0m"); // สีเขียว
    }
    
    public static void printError(String message) {
        System.out.println("\u001B[31m" + message + "\u001B[0m"); // สีแดง
    }
    
    // จัดการคำสั่งซื้อ
    public static void handleCustomerOrder(Scanner scanner, Map<String, Customer> customerData, String customerFile) throws IOException {
        printHeader("Place an Order");
        System.out.print("Enter your phone number: ");
        String phoneNumber = scanner.nextLine().trim();

        Customer customer = customerData.getOrDefault(phoneNumber, null);
        if (customer == null) {
            System.out.print("You are a new customer. Please enter your name: ");
            String name = scanner.nextLine().trim();
            customer = new Customer(name, phoneNumber, 0);
            customerData.put(phoneNumber, customer);
            saveCustomerData(customerFile, customerData);
            printSuccess("Welcome, " + name + "! Your information has been saved.");
        } else {
            printSuccess("Welcome back, " + customer.getName() + "!");
            System.out.println("Your current points: " + customer.getPoints());
        }

        // แสดงเมนู
        List<MenuItem> menu = initializeMenu();
        displayMenu(menu);

        // สร้างออเดอร์
        Order order = new Order();
        while (true) {
            System.out.print("Select a menu item by number (or 0 to finish): ");
            String input = scanner.nextLine().trim();
            if (input.equals("0")) break;

            try {
                int choice = Integer.parseInt(input);
                if (choice > 0 && choice <= menu.size()) {
                    MenuItem item = menu.get(choice - 1);
                    order.addMenuItem(item);
                    printSuccess(item.getName() + " has been added to your order.");
                } else {
                    printError("Invalid choice. Please select a valid menu item.");
                }
            } catch (NumberFormatException e) {
                printError("Please enter a valid number.");
            }
        }

        // แลกเครื่องดื่มฟรี
        System.out.print("Do you want to redeem a free drink? (yes/no): ");
        String redeemChoice = scanner.nextLine().trim().toLowerCase();
        boolean isFreeDrinkRedeemed = false;
        if (redeemChoice.equals("yes")) {
            if (customer.canRedeemFreeDrink()) {
                customer.redeemFreeDrink();
                saveCustomerData(customerFile, customerData);
                isFreeDrinkRedeemed = true;
                printSuccess("You have redeemed a free drink!");
            } else {
                printError("You don't have enough points to redeem a free drink.");
            }
        }

        // คำนวณคะแนนและพิมพ์ใบเสร็จ
        int earnedPoints = order.calculateDrinkPoints(isFreeDrinkRedeemed);
        customer.addPoints(earnedPoints);
        System.out.println("\nPoints earned: " + earnedPoints);
        System.out.println("Your total points now: " + customer.getPoints());
        saveCustomerData(customerFile, customerData);

        printHeader("Invoice");
        order.printInvoice(customer, isFreeDrinkRedeemed);
    }

    // แสดงรายละเอียดลูกค้า
    public static void viewCustomerDetails(Scanner scanner, Map<String, Customer> customerData) {
        printHeader("Customer Details");
        System.out.print("Enter phone number: ");
        String phoneNumber = scanner.nextLine().trim();
        Customer customer = customerData.getOrDefault(phoneNumber, null);

        if (customer != null) {
            System.out.println("Name: " + customer.getName());
            System.out.println("Phone: " + customer.getPhoneNumber());
            System.out.println("Points: " + customer.getPoints());
        } else {
            printError("Customer not found.");
        }
    }

    // แสดงเมนูอาหารในรูปแบบตาราง
    public static void displayMenu(List<MenuItem> menu) {
        System.out.printf("%-5s %-20s %-10s %-10s\n", "No.", "Menu Item", "Price", "Type");
        System.out.println("---------------------------------------------------");
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.get(i);
            System.out.printf("%-5d %-20s %-10.2f %-10s\n", (i + 1), item.getName(), item.getPrice(), item.getType());
        }
    }

    // โหลดข้อมูลลูกค้าจากไฟล์
    public static Map<String, Customer> loadCustomerData(String fileName) throws IOException {
        Map<String, Customer> customerData = new HashMap<>();
        File file = new File(fileName);

        if (file.exists()) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String phone = parts[0];
                    String name = parts[1];
                    int points = Integer.parseInt(parts[2]);
                    customerData.put(phone, new Customer(name, phone, points));
                }
            }
            reader.close();
        }
        return customerData;
    }

    // บันทึกข้อมูลลูกค้าลงไฟล์
    public static void saveCustomerData(String fileName, Map<String, Customer> customerData) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        for (Customer customer : customerData.values()) {
            writer.write(customer.getPhoneNumber() + "," + customer.getName() + "," + customer.getPoints());
            writer.newLine();
        }
        writer.close();
    }

    // สร้างเมนู
    public static List<MenuItem> initializeMenu() {
        List<MenuItem> menu = new ArrayList<>();
        menu.add(new MenuItem("Latte", 120.0, "Drink"));
        menu.add(new MenuItem("Cappuccino", 115.0, "Drink"));
        menu.add(new MenuItem("Espresso", 90.0, "Drink"));
        menu.add(new MenuItem("Americano", 100.0, "Drink"));
        menu.add(new MenuItem("Mocha", 135.0, "Drink"));
        menu.add(new MenuItem("Matcha Latte", 140.0, "Drink"));
        menu.add(new MenuItem("Croissant", 70.0, "Snack"));
        menu.add(new MenuItem("Brownie", 85.0, "Snack"));
        menu.add(new MenuItem("Cheesecake", 150.0, "Snack"));
        menu.add(new MenuItem("Cookie", 60.0, "Snack"));
        menu.add(new MenuItem("Muffin", 75.0, "Snack"));
        menu.add(new MenuItem("Pancake", 95.0, "Snack"));
        return menu;
    }
}