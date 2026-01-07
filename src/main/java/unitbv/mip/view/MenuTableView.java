package unitbv.mip.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import unitbv.mip.model.Drink;
import unitbv.mip.model.Food;
import unitbv.mip.model.Product;

public class MenuTableView extends TableView<Product> {

    public MenuTableView() {
        TableColumn<Product, String> nameColumn = new TableColumn<>("Nume Produs");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setMinWidth(150);

        TableColumn<Product, Double> priceColumn = new TableColumn<>("Pret");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceColumn.setMinWidth(80);

        TableColumn<Product, String> categoryColumn = new TableColumn<>("Categorie");
        categoryColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCategory().toString()));

        TableColumn<Product, String> detailsColumn = new TableColumn<>("Detalii");
        detailsColumn.setCellValueFactory(cellData -> {
            Product p = cellData.getValue();
            if (p instanceof Food) {
                return new SimpleStringProperty(((Food) p).getWeight() + "g");
            } else if (p instanceof Drink) {
                return new SimpleStringProperty(((Drink) p).getVolume() + "l");
            }
            return new SimpleStringProperty("-");
        });

        this.getColumns().addAll(nameColumn, priceColumn, categoryColumn, detailsColumn);

        this.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
}
