package unitbv.mip.service;

import unitbv.mip.model.Category;
import unitbv.mip.model.Food;
import unitbv.mip.model.Product;

import java.util.*;
import java.util.stream.Collectors;

public class Menu {
    private Map<Category, List<Product>> productsByCategory = new HashMap<>();

    public void addProduct(Product product) {
        productsByCategory.computeIfAbsent(product.getCategory(),
                        ctg -> new ArrayList<>()).add(product);
    }

    public List<Product> getProductsByCategory(Category category) {
        return productsByCategory.getOrDefault(category, new ArrayList<>());
    }

    public List<Product> getVegetarianFoodsSorted() {
        return productsByCategory.values().stream()
                .flatMap(List::stream)
                .filter(f -> f instanceof Food)
                .map(f -> (Food) f)
                .filter(Food::isIsVegetarian)
                .sorted(Comparator.comparing(Product::getName))
                .collect(Collectors.toList());
    }

    public double getAverageDessertPrice() {
        return getProductsByCategory(Category.DESERTURI).stream()
                .mapToDouble(Product::getPrice)
                .average()
                .orElse(0.0);
    }

    public boolean hasExpensiveProduct() {
        return productsByCategory.values().stream()
                .flatMap(List::stream)
                .anyMatch(p -> p.getPrice() > 100.0);
    }

    public Optional<Product> searchProductInMenu(String name) {
        return productsByCategory.values().stream()
                .flatMap(List::stream)
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public void printMenu() {
        productsByCategory.forEach((category, products) -> {
            System.out.println("\n--- " + category + " ---");
            products.forEach(System.out::println);
        });
    }
}
