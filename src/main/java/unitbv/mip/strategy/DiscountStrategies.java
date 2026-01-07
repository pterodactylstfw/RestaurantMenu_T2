package unitbv.mip.strategy;

import unitbv.mip.model.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DiscountStrategies {

    public static boolean HAPPY_HOUR_ACTIVE = false;
    public static boolean MEAL_DEAL_ACTIVE = false;
    public static boolean PARTY_PACK_ACTIVE = false;

    public static double calculateTotalWithDiscounts(Order order) {
        double subtotal = order.getSubtotalNet();
        double totalDiscount = 0.0;

        if (HAPPY_HOUR_ACTIVE) {
            totalDiscount += calculateHappyHourDiscount(order);
        }

        if (MEAL_DEAL_ACTIVE) {
            totalDiscount += calculateMealDealDiscount(order);
        }

        if (PARTY_PACK_ACTIVE) {
            totalDiscount += calculatePartyPackDiscount(order);
        }

        return Math.max(0, subtotal - totalDiscount);
    }

    public static boolean isPizza(Product p) {
        if (p instanceof Pizza) return true;
        return p.getName() != null && p.getName().toLowerCase().contains("pizza");
    }

    public static double calculateHappyHourDiscount(Order order) {
        List<Product> drinks = new ArrayList<>();

        for (OrderItem item : order.getItems()) {
            if (item.getProduct() instanceof Drink) {
                for (int i = 0; i < item.getQuantity(); i++) {
                    drinks.add(item.getProduct());
                }
            }
        }

        drinks.sort(Comparator.comparingDouble(Product::getPrice));

        int drinksToDiscount = drinks.size() / 2;
        double discount = 0.0;

        for (int i = 0; i < drinksToDiscount; i++) {
            discount += drinks.get(i).getPrice() * 0.5;
        }

        if (discount > 0) System.out.println("[Happy Hour] Discount aplicat: " + discount);
        return discount;
    }

    public static double calculateMealDealDiscount(Order order) {
        boolean hasPizza = false;
        List<Product> desserts = new ArrayList<>();

        for (OrderItem item : order.getItems()) {
            Product p = item.getProduct();

            if (isPizza(p)) {
                hasPizza = true;
            }

            if (p.getCategory() == Category.DESERTURI) {
                for (int i = 0; i < item.getQuantity(); i++) {
                    desserts.add(p);
                }
            }
        }

        if (hasPizza && !desserts.isEmpty()) {
            Product cheapestDessert = desserts.stream()
                    .min(Comparator.comparingDouble(Product::getPrice))
                    .orElse(null);

            if (cheapestDessert != null) {
                double discount = cheapestDessert.getPrice() * 0.25;
                System.out.println("[Meal Deal] Discount desert: " + discount);
                return discount;
            }
        }
        return 0.0;
    }

    public static double calculatePartyPackDiscount(Order order) {
        List<Product> pizzas = new ArrayList<>();

        for (OrderItem item : order.getItems()) {
            if (isPizza(item.getProduct())) {
                for (int i = 0; i < item.getQuantity(); i++) {
                    pizzas.add(item.getProduct());
                }
            }
        }

        if (pizzas.size() >= 4) {
            Product cheapestPizza = pizzas.stream()
                    .min(Comparator.comparingDouble(Product::getPrice))
                    .orElse(null);

            if (cheapestPizza != null) {
                double discount = cheapestPizza.getPrice();
                System.out.println("[Party Pack] Pizza gratis: " + discount);
                return discount;
            }
        }
        return 0.0;
    }
}