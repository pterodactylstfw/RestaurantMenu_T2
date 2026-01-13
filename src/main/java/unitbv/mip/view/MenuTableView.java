package unitbv.mip.view;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import unitbv.mip.model.ProductViewModel;

public class MenuTableView extends TableView<ProductViewModel> {

    public MenuTableView() {
        TableColumn<ProductViewModel, String> nameColumn = new TableColumn<>("Nume Produs");
        nameColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());
        nameColumn.setMinWidth(150);

        TableColumn<ProductViewModel, Double> priceColumn = new TableColumn<>("Pret");
        priceColumn.setCellValueFactory(cell -> cell.getValue().priceProperty().asObject());
        priceColumn.setMinWidth(80);

        TableColumn<ProductViewModel, String> categoryColumn = new TableColumn<>("Categorie");
        categoryColumn.setCellValueFactory(cell -> cell.getValue().categoryProperty());

        TableColumn<ProductViewModel, String> detailsColumn = new TableColumn<>("Detalii");
        detailsColumn.setCellValueFactory(cell -> cell.getValue().detailsProperty());

        this.getColumns().addAll(nameColumn, priceColumn, categoryColumn, detailsColumn);
        this.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
}