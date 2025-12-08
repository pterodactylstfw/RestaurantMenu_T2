package unitbv.mip;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import unitbv.mip.config.PersistenceManager;
import unitbv.mip.model.*;
import unitbv.mip.repository.ProductRepository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class RestaurantApp extends Application {
    private ObservableList<Product> productsList;

    private TextField nameField;
    private TextField priceField;
    private TextField extraField;
    private Label extraLabel;


    private final ObjectMapper mapper = new ObjectMapper();
    private ProductRepository repository;

    @Override
    public void start(Stage primaryStage) {
        repository = new ProductRepository();

        List<Product> dbProducts = repository.getAllProducts();

        productsList = FXCollections.observableArrayList(dbProducts);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        MenuBar menuBar = createMenuBar(primaryStage);
        root.setTop(menuBar);

        ListView<Product> listView = new ListView<>(productsList);
        listView.setPrefWidth(200);
        root.setLeft(listView);

        VBox form = createDetailsForm();
        root.setCenter(form);

        listView.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldProduct, newProduct) -> {
                    if (oldProduct != null) {
                        unbindProductFromForm(oldProduct);
                    }
                    if (newProduct != null) {
                        bindProductToForm(newProduct);
                    }
                });

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Meniu Restaurant 'La Andrei'");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private MenuBar createMenuBar(Stage stage) {
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");

        MenuItem exportItem = new MenuItem("Export JSON");
        exportItem.setOnAction(e -> exportToJson(stage));

        MenuItem importItem = new MenuItem("Import JSON");
        importItem.setOnAction(e -> importFromJson(stage));

        fileMenu.getItems().addAll(exportItem, importItem);
        menuBar.getMenus().add(fileMenu);
        return menuBar;
    }

    private void exportToJson(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvează Meniul");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {

                mapper.writerFor(new com.fasterxml.jackson.core.type.TypeReference<List<Product>>() {})
                        .withDefaultPrettyPrinter()
                        .writeValue(file, new ArrayList<>(productsList)); // forteaza json sa respect adnotarile de pe parinte

                System.out.println("Export reusit in: " + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                //showAlert("Eroare Export", "Nu s-a putut salva fișierul: " + e.getMessage());
            }
        }
    }

    private void importFromJson(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Încarcă Meniu");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                List<Product> importedProducts = mapper.readValue(file, new TypeReference<List<Product>>() {});

                int addedCount = 0;
                int skippedCount = 0;

                for (Product p : importedProducts) {
                    Product existingProduct = repository.findByName(p.getName()); // verif duplicate

                    if (existingProduct == null) {
                        // nu exista - adaug
                        p.setId(null); // resetam id-ul pentru a crea o noua intrare in baza de date
                        repository.addProduct(p);
                        addedCount++;
                    } else {
                        // Exista deja - sar
                        System.out.println("Produsul '" + p.getName() + "' există deja. Ignorat.");
                        skippedCount++;
                    }
                }

                // reincarc tabelul
                productsList.setAll(repository.getAllProducts());

                String msg = String.format("Import finalizat.\nAdăugate: %d\nIgnorate (duplicate): %d", addedCount, skippedCount);

                System.out.println(msg);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Rezultat Import");
                alert.setHeaderText(null);
                alert.setContentText(msg);
                alert.showAndWait();

            } catch (IOException e) {
                e.printStackTrace();
                //showAlert("Eroare Import", "Fișier corupt sau format invalid: " + e.getMessage());
            }
        }
    }

    @Override
    public void stop() {
        PersistenceManager.getInstance().close();
    }

    private void bindProductToForm(Product product) {
        nameField.textProperty().unbind();
        priceField.textProperty().unbind();
        extraField.textProperty().unbind();

        nameField.textProperty().bindBidirectional(product.nameProperty());

        priceField.textProperty().bindBidirectional(product.priceProperty(), new javafx.util.converter.NumberStringConverter());

        if (product instanceof Food food) {
            extraLabel.setText("Gramaj (g):");
            extraField.textProperty().bindBidirectional(food.weightProperty(), new javafx.util.converter.NumberStringConverter());
        } else if (product instanceof Drink drink) {
            extraLabel.setText("Volum (l):");
            extraField.textProperty().bindBidirectional(drink.volumeProperty(), new javafx.util.converter.NumberStringConverter());
        }
    }

    private void unbindProductFromForm(Product product) {
        nameField.textProperty().unbindBidirectional(product.nameProperty());

        priceField.textProperty().unbindBidirectional(product.priceProperty());

        if (product instanceof Food food) {
            extraField.textProperty().unbindBidirectional(food.weightProperty());
        } else if (product instanceof Drink drink) {
            extraField.textProperty().unbindBidirectional(drink.volumeProperty());
        }
    }

    private VBox createDetailsForm() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(0, 0, 0, 20));

        Label titleLabel = new Label("Detalii Produs");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        nameField = new TextField();
        priceField = new TextField();
        extraField = new TextField();
        extraLabel = new Label("Extra:");

        box.getChildren().addAll(
                titleLabel,
                new Label("Nume:"), nameField,
                new Label("Preț (RON):"), priceField,
                extraLabel, extraField
        );

        return box;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
