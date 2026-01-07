package unitbv.mip.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import unitbv.mip.model.Order;

public class StaffHistoryView extends BorderPane {

    private TableView<Order> historyTable;
    private Button backButton;

    public StaffHistoryView() {
        this.setPadding(new Insets(20));

        Label title = new Label("Istoricul Meu de Comenzi");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        VBox topBox = new VBox(10);
        backButton = new Button("<- Înapoi la Mese");
        topBox.getChildren().addAll(backButton, title);
        this.setTop(topBox);

        historyTable = new TableView<>();

        TableColumn<Order, Long> idCol = new TableColumn<>("ID Comandă");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Order, String> tableCol = new TableColumn<>("Masa");
        tableCol.setCellValueFactory(cell ->
                new SimpleStringProperty("Masa " + cell.getValue().getTable().getTableNumber()));

        TableColumn<Order, String> totalCol = new TableColumn<>("Total (RON)");
        totalCol.setCellValueFactory(cell ->
                new SimpleStringProperty(String.format("%.2f", cell.getValue().getTotalAmount())));

        historyTable.getColumns().addAll(idCol, tableCol, totalCol);
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        this.setCenter(historyTable);
    }

    public TableView<Order> getHistoryTable() { return historyTable; }
    public Button getBackButton() { return backButton; }
}