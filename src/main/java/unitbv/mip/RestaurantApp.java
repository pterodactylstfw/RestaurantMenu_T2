package unitbv.mip;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import unitbv.mip.model.*;

import java.util.List;

public class RestaurantApp extends Application {
    private ObservableList<Product> productsList;

    private TextField nameField;
    private TextField priceField;
    private TextField extraField;
    private Label extraLabel;

    @Override
    public void start(Stage primaryStage) {
        productsList = FXCollections.observableArrayList(createDummyProducts());

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

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

                    if(newProduct != null)
                        bindProductToForm(newProduct);
                });

        Scene scene = new Scene(root,800,600);
        primaryStage.setTitle("Meniu Restaurant 'La Andrei'");
        primaryStage.setScene(scene);
        primaryStage.show();
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

    private List<Product> createDummyProducts() {
        return List.of(
                new Food("Carne de pui", 25.5, 350, Category.FELURI_PRINCIPALE, false),
        new Food("Paste Bolognese", 28.0, 300, Category.FELURI_PRINCIPALE, false),
        new Food("Pizza Margherita", 35.0, 400, Category.FELURI_PRINCIPALE, true),
        new Food("Salată Caesar", 30.0, 250, Category.APERITIVE, false),
        new Food("Lava Cake", 20.0, 150, Category.DESERTURI, true),

        new Drink("Apă", 9.0, 0.5, false),
        new Drink("Suc de portocale", 12.0, 0.33, false),
        new Drink("Bere", 10.0, 0.5, true),

        new Pizza.PizzaBuilder("Pufos", "Dulce", 20.0)
                .withName("Pizza Casei")
                .addTopping("Mozzarella", 5.0, 50, false)
                .addTopping("Bacon", 6.0, 40, true)
                .addTopping("Ciuperci", 3.0, 30, false)
                .build()
        );


    }

    public static void main(String[] args) {
        launch(args);
    }
}
