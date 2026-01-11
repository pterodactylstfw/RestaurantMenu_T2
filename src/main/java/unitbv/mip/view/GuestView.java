package unitbv.mip.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class GuestView extends BorderPane {

    private MenuTableView menuTable;
    private TextField searchField;
    private CheckBox vegCheckBox;
    private ComboBox<String> typeFilter;
    private TextField minPriceField;
    private TextField maxPriceField;
    private Button backButton;

    private Label detailNameLabel;
    private Label detailPriceLabel;
    private TextArea detailDescArea;
    private ImageView productImageView;

    public GuestView() {
        this.setPadding(new Insets(15));

        VBox topBox = new VBox(10);
        Text title = new Text("Bine ați venit! - Mod Consultare Meniu");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        topBox.getChildren().add(title);
        this.setTop(topBox);

        createLeftPanel();

        menuTable = new MenuTableView();
        this.setCenter(menuTable);

        createRightPanel();
    }

    private void createLeftPanel() {
        VBox leftBox = new VBox(10);
        leftBox.setPadding(new Insets(10));
        leftBox.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ccc;");
        leftBox.setPrefWidth(200);

        Label filterLabel = new Label("Filtrează Meniul:");
        filterLabel.setStyle("-fx-font-weight: bold");

        searchField = new TextField(); searchField.setPromptText("Caută produs...");
        vegCheckBox = new CheckBox("Doar Vegetariene");
        typeFilter = new ComboBox<>();
        typeFilter.getItems().addAll("Toate", "Mâncare", "Băutură");
        typeFilter.getSelectionModel().selectFirst();
        Label priceLabel = new Label("Interval Preț (RON):");

        minPriceField = new TextField();
        minPriceField.setPromptText("Min");
        maxPriceField = new TextField();
        maxPriceField.setPromptText("Max");
        HBox priceBox = new HBox(5, minPriceField, maxPriceField);

        backButton = new Button("<- Ieșire (Logout)");

        leftBox.getChildren().addAll(filterLabel, new Separator(),
                new Label("Căutare:"), searchField,
                new Label("Preferințe:"), vegCheckBox,
                new Label("Tip:"), typeFilter,
                priceLabel, priceBox,
                new Separator(), backButton);
        this.setLeft(leftBox);
    }

    private void createRightPanel() {
        VBox rightBox = new VBox(15);
        rightBox.setPadding(new Insets(15));
        rightBox.setPrefWidth(250);
        rightBox.setAlignment(Pos.TOP_CENTER);
        rightBox.setStyle("-fx-border-color: #ccc; -fx-border-width: 0 0 0 1;");

        Label title = new Label("Detalii Produs");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        productImageView = new ImageView();
        productImageView.setFitHeight(150);
        productImageView.setFitWidth(150);
        productImageView.setPreserveRatio(true);
        productImageView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);");

        detailNameLabel = new Label("Selectează un produs");
        detailNameLabel.setWrapText(true);
        detailNameLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        detailPriceLabel = new Label("");
        detailPriceLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 16px;");

        detailDescArea = new TextArea();
        detailDescArea.setEditable(false);
        detailDescArea.setWrapText(true);
        detailDescArea.setPrefHeight(100);
        detailDescArea.setPromptText("Ingrediente / Descriere...");

        rightBox.getChildren().addAll(title, productImageView, detailNameLabel, detailPriceLabel, new Label("Descriere:"), detailDescArea);
        this.setRight(rightBox);
    }

    public MenuTableView getMenuTable() { return menuTable; }
    public TextField getSearchField() { return searchField; }
    public CheckBox getVegCheckBox() { return vegCheckBox; }
    public ComboBox<String> getTypeFilter() { return typeFilter; }
    public TextField getMinPriceField() { return minPriceField; }
    public TextField getMaxPriceField() { return maxPriceField; }
    public Button getBackButton() { return backButton; }

    public Label getDetailNameLabel() { return detailNameLabel; }
    public Label getDetailPriceLabel() { return detailPriceLabel; }
    public TextArea getDetailDescArea() { return detailDescArea; }
    public ImageView getProductImageView() { return productImageView; }
}