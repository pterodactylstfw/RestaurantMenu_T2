package unitbv.mip.model;

import unitbv.mip.strategy.DiscountStrategy;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private List<OrderItem> items = new ArrayList<>();
    private DiscountStrategy strategy;

    public static double TVA = 0.09;

    public Order() {
        this.strategy = order -> order.getSubtotalNet();
    }


    public void addProduct(Product product, int quantity) {
        if (quantity <= 0) return;

        for (OrderItem item : items) {
            if (item.getProduct().equals(product)) {
                item.addQuantity(quantity);
                return;
            }
        }

        items.add(new OrderItem(product, quantity));
    }


    public List<OrderItem> getItems() {
        return items;
    }

    public double getSubtotalNet() {
        double subtotal = 0.0;
        for (OrderItem item : items) {
            subtotal += item.getSubtotalNet();
        }
        return subtotal;
    }

    public void setStrategy(DiscountStrategy strategy) {
        this.strategy = strategy;
    }

    public double calculateTotal() {
        double discountedSubtotal = strategy.calculateDiscountedSubtotal(this);

        return discountedSubtotal * (1 + TVA);
    }

}
