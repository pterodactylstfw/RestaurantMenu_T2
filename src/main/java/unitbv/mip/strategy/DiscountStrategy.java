package unitbv.mip.strategy;

import unitbv.mip.model.Order;

@FunctionalInterface
public interface DiscountStrategy {
    abstract double calculateDiscountedSubtotal(Order order);
}
