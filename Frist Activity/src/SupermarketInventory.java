import java.util.Scanner;

public class SupermarketInventory {
    // Constants
    public static final int MAX_PRODUCTS = 5;
    public static final int LOW_STOCK_THRESHOLD = 10;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        String[] names = new String[MAX_PRODUCTS];
        int[] quantities = new int[MAX_PRODUCTS];

        // Product registration
        System.out.println("=== Product Registration ===");
        for (int i = 0; i < MAX_PRODUCTS; i++) {
            System.out.print("Enter the name of product " + (i + 1) + ": ");
            names[i] = sc.nextLine();

            quantities[i] = readNonNegativeInt(sc, "Enter available quantity: ");
        }

        int option;
        do {
            showMenu();
            option = readInt(sc, "Choose an option: ");

            switch (option) {
                case 1 -> showProducts(names, quantities);
                case 2 -> {
                    System.out.print("Enter the product name to search: ");
                    String searched = sc.nextLine();
                    searchProduct(names, quantities, searched);
                }
                case 3 -> {
                    System.out.print("Enter the product name to update: ");
                    String update = sc.nextLine();
                    updateInventory(names, quantities, update, sc);
                }
                case 4 -> generateAlerts(names, quantities);
                case 5 -> System.out.println("Exiting system...");
                default -> System.out.println("Invalid option. Try again.");
            }
        } while (option != 5);

        sc.close();
    }

    // Show menu
    public static void showMenu() {
        System.out.println("\n=== Supermarket Inventory ===");
        System.out.println("1. Show all products and stock");
        System.out.println("2. Search product by name");
        System.out.println("3. Update product stock");
        System.out.println("4. Generate low stock alerts (< " + LOW_STOCK_THRESHOLD + ")");
        System.out.println("5. Exit");
    }

    // 1. Show products
    public static void showProducts(String[] names, int[] quantities) {
        int total = 0;
        System.out.println("\n=== Current Inventory ===");
        for (int i = 0; i < MAX_PRODUCTS; i++) {
            System.out.println(names[i] + " -> " + quantities[i] + " units");
            total += quantities[i];
        }
        System.out.println("Total stock: " + total + " units.");
    }

    // 2. Search product
    public static void searchProduct(String[] names, int[] quantities, String searched) {
        boolean found = false;
        for (int i = 0; i < MAX_PRODUCTS; i++) {
            if (names[i].equalsIgnoreCase(searched)) {
                System.out.println("Product found: " + names[i] + " -> " + quantities[i] + " units");
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.println("Product not found.");
        }
    }

    // 3. Update inventory
    public static void updateInventory(String[] names, int[] quantities, String product, Scanner sc) {
        boolean found = false;
        for (int i = 0; i < MAX_PRODUCTS; i++) {
            if (names[i].equalsIgnoreCase(product)) {
                found = true;
                int change = readInt(sc, "Enter quantity to add (positive) or remove (negative): ");
                if (quantities[i] + change < 0) {
                    System.out.println("Error: Stock cannot be negative.");
                } else {
                    quantities[i] += change;
                    System.out.println("Stock updated: " + names[i] + " -> " + quantities[i] + " units");
                }
                break;
            }
        }
        if (!found) {
            System.out.println("Product not found.");
        }
    }

    // 4. Generate alerts
    public static void generateAlerts(String[] names, int[] quantities) {
        System.out.println("\n=== Low Stock Alerts (<" + LOW_STOCK_THRESHOLD + " units) ===");
        boolean alert = false;
        for (int i = 0; i < MAX_PRODUCTS; i++) {
            if (quantities[i] < LOW_STOCK_THRESHOLD) {
                System.out.println(names[i] + " -> " + quantities[i] + " units");
                alert = true;
            }
        }
        if (!alert) {
            System.out.println("All products have enough stock.");
        }
    }

    // Helper method: read integer
    public static int readInt(Scanner sc, String message) {
        while (true) {
            try {
                System.out.print(message);
                return Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter an integer.");
            }
        }
    }

    // Helper method: read non-negative integer
    public static int readNonNegativeInt(Scanner sc, String message) {
        int value;
        do {
            value = readInt(sc, message);
            if (value < 0) {
                System.out.println("Quantity cannot be negative.");
            }
        } while (value < 0);
        return value;
    }
}
