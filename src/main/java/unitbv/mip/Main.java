package unitbv.mip;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import unitbv.mip.config.ConfigManager; // Import nou
import unitbv.mip.model.*;
import unitbv.mip.service.Menu;
import unitbv.mip.strategy.DiscountStrategies;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        ConfigManager.loadConfig();

        System.out.println("TVA Curent: " + (Order.TVA * 100) + "%");

        List<Product> allProducts = createProductList();
        Menu restaurantMenu = createMenuFromList(allProducts);

        System.out.println("\n--- Meniu Restaurant \"La Andrei\" ---");
        restaurantMenu.printMenu();

        runOrderScenarios(restaurantMenu);

        exportMenuData(allProducts);
    }


    private static List<Product> createProductList() {
        List<Product> products = new ArrayList<>();
        products.add(new Food("Carne de pui", 25.5, 350, Category.FELURI_PRINCIPALE, false));
        products.add(new Food("Paste Bolognese", 28.0, 300, Category.FELURI_PRINCIPALE, false));
        products.add(new Pizza.PizzaBuilder("Subțire", "Roșii Dulci", 35.0)
                .withName("Pizza Margherita")
                .addTopping("Mozzarella Extra", 5.0, 50.0, false)
                .build());
        products.add(new Food("Salată Caesar", 30.0, 250, Category.APERITIVE, false));
        products.add(new Food("Lava Cake", 20.0, 150, Category.DESERTURI, true));

        products.add(new Drink("Apă", 9.0, 0.5, false));
        products.add(new Drink("Suc de portocale", 12.0, 0.33, false));
        products.add(new Drink("Bere", 10.0, 0.5, true));
        return products;
    }

    private static Menu createMenuFromList(List<Product> products) {
        Menu menu = new Menu();
        for (Product p : products) {
            menu.addProduct(p);
        }
        return menu;
    }

    private static void runOrderScenarios(Menu menu) {
        System.out.println("\nVERIFICARE STRATEGII DE DISCOUNT");

        DiscountStrategies.HAPPY_HOUR_ACTIVE = false;
        DiscountStrategies.MEAL_DEAL_ACTIVE = false;
        DiscountStrategies.PARTY_PACK_ACTIVE = false;

        Order myOrder = new Order();
        try {
            addProductToOrder(myOrder, menu, "Carne de pui", 1);
            addProductToOrder(myOrder, menu, "Bere", 3);
            addProductToOrder(myOrder, menu, "Pizza Margherita", 1);
            addProductToOrder(myOrder, menu, "Lava Cake", 1);
        } catch (RuntimeException e) {
            System.err.println("Eroare: " + e.getMessage());
            return;
        }

        System.out.println("\n>>> Conținut Comandă: " + myOrder.getItems());

        // 1. Test Standard
        System.out.println("\n--- 1. Total Standard ---");
        myOrder.setStrategy(DiscountStrategies::calculateTotalWithDiscounts);
        System.out.println("Total: " + String.format("%.2f", myOrder.calculateTotal()) + " RON");

        System.out.println("\n--- 2. Happy Hour (Manual ON) ---");
        DiscountStrategies.HAPPY_HOUR_ACTIVE = true;
        System.out.println("Total: " + String.format("%.2f", myOrder.calculateTotal()) + " RON");
        DiscountStrategies.HAPPY_HOUR_ACTIVE = false;

        System.out.println("\n--- 3. Meal Deal (Manual ON) ---");
        DiscountStrategies.MEAL_DEAL_ACTIVE = true;
        System.out.println("Total: " + String.format("%.2f", myOrder.calculateTotal()) + " RON");
        DiscountStrategies.MEAL_DEAL_ACTIVE = false;
    }

    private static void addProductToOrder(Order order, Menu menu, String productName, int qty) {
        Product p = menu.searchProductInMenu(productName)
                .orElseThrow(() -> new RuntimeException("Produsul '" + productName + "' nu există!"));
        order.addProduct(p, qty);
    }

    private static void exportMenuData(List<Product> products) {
        try {
            mapper.writeValue(new File("menu_export.json"), products);
            System.out.println("\nMeniu exportat cu succes.");
        } catch (IOException e) {
            System.err.println("Eroare export: " + e.getMessage());
        }
    }
}