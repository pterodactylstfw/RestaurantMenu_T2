package unitbv.mip.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import unitbv.mip.mapper.ProductMapper;
import unitbv.mip.model.*;
import unitbv.mip.repository.OrderRepository;
import unitbv.mip.repository.ProductRepository;
import unitbv.mip.repository.RestaurantTableRepository;
import unitbv.mip.service.AuthService;
import unitbv.mip.strategy.DiscountStrategies;
import unitbv.mip.utils.SceneManager;
import unitbv.mip.view.LoginView;
import unitbv.mip.view.StaffHistoryView;
import unitbv.mip.view.StaffView;
import unitbv.mip.view.TableSelectionView;

import java.util.List;
import java.util.stream.Collectors;

public class StaffController {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final RestaurantTableRepository tableRepository;

    private int currentTableNumber;
    private final ObservableList<OrderItem> currentOrderItems = FXCollections.observableArrayList();

    private List<Product> allProducts;

    public StaffController() {
        this.productRepository = new ProductRepository();
        this.orderRepository = new OrderRepository();
        this.tableRepository = new RestaurantTableRepository();
        showTableSelection();
    }

    private void showTableSelection() {
        TableSelectionView view = new TableSelectionView();

        for (Button btn : view.getTableButtons()) {
            if ("logout".equals(btn.getId()) || "history".equals(btn.getId())) {
                // ... logica existenta pt butoane speciale
                if ("logout".equals(btn.getId())) btn.setOnAction(e -> logout());
                // if history ...
            } else {
                int tableNum = (int) btn.getUserData();

                boolean occupied = tableRepository.isTableOccupied(tableNum);

                if (occupied) {
                    btn.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-font-size: 16px;");
                    btn.setText("Masa " + tableNum + "\n(Ocupat)");
                } else {
                    btn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px;");
                }

                btn.setOnAction(e -> {
                    startOrderForTable(tableNum);
                });
            }
        }
        view.getHistoryButton().setOnAction(e -> showHistory());
        SceneManager.getInstance().changeScene(view, "Staff - Selectează Masa");
    }

    private void startOrderForTable(int tableNumber) {
        this.currentTableNumber = tableNumber;
        this.currentOrderItems.clear();

        tableRepository.updateStatus(tableNumber, true);

        StaffView orderView = new StaffView();
        orderView.getTableLabel().setText("Masa " + tableNumber);

        allProducts = productRepository.getAllProducts();
        List<ProductViewModel> viewModels = allProducts.stream()
                .map(ProductMapper::toModel)
                .collect(Collectors.toList());

        orderView.getMenuTable().setItems(FXCollections.observableArrayList(viewModels));

        orderView.getCartTable().setItems(currentOrderItems);

        orderView.getAddButton().setOnAction(e -> {
            ProductViewModel selectedVM = orderView.getMenuTable().getSelectionModel().getSelectedItem();

            if (selectedVM != null) {
                Product selectedProduct = allProducts.stream()
                        .filter(p -> p.getId().equals(selectedVM.getId()))
                        .findFirst()
                        .orElse(null);

                if (selectedProduct != null) {
                    addToCart(selectedProduct, orderView.getQuantity());
                    updateTotal(orderView);
                }
            }
        });

        orderView.getRemoveButton().setOnAction(e -> {
            OrderItem selected = orderView.getCartTable().getSelectionModel().getSelectedItem();
            if(selected != null) {
                currentOrderItems.remove(selected);
                updateTotal(orderView);
            }
        });

        orderView.getPlaceOrderButton().setOnAction(e -> finalizeOrder());

        orderView.getBackButton().setOnAction(e -> showTableSelection());

        SceneManager.getInstance().changeScene(orderView, "Staff - Comandă pentru Masa " + tableNumber);
    }

    private void finalizeOrder() {
        if(currentOrderItems.isEmpty()) {
            showAlert("Coșul este gol!", "Te rog, adaugă produse în comandă înainte de a finaliza.");
            return;
        }

        try {
            Order order = new Order();
            RestaurantTable tableFromDb = tableRepository.findOrCreate(currentTableNumber);
            order.setTable(tableFromDb);
            order.setWaiter(AuthService.getCurrentUser());

            for(OrderItem item : currentOrderItems) {
                item.setOrder(order);
                order.getItems().add(item);
            }

            double netTotal = DiscountStrategies.calculateTotalWithDiscounts(order);
            double finalTotal = netTotal * (1 + Order.TVA);
            order.setTotalAmount(finalTotal);

            orderRepository.saveOrder(order);

            tableRepository.updateStatus(currentTableNumber, false);

            showAlert("Succes", "Comanda a fost trimisă la bucătărie!\nTotal: " + String.format("%.2f", finalTotal));
            showTableSelection();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Eroare", "Nu s-a putut salva comanda: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void addToCart(Product product, int quantity) {
        for (OrderItem item : currentOrderItems) {
            if (item.getProduct().getId().equals(product.getId())) {
                item.addQuantity(quantity);
                int idx = currentOrderItems.indexOf(item);
                currentOrderItems.set(idx, item);
                return;
            }
        }
        currentOrderItems.add(new OrderItem(product, quantity));
    }

    private void updateTotal(StaffView view) {
        Order tempOrder = new Order();
        tempOrder.setItems(currentOrderItems);

        double subtotal = tempOrder.getSubtotalNet();
        StringBuilder discountText = new StringBuilder();
        double totalDiscount = 0.0;


        if (DiscountStrategies.HAPPY_HOUR_ACTIVE) {
            double disc = DiscountStrategies.calculateHappyHourDiscount(tempOrder);
            if (disc > 0.01) {
                discountText.append(String.format("Happy Hour: -%.2f RON\n", disc));
                totalDiscount += disc;
            }
        }

        if (DiscountStrategies.MEAL_DEAL_ACTIVE) {
            double disc = DiscountStrategies.calculateMealDealDiscount(tempOrder);
            if (disc > 0.01) {
                discountText.append(String.format("Meal Deal: -%.2f RON\n", disc));
                totalDiscount += disc;
            }
        }

        if (DiscountStrategies.PARTY_PACK_ACTIVE) {
            double disc = DiscountStrategies.calculatePartyPackDiscount(tempOrder);
            if (disc > 0.01) {
                discountText.append(String.format("Party Pack: -%.2f RON\n", disc));
                totalDiscount += disc;
            }
        }

        double netTotal = Math.max(0, subtotal - totalDiscount);
        double finalTotal = netTotal * (1 + Order.TVA);

        view.getDiscountDetailsLabel().setText(discountText.toString());

        view.getDiscountDetailsLabel().setWrapText(true);

        view.getTotalLabel().setText(String.format("TOTAL: %.2f RON", finalTotal));
    }

    private void showHistory() {
        StaffHistoryView historyView = new StaffHistoryView();

        User currentUser = AuthService.getCurrentUser();
        List<Order> myOrders = orderRepository.findByWaiter(currentUser);
        historyView.getHistoryTable().setItems(FXCollections.observableArrayList(myOrders));

        historyView.getBackButton().setOnAction(e -> showTableSelection());

        SceneManager.getInstance().changeScene(historyView, "Istoric Comenzi - Staff");
    }

    private void logout() {
        new AuthService().logout();
        LoginView loginView = new LoginView();
        new LoginController(loginView);
        SceneManager.getInstance().changeScene(loginView, "Autentificare");
    }
}
