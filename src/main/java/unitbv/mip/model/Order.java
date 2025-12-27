package unitbv.mip.model;

import jakarta.persistence.*;
import unitbv.mip.strategy.DiscountStrategy;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "waiter_id")
    private User waiter;

    @ManyToOne
    @JoinColumn(name = "table_id")
    private RestaurantTable table;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Transient
    private DiscountStrategy strategy;

    private double totalAmount;

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

    public double getSubtotalNet() {
        return items.stream().mapToDouble(OrderItem::getSubtotalNet).sum();
    }

    public double calculateTotal() {
        double discountedSubtotal = strategy.calculateDiscountedSubtotal(this);

        return discountedSubtotal * (1 + TVA);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getWaiter() { return waiter; }
    public void setWaiter(User waiter) { this.waiter = waiter; }

    public RestaurantTable getTable() { return table; }
    public void setTable(RestaurantTable table) { this.table = table; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public void setStrategy(DiscountStrategy strategy) { this.strategy = strategy; }

}
