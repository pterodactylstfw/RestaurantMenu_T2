package unitbv.mip.view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import unitbv.mip.model.OrderItem;
import unitbv.mip.view.MenuTableView;

public class StaffView extends BorderPane {
    private MenuTableView menuTable;
    private TableView<OrderItem> cartTable;
    private Label tableLabel;
    private Label totalLabel;
    private Button addButton;
    private Button removeButton;
    private Button placeOrderButton;
    private Button backButton;
    private Spinner<Integer> quantitySpinner;
    private Label discountDetailsLabel;

    public StaffView() {
        this.setPadding(new Insets(15));

        HBox topBox = new HBox(20);
        tableLabel = new Label("Comandă pentru Masa: -");
        tableLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        backButton = new Button("<- Înapoi la Mese");
        topBox.getChildren().addAll(backButton, tableLabel);
        this.setTop(topBox);

        VBox centerBox = new VBox(10);
        centerBox.setPadding(new Insets(10, 10, 0, 0));
        menuTable = new MenuTableView(); // Reutilizăm componenta

        HBox addBox = new HBox(10);
        addButton = new Button("Adaugă în Coș >>");
        quantitySpinner = new Spinner<>(1, 10, 1); // Min 1, Max 10, Start 1
        quantitySpinner.setPrefWidth(70);
        addBox.getChildren().addAll(new Label("Cantitate:"), quantitySpinner, addButton);

        centerBox.getChildren().addAll(new Label("Meniu Restaurant"), menuTable, addBox);
        this.setCenter(centerBox);

        VBox rightBox = new VBox(10);
        rightBox.setPadding(new Insets(10, 0, 0, 10));
        rightBox.setPrefWidth(350);
        rightBox.setStyle("-fx-border-color: #ccc; -fx-border-width: 0 0 0 1;");

        Label cartTitle = new Label("Coș Curent");
        cartTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        cartTable = new TableView<>();

        TableColumn<OrderItem, String> prodCol = new TableColumn<>("Produs");
        prodCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getProduct().getName()));

        TableColumn<OrderItem, Integer> qtyCol = new TableColumn<>("Cant.");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<OrderItem, Double> subtotalCol = new TableColumn<>("Total");
        subtotalCol.setCellValueFactory(new PropertyValueFactory<>("subtotalNet")); // Atenție: trebuie getter în OrderItem

        cartTable.getColumns().addAll(prodCol, qtyCol, subtotalCol);
        cartTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        totalLabel = new Label("TOTAL: 0.00 RON");
        totalLabel.setFont(Font.font("System", FontWeight.BOLD, 16));

        discountDetailsLabel = new Label("");
        discountDetailsLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        removeButton = new Button("Șterge Linie");
        placeOrderButton = new Button("Finalizează Comanda");
        placeOrderButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
        placeOrderButton.setMaxWidth(Double.MAX_VALUE);

        rightBox.getChildren().addAll(cartTitle, cartTable, removeButton, new Separator(),
                discountDetailsLabel, totalLabel, placeOrderButton);
        this.setRight(rightBox);
    }

    public MenuTableView getMenuTable() { return menuTable; }
    public TableView<OrderItem> getCartTable() { return cartTable; }
    public Label getTableLabel() { return tableLabel; }
    public Label getTotalLabel() { return totalLabel; }
    public Button getAddButton() { return addButton; }
    public Button getRemoveButton() { return removeButton; }
    public Button getPlaceOrderButton() { return placeOrderButton; }
    public Button getBackButton() { return backButton; }
    public int getQuantity() { return quantitySpinner.getValue(); }
    public Label getDiscountDetailsLabel() { return discountDetailsLabel; }
}
