package unitbv.mip;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import unitbv.mip.config.AppConfig;
import unitbv.mip.config.ConfigException;
import unitbv.mip.model.*;
import unitbv.mip.service.Menu;
import unitbv.mip.strategy.DiscountStrategies;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            setupGlobalConfiguration();
        } catch (ConfigException e) {
            System.err.println(e.getMessage());
            System.err.println("Fișierul de configurare lipsește sau este corupt. Vă rugăm să contactați echipa de suport tehnic.");
            System.exit(1);
        }

        List<Product> allProducts = createProductList();
        Menu restaurantMenu = createMenuFromList(allProducts);

        System.out.println("\n--- Meniu Restaurant \"La Andrei\" ---");
        restaurantMenu.printMenu();

        runOrderScenarios(restaurantMenu);

        exportMenuData(allProducts);
    }


    private static void setupGlobalConfiguration() throws ConfigException {
        try {
            File configFile = new File("config.json");
            AppConfig config = mapper.readValue(configFile, AppConfig.class);

            Order.TVA = config.getTVA();

            System.out.println("--- INIȚIALIZARE SISTEM ---");
            System.out.println("Configurație încărcată cu succes!");
            System.out.println("Nume Restaurant: " + config.getRestaurantName());
            System.out.println("TVA setat la: " + (Order.TVA * 100) + "%");

        } catch (FileNotFoundException e) {
            throw new ConfigException(
                    "Fișierul de configurare 'config.json' nu a fost găsit.",
                    e);
        } catch (JsonProcessingException e) {
            throw new ConfigException(
                    "Fișierul de configurare este corupt sau are un format JSON invalid.",
                    e);
        } catch (IOException e) {
            throw new ConfigException(
                    "A apărut o eroare generală la citirea configurării.",
                    e);
        }
    }


    private static List<Product> createProductList() {
        List<Product> products = new ArrayList<>();

        products.add(new Food("Carne de pui", 25.5, 350, Category.FELURI_PRINCIPALE, false));
        products.add(new Food("Paste Bolognese", 28.0, 300, Category.FELURI_PRINCIPALE, false));
        products.add(new Food("Pizza Margherita", 35.0, 400, Category.FELURI_PRINCIPALE, true));
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
        System.out.println("VERIFICARE STRATEGII DE DISCOUNT");

        Order myOrder = new Order();

        try {
            addProductToOrder(myOrder, menu, "Carne de pui", 1);
            addProductToOrder(myOrder, menu, "Bere", 3);           // Pt Happy Hour
            addProductToOrder(myOrder, menu, "Pizza Casei", 1);    // Pizza Custom
            addProductToOrder(myOrder, menu, "Apă", 1);            // Pt Promoție
        } catch (RuntimeException e) {
            System.err.println("Eroare la crearea comenzii: " + e.getMessage());
            return;
        }

        System.out.println("\n>>> Conținutul Comenzii:");
        for (OrderItem item : myOrder.getItems()) {
            System.out.println(item.toString() + " | Preț unitar: " + item.getProduct().getPrice() + " RON");
        }

        System.out.println("\n--- 1. Total Standard (Fără reduceri) ---");
        System.out.println("Total de plată: " + String.format("%.2f", myOrder.calculateTotal()) + " RON");

        System.out.println("\n--- 2. Happy Hour (Reducere 20% la alcool) ---");
        myOrder.setStrategy(DiscountStrategies::applyHappyHourStrategy);
        System.out.println("Total de plată Happy Hour: " + String.format("%.2f", myOrder.calculateTotal()) + " RON");

        System.out.println("\n--- 3. Promoție: Pizza + Băutură Gratis ---");
        myOrder.setStrategy(DiscountStrategies::applyFreeDrinkStrategy);
        System.out.println("Total de plată Promoție: " + String.format("%.2f", myOrder.calculateTotal()) + " RON");

        System.out.println("\n--- 4. Valentine's Day (10% la tot) ---");
        myOrder.setStrategy(DiscountStrategies::ValentinesDayStrategy);
        System.out.println("Total de plată Valentine's: " + String.format("%.2f", myOrder.calculateTotal()) + " RON");
    }

    private static void addProductToOrder(Order order, Menu menu, String productName, int qty) {
        Product p = menu.searchProductInMenu(productName)
                .orElseThrow(() -> new RuntimeException("Produsul '" + productName + "' nu există în meniu!"));
        order.addProduct(p, qty);
    }

    private static void exportMenuData(List<Product> products) {
        System.out.println("\n--- EXPORT MENIU ---");
        try {
            mapper.writeValue(new File("menu_export.json"), products);
            System.out.println("Meniul a fost exportat cu succes în 'menu_export.json'.");
        } catch (IOException e) {
            System.err.println("Nu s-a putut exporta meniul (" + e.getMessage() + ")");
        }
    }
}