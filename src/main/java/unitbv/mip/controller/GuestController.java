package unitbv.mip.controller;

import javafx.collections.FXCollections;
import unitbv.mip.mapper.ProductMapper;
import unitbv.mip.model.*;
import unitbv.mip.repository.ProductRepository;
import unitbv.mip.utils.SceneManager;
import unitbv.mip.view.GuestView;
import unitbv.mip.view.LoginView;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javafx.collections.FXCollections.observableArrayList;

public class GuestController {
    private final GuestView view;
    private final ProductRepository productRepository;
    private List<Product> allProducts;

    public GuestController(GuestView view) {
        this.view = view;
        this.productRepository = new ProductRepository();

        loadData();
        attachListeners();
    }

    private void attachListeners() {
        view.getMenuTable().getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                allProducts.stream()
                        .filter(p -> p.getId().equals(newSelection.getId()))
                        .findFirst()
                        .ifPresent(this::updateDetailsPanel);
            }
        });
        view.getSearchField().textProperty().addListener((obs, old, nev) -> applyFilters());
        view.getVegCheckBox().selectedProperty().addListener((obs, old, nev) -> applyFilters());
        view.getTypeFilter().valueProperty().addListener((obs, old, nev) -> applyFilters());
        view.getMinPriceField().textProperty().addListener(obs -> applyFilters());
        view.getMaxPriceField().textProperty().addListener(obs -> applyFilters());
        view.getBackButton().setOnAction(e -> {
            LoginView loginView = new LoginView();
            new LoginController(loginView);
            SceneManager.getInstance().changeScene(loginView, "Autentificare");
        });
    }

    private void updateDetailsPanel(Product product) {
        view.getDetailNameLabel().setText(product.getName());

        view.getDetailPriceLabel().setText(String.format("%.2f RON", product.getPrice()));

        StringBuilder desc = new StringBuilder();

        desc.append("Categorie: ").append(product.getCategory()).append("\n");

        if (product instanceof Pizza) {
            Pizza p = (Pizza) product;
            desc.append("Blat: ").append(p.getCrust()).append("\n");
            desc.append("Sos: ").append(p.getSauce()).append("\n");
            desc.append("Topping-uri: ").append(p.getToppings()).append("\n");
        } else if (product instanceof Food) {
            Food f = (Food) product;
            desc.append("Gramaj: ").append(f.getWeight()).append("g\n");
            if (f.isVegetarian()) desc.append("Vegetarian\n");
        } else if (product instanceof Drink) {
            Drink d = (Drink) product;
            desc.append("Volum: ").append(d.getVolume()).append("l\n");
            if (d.isAlcoholic()) desc.append("Conține Alcool\n");
        }

        view.getDetailDescArea().setText(desc.toString());

    }

    private void loadData() {
        allProducts = productRepository.getAllProducts();

        List<ProductViewModel> models = allProducts.stream()
                .map(ProductMapper::toModel)
                .collect(Collectors.toList());

        view.getMenuTable().setItems(FXCollections.observableArrayList(models));
    }

    private void applyFilters() {
        String searchText = view.getSearchField().getText().toLowerCase();
        boolean onlyVeg = view.getVegCheckBox().isSelected();
        String typeSelection = view.getTypeFilter().getValue();

        double minPrice = parsePrice(view.getMinPriceField().getText(), 0.0);
        double maxPrice = parsePrice(view.getMaxPriceField().getText(), Double.MAX_VALUE);

        Stream<Product> stream = allProducts.stream();


        if (!searchText.isEmpty()) {
            stream = stream.filter(p -> p.getName().toLowerCase().contains(searchText));
        }

        if (onlyVeg) {
            stream = stream.filter(p -> (p instanceof Food) && ((Food) p).isVegetarian());
        }

        if ("Mâncare".equals(typeSelection)) {
            stream = stream.filter(p -> p instanceof Food);
        } else if ("Băutură".equals(typeSelection)) {
            stream = stream.filter(p -> p instanceof Drink);
        }

        stream = stream.filter(p -> p.getPrice() >= minPrice && p.getPrice() <= maxPrice);

        List<Product> filteredEntities = stream.collect(Collectors.toList());

        List<ProductViewModel> filteredModels = filteredEntities.stream()
                .map(ProductMapper::toModel)
                .collect(Collectors.toList());

        view.getMenuTable().setItems(FXCollections.observableArrayList(filteredModels));
    }

    private double parsePrice(String text, double defaultValue) {
        if (text == null || text.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return defaultValue; // Dacă scrie prostii ("abc"), ignorăm filtrul
        }
    }
}
