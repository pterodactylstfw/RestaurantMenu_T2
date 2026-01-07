package unitbv.mip.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.scene.control.*;

import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import unitbv.mip.config.ConfigManager;
import unitbv.mip.model.Order;
import unitbv.mip.model.Product;
import unitbv.mip.model.Role;
import unitbv.mip.model.User;
import unitbv.mip.repository.OrderRepository;
import unitbv.mip.repository.ProductRepository;
import unitbv.mip.repository.UserRepository;
import unitbv.mip.service.AuthService;
import unitbv.mip.service.MenuService;
import unitbv.mip.strategy.DiscountStrategies;
import unitbv.mip.utils.SceneManager;
import unitbv.mip.view.AdminView;
import unitbv.mip.view.LoginView;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminController {

    private final AdminView view;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final MenuService menuService;

    private final ExecutorService executorService;

    public AdminController(AdminView view) {
        this.view = view;
        this.userRepository = new UserRepository();
        this.productRepository = new ProductRepository();
        this.orderRepository = new OrderRepository();
        this.menuService = new MenuService();
        this.executorService = Executors.newSingleThreadExecutor();

        refreshLightData();
        attachListeners();
        loadOfferState();
    }

    private void refreshLightData() {
        view.getStaffTable().setItems(FXCollections.observableArrayList(userRepository.findAllStaff()));
        view.getMenuTable().setItems(FXCollections.observableArrayList(productRepository.getAllProducts()));
    }

    private void refreshAllData() {
        view.getStaffTable().setItems(FXCollections.observableArrayList(userRepository.findAllStaff()));
        view.getMenuTable().setItems(FXCollections.observableArrayList(productRepository.getAllProducts()));
        view.getGlobalHistoryTable().setItems(FXCollections.observableArrayList(orderRepository.findAll()));
    }

    private void attachListeners() {
        view.getAddStaffButton().setOnAction(e -> addStaff());
        view.getDeleteStaffButton().setOnAction(e -> deleteStaff());
        view.getRefreshHistoryButton().setOnAction(e -> loadHistoryAsync());
        view.getDeleteProductButton().setOnAction(e -> deleteProduct());
        view.getEditProductButton().setOnAction(e -> editProduct());
        view.getExportJsonButton().setOnAction(e -> exportJson());
        view.getImportJsonButton().setOnAction(e -> importJsonAsync());

        view.getSaveOffersButton().setOnAction(e -> saveOffers());

        view.getLogoutButton().setOnAction(e -> {
            new AuthService().logout();
            LoginView loginView = new LoginView();
            new LoginController(loginView);
            SceneManager.getInstance().changeScene(loginView, "Autentificare");
        });
    }

    private void importJsonAsync() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importă Meniu din JSON");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File file = fileChooser.showOpenDialog(view.getScene().getWindow());

        if (file == null) return;

        view.setLoading(true);

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                ObjectMapper mapper = new ObjectMapper();
                List<Product> importedProducts = mapper.readValue(file, new TypeReference<List<Product>>() {});

                int addedCount = 0;
                int skippedCount = 0;

                Thread.sleep(1000);

                for (Product p : importedProducts) {
                    if (productRepository.findByName(p.getName()).isPresent()) {
                        skippedCount++;
                    } else {
                        p.setId(null);
                        productRepository.addProduct(p);
                        addedCount++;
                    }
                }
                return String.format("Import finalizat!\n\nProduse noi: %d\nDuplicate ignorate: %d", addedCount, skippedCount);
            }
        };

        task.setOnSucceeded(e -> {
            refreshLightData();
            view.setLoading(false);
            showAlert("Raport Import", task.getValue());
        });

        task.setOnFailed(e -> {
            view.setLoading(false);
            showAlert("Eroare Import", "Fișier invalid sau corupt.");
            task.getException().printStackTrace();
        });

        executorService.submit(task);
    }

    public void shutdown() {
        executorService.shutdown();
    }


    private void loadHistoryAsync() {
        view.setLoading(true);

        Task<List<Order>> task = new Task<>() {
            @Override
            protected List<Order> call() throws Exception {
                Thread.sleep(1500);

                return orderRepository.findAll();
            }
        };

        task.setOnSucceeded(e -> {
            view.getGlobalHistoryTable().setItems(FXCollections.observableArrayList(task.getValue()));
            view.setLoading(false);
        });

        task.setOnFailed(e -> {
            view.setLoading(false);
            showAlert("Eroare", "Nu s-a putut încărca istoricul.");
            task.getException().printStackTrace();
        });

        executorService.submit(task);
    }

    private void editProduct() {
        Product selected = view.getMenuTable().getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Atenție", "Selectează un produs din tabel pentru a-l edita.");
            return;
        }

        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Editare Produs");
        dialog.setHeaderText("Modifică detaliile pentru: " + selected.getName());

        ButtonType saveButtonType = new ButtonType("Salvează", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField nameField = new TextField(selected.getName());
        TextField priceField = new TextField(String.valueOf(selected.getPrice()));

        grid.add(new Label("Nume:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Preț:"), 0, 1);
        grid.add(priceField, 1, 1);

        TextField extraField = new TextField();
        Label extraLabel = new Label();

        if (selected instanceof unitbv.mip.model.Food) {
            extraLabel.setText("Gramaj (g):");
            extraField.setText(String.valueOf(((unitbv.mip.model.Food) selected).getWeight()));
        } else if (selected instanceof unitbv.mip.model.Drink) {
            extraLabel.setText("Volum (l):");
            extraField.setText(String.valueOf(((unitbv.mip.model.Drink) selected).getVolume()));
        }

        grid.add(extraLabel, 0, 2);
        grid.add(extraField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    selected.setName(nameField.getText());
                    selected.setPrice(Double.parseDouble(priceField.getText()));

                    double extraValue = Double.parseDouble(extraField.getText());

                    if (selected instanceof unitbv.mip.model.Food) {
                        ((unitbv.mip.model.Food) selected).setWeight(extraValue);
                    } else if (selected instanceof unitbv.mip.model.Drink) {
                        ((unitbv.mip.model.Drink) selected).setVolume(extraValue);
                    }

                    return selected;
                } catch (NumberFormatException e) {
                    showAlert("Eroare", "Prețul și Gramajul/Volumul trebuie să fie numere!");
                    return null;
                }
            }
            return null;
        });

        java.util.Optional<Product> result = dialog.showAndWait();

        result.ifPresent(product -> {
            productRepository.updateProduct(product);
            refreshAllData();
            showAlert("Succes", "Produsul a fost actualizat!");
        });
    }

    private void addStaff() {
        String user = view.getUsernameField().getText();
        String pass = view.getPasswordField().getText();
        if (user.isEmpty() || pass.isEmpty()) return;

        User newUser = new User();
        newUser.setUsername(user);
        newUser.setPassword(pass);
        newUser.setRole(Role.STAFF);

        userRepository.save(newUser);
        view.getUsernameField().clear();
        view.getPasswordField().clear();
        refreshAllData();
    }

    private void deleteStaff() {
        User selected = view.getStaffTable().getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Atenție", "Selectați un angajat pentru ștergere.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmare Ștergere");
        alert.setHeaderText("Sunteți sigur că vreți să concediați angajatul " + selected.getUsername() + "?");
        alert.setContentText("ATENȚIE: Toate comenzile asociate acestui ospătar vor fi șterse definitiv din istoric!");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            userRepository.delete(selected);
            refreshAllData();
        }
    }

    private void deleteProduct() {
        Product selected = view.getMenuTable().getSelectionModel().getSelectedItem();
        if (selected != null) {
            productRepository.delete(selected);
            refreshAllData();
        }
    }

    private void saveOffers() {
        DiscountStrategies.HAPPY_HOUR_ACTIVE = view.getHappyHourCheck().isSelected();
        DiscountStrategies.MEAL_DEAL_ACTIVE = view.getMealDealCheck().isSelected();
        DiscountStrategies.PARTY_PACK_ACTIVE = view.getPartyPackCheck().isSelected();
        ConfigManager.saveCurrentState();
        showAlert("Succes", "Regulile au fost actualizate și salvate!");
    }

    private void loadOfferState() {
        view.getHappyHourCheck().setSelected(DiscountStrategies.HAPPY_HOUR_ACTIVE);
        view.getMealDealCheck().setSelected(DiscountStrategies.MEAL_DEAL_ACTIVE);
        view.getPartyPackCheck().setSelected(DiscountStrategies.PARTY_PACK_ACTIVE);
    }

    private void exportJson() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvează Meniul ca JSON");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        fileChooser.setInitialFileName("meniu_restaurant.json");

        File file = fileChooser.showSaveDialog(view.getScene().getWindow());

        if (file != null) {
            ObjectMapper mapper = new ObjectMapper();

            try {
                List<Product> products = productRepository.getAllProducts();

                mapper.writerFor(new TypeReference<List<Product>>() {})
                        .withDefaultPrettyPrinter()
                        .writeValue(file, products);

                showAlert("Succes", "Meniul a fost exportat cu succes în:\n" + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Eroare Export", "Nu s-a putut salva fișierul: " + e.getMessage());
            }
        }
    }

    private void importJson() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importă Meniu din JSON");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));

        File file = fileChooser.showOpenDialog(view.getScene().getWindow());

        if (file != null) {
            ObjectMapper mapper = new ObjectMapper();

            try {
                List<Product> importedProducts = mapper.readValue(file, new TypeReference<List<Product>>() {});

                int addedCount = 0;
                int skippedCount = 0;

                for (Product p : importedProducts) {
                    if (productRepository.findByName(p.getName()).isPresent()) {
                        System.out.println("Produsul '" + p.getName() + "' există deja. Se sare.");
                        skippedCount++;
                        continue;
                    }

                    p.setId(null);
                    productRepository.addProduct(p);
                    addedCount++;
                }

                refreshAllData();

                String message = String.format("Import finalizat!\n\nProduse noi adăugate: %d\nDuplicate ignorate: %d", addedCount, skippedCount);
                showAlert("Raport Import", message);

            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Eroare Import", "Fișierul este corupt sau invalid.\n" + e.getMessage());
            }
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}