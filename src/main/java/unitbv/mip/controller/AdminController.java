package unitbv.mip.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import unitbv.mip.config.ConfigManager;
import unitbv.mip.mapper.OrderMapper;
import unitbv.mip.mapper.ProductMapper;
import unitbv.mip.model.*;
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
import java.util.stream.Collectors;

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
        List<ProductViewModel> productModels = productRepository.getAllProducts().stream()
                .map(ProductMapper::toModel)
                .collect(Collectors.toList());

        view.getMenuTable().setItems(FXCollections.observableArrayList(productModels));
    }

    private void refreshAllData() {
        refreshLightData();
        loadHistoryAsync();
    }

    private void attachListeners() {
        // staff
        view.getAddStaffButton().setOnAction(e -> addStaff());
        view.getDeleteStaffButton().setOnAction(e -> deleteStaff());

        view.getStaffTable().setOnMouseClicked(event -> { // double click pentru edit
            if (event.getClickCount() == 2 && view.getStaffTable().getSelectionModel().getSelectedItem() != null) {
                editStaff();
            }
        });

        // istoric
        view.getRefreshHistoryButton().setOnAction(e -> loadHistoryAsync());

        // meniu
        view.getAddProductButton().setOnAction(e -> addProduct());
        view.getDeleteProductButton().setOnAction(e -> deleteProduct());
        view.getEditProductButton().setOnAction(e -> editProduct());
        view.getExportJsonButton().setOnAction(e -> exportJson());
        view.getImportJsonButton().setOnAction(e -> importJsonAsync());

        // oferte
        view.getSaveOffersButton().setOnAction(e -> saveOffers());

        // logout
        view.getLogoutButton().setOnAction(e -> {
            new AuthService().logout();
            LoginView loginView = new LoginView();
            new LoginController(loginView);
            SceneManager.getInstance().changeScene(loginView, "Autentificare");
        });
    }

    // metode staff

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

    private void editStaff() {
        User selected = view.getStaffTable().getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Editare Angajat");
        dialog.setHeaderText("Modifică datele pentru: " + selected.getUsername());

        ButtonType saveButtonType = new ButtonType("Salvează", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField usernameField = new TextField(selected.getUsername());
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Lasă gol pt a nu schimba");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Parolă Nouă:"), 0, 1);
        grid.add(passwordField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                selected.setUsername(usernameField.getText());
                // Schimbăm parola doar dacă a scris ceva în câmp
                if (!passwordField.getText().isEmpty()) {
                    selected.setPassword(passwordField.getText());
                }
                return selected;
            }
            return null;
        });

        Optional<User> result = dialog.showAndWait();

        result.ifPresent(user -> {
            userRepository.update(user);
            refreshAllData();
            showAlert("Succes", "Datele angajatului au fost actualizate!");
        });
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
            // stergem comenzile asociate ospătarului înainte de a-l șterge ( cascade)
            List<Order> allOrders = orderRepository.findAll();
            for (Order o : allOrders) {
                if (o.getWaiter() != null && o.getWaiter().getId().equals(selected.getId())) {
                    orderRepository.delete(o);
                }
            }

            // acum sterg ospatar
            userRepository.delete(selected);
            refreshAllData();
            showAlert("Succes", "Angajatul și istoricul său au fost șterse.");
        }
    }

    // metode meniu

    private void addProduct() {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Adăugare Produs Nou");
        dialog.setHeaderText("Completează detaliile noului produs");

        ButtonType saveButtonType = new ButtonType("Adaugă", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Nume produs");

        TextField priceField = new TextField();
        priceField.setPromptText("Preț (ex: 25.5)");

        ComboBox<unitbv.mip.model.Category> categoryBox = new ComboBox<>();
        categoryBox.getItems().setAll(unitbv.mip.model.Category.values());
        categoryBox.setPromptText("Categorie");

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Mâncare (Food)", "Băutură (Drink)");
        typeBox.setValue("Mâncare (Food)"); // Default

        Label extraLabel = new Label("Gramaj (g):");
        TextField extraField = new TextField();
        extraField.setPromptText("ex: 350");

        typeBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.contains("Mâncare")) {
                extraLabel.setText("Gramaj (g):");
                extraField.setPromptText("ex: 350");
            } else {
                extraLabel.setText("Volum (l):");
                extraField.setPromptText("ex: 0.5");
            }
        });

        grid.add(new Label("Tip:"), 0, 0);
        grid.add(typeBox, 1, 0);
        grid.add(new Label("Nume:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Preț:"), 0, 2);
        grid.add(priceField, 1, 2);
        grid.add(new Label("Categorie:"), 0, 3);
        grid.add(categoryBox, 1, 3);
        grid.add(extraLabel, 0, 4);
        grid.add(extraField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String name = nameField.getText();
                    double price = Double.parseDouble(priceField.getText());
                    unitbv.mip.model.Category category = categoryBox.getValue();
                    String type = typeBox.getValue();
                    double extra = Double.parseDouble(extraField.getText());

                    if (name.isEmpty() || category == null) {
                        throw new IllegalArgumentException("Toate câmpurile sunt obligatorii!");
                    }

                    Product newProduct;
                    if (type.contains("Mâncare")) {
                        Food food = new Food();
                        food.setName(name);
                        food.setPrice(price);
                        food.setCategory(category);
                        food.setWeight(extra);
                        food.setVegetarian(false);
                        newProduct = food;
                    } else {
                        Drink drink = new Drink();
                        drink.setName(name);
                        drink.setPrice(price);
                        drink.setCategory(category);
                        drink.setVolume(extra);
                        drink.setAlcoholic(false);
                        newProduct = drink;
                    }

                    newProduct.setActive(true);

                    return newProduct;

                } catch (NumberFormatException e) {
                    showAlert("Eroare", "Prețul și Gramajul/Volumul trebuie să fie numere valide!");
                    return null;
                } catch (Exception e) {
                    showAlert("Eroare", "Date invalide: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        Optional<Product> result = dialog.showAndWait();

        result.ifPresent(product -> {
            productRepository.addProduct(product);
            refreshAllData();
            showAlert("Succes", "Produsul a fost adăugat în meniu!");
        });
    }

    private void deleteProduct() {
        ProductViewModel selected = view.getMenuTable().getSelectionModel().getSelectedItem();
        if (selected != null) {
            Optional<Product> productOpt = productRepository.findById(selected.getId());
            productOpt.ifPresent(product -> {
                productRepository.delete(product);
                refreshAllData();
            });
        }
    }

    private void editProduct() {
        ProductViewModel selectedViewModel = view.getMenuTable().getSelectionModel().getSelectedItem();
        if (selectedViewModel == null) {
            showAlert("Atenție", "Selectează un produs din tabel pentru a-l edita.");
            return;
        }

        Optional<Product> productOpt = productRepository.findById(selectedViewModel.getId());

        if (productOpt.isEmpty()) {
            showAlert("Eroare", "Produsul nu mai există în baza de date.");
            return;
        }

        Product selected = productOpt.get();

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

        if (selected instanceof Food) {
            extraLabel.setText("Gramaj (g):");
            extraField.setText(String.valueOf(((Food) selected).getWeight()));
        } else if (selected instanceof Drink) {
            extraLabel.setText("Volum (l):");
            extraField.setText(String.valueOf(((Drink) selected).getVolume()));
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

                    if (selected instanceof Food) {
                        ((Food) selected).setWeight(extraValue);
                    } else if (selected instanceof Drink) {
                        ((Drink) selected).setVolume(extraValue);
                    }

                    return selected;
                } catch (NumberFormatException e) {
                    showAlert("Eroare", "Prețul și Gramajul/Volumul trebuie să fie numere!");
                    return null;
                }
            }
            return null;
        });

        Optional<Product> result = dialog.showAndWait();

        result.ifPresent(product -> {
            productRepository.updateProduct(product);
            refreshAllData();
            showAlert("Succes", "Produsul a fost actualizat!");
        });
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

    // metode istoric

    private void loadHistoryAsync() {
        view.setLoading(true);

        Task<List<OrderViewModel>> task = new Task<>() {
            @Override
            protected List<OrderViewModel> call() throws Exception {
                Thread.sleep(1000); // simulare 1 sec

                // luam datele brute din baza de date
                List<Order> rawOrders = orderRepository.findAll();

                // folosesc OrderMapper pentru a converti fiecare Order în OrderViewModel
                return rawOrders.stream()
                        .map(OrderMapper::toModel)
                        .collect(Collectors.toList());
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

    // metode oferte

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

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void shutdown() {
        executorService.shutdown();
    }
}