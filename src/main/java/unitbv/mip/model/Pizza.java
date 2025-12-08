package unitbv.mip.model;

import java.util.ArrayList;
import java.util.List;

public final class Pizza extends Food {
    private final String crust;
    private final String sauce;
    private final List<String> toppings;

    // Constructorul privat
    private Pizza(String name, double price, double weight, boolean isVegetarian,
                  String crust, String sauce, List<String> toppings) {
        super(name, price, weight, Category.FELURI_PRINCIPALE, isVegetarian);

        this.crust = crust;
        this.sauce = sauce;
        this.toppings = toppings;
    }

    public String getCrust() {
        return crust;
    }

    public String getSauce() {
        return sauce;
    }

    public List<String> getToppings() {
        return new ArrayList<>(toppings);
    }

//    @Override
//    public String toString() {
//        String toppingsStr = toppings.isEmpty() ? "fără topping-uri extra" : toppings.stream().collect(Collectors.joining(", "));
//        return super.toString() + String.format("\n  -> Detalii Pizza: Blat %s, Sos %s\n  -> Topping-uri: %s",
//                crust, sauce, toppingsStr);
//    }

    public static class PizzaBuilder {
        private final String crust;
        private final String sauce;
        private List<String> toppings = new ArrayList<>();
        private String name = "Pizza CumVreiTu";
        private double price;
        private double weight = 300;
        private boolean isVegetarian = true;

        public PizzaBuilder(String crust, String sauce, double basePrice) {
            this.crust = crust;
            this.sauce = sauce;
            this.price = basePrice;
        }

        public PizzaBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public PizzaBuilder addTopping(String topping, double toppingPrice, double toppingWeight, boolean isMeat) {
            this.toppings.add(topping);
            this.price += toppingPrice;
            this.weight += toppingWeight;

            if (isMeat) {
                this.isVegetarian = false;
            }
            return this;
        }

        public Pizza build() {
            return new Pizza(name, price, weight, isVegetarian, crust, sauce, new ArrayList<>(toppings));
        }
    }
}