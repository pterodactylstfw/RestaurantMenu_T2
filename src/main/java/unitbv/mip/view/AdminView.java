package unitbv.mip.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import unitbv.mip.model.Order;
import unitbv.mip.model.OrderViewModel;
import unitbv.mip.model.User;

public class AdminView extends StackPane {

    private TabPane tabPane;
    private ProgressIndicator loadingSpinner;
    private VBox loadingOverlay;

    private TableView<User> staffTable;
    private TextField usernameField;
    private PasswordField passwordField;
    private Button addStaffButton, deleteStaffButton;

    private MenuTableView menuTable;
    private Button addProductButton, deleteProductButton, editProductButton;
    private Button importJsonButton, exportJsonButton;

    private CheckBox happyHourCheck, mealDealCheck, partyPackCheck;
    private Button saveOffersButton;

    private TableView<OrderViewModel> globalHistoryTable;
    private Button refreshHistoryButton;

    private Button logoutButton;

    public AdminView() {

        this.setPadding(new Insets(0));

        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        logoutButton = new Button("Deconectare");
        logoutButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");

        createStaffTab();
        createMenuTab();
        createOffersTab();
        createHistoryTab();

        loadingSpinner = new ProgressIndicator();
        Label loadingLabel = new Label("Se procesează datele...");
        loadingLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        loadingOverlay = new VBox(10, loadingSpinner, loadingLabel);
        loadingOverlay.setAlignment(Pos.CENTER);
        loadingOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        loadingOverlay.setVisible(false);

        this.getChildren().addAll(tabPane, loadingOverlay);

    }

    public void setLoading(boolean isLoading) {
        loadingOverlay.setVisible(isLoading);
        tabPane.setDisable(isLoading);
    }

    private void createStaffTab() {
        Tab tab = new Tab("Gestiune Angajați");
        VBox content = new VBox(10);
        content.setPadding(new Insets(15));

        HBox form = new HBox(10);
        usernameField = new TextField(); usernameField.setPromptText("Username");
        passwordField = new PasswordField(); passwordField.setPromptText("Parola");
        addStaffButton = new Button("Angajează Ospătar");
        form.getChildren().addAll(new Label("Nou:"), usernameField, passwordField, addStaffButton);

        staffTable = new TableView<>();
        TableColumn<User, String> userCol = new TableColumn<>("Utilizator");
        userCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        staffTable.getColumns().add(userCol);
        staffTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        deleteStaffButton = new Button("Concediază (Șterge)");
        deleteStaffButton.setStyle("-fx-background-color: #ffcccc; -fx-text-fill: red;");

        content.getChildren().addAll(form, new Separator(), staffTable, deleteStaffButton);
        tab.setContent(content);
        tabPane.getTabs().add(tab);
    }

    private void createMenuTab() {
        Tab tab = new Tab("Gestiune Meniu");
        BorderPane content = new BorderPane();
        content.setPadding(new Insets(15));

        menuTable = new MenuTableView();
        content.setCenter(menuTable);

        HBox controls = new HBox(10);
        controls.setPadding(new Insets(10));
        addProductButton = new Button("Adaugă Produs Nou");
        deleteProductButton = new Button("Șterge Produs Selectat");
        editProductButton = new Button("Editează Produs");
        importJsonButton = new Button("Import JSON");
        exportJsonButton = new Button("Export JSON");

        controls.getChildren().addAll(addProductButton, new Separator(), editProductButton, deleteProductButton, new Separator(), importJsonButton, exportJsonButton);
        content.setBottom(controls);

        tab.setContent(content);
        tabPane.getTabs().add(tab);
    }

    private void createOffersTab() {
        Tab tab = new Tab("Configurare Oferte");
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));

        happyHourCheck = new CheckBox("Activează Happy Hour (-20% la alcool)");
        mealDealCheck = new CheckBox("Activează Meal Deal (Pizza + Desert redus)");
        partyPackCheck = new CheckBox("Activează Party Pack (4 Pizza = 1 Gratis)");

        saveOffersButton = new Button("Salvează Configurația Ofertelor");

        content.getChildren().addAll(new Label("Selectează ofertele active azi:"),
                happyHourCheck, mealDealCheck, partyPackCheck,
                new Separator(), saveOffersButton);
        tab.setContent(content);
        tabPane.getTabs().add(tab);
    }

    private void createHistoryTab() {
        Tab tab = new Tab("Istoric Global");
        VBox content = new VBox(10);
        content.setPadding(new Insets(15));

        HBox topBar = new HBox(10);
        refreshHistoryButton = new Button("Reîncarcă Istoric");
        topBar.getChildren().addAll(logoutButton, new Separator(), refreshHistoryButton);

        // modif tabel istoric global
        globalHistoryTable = new TableView<>();

        TableColumn<unitbv.mip.model.OrderViewModel, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cell -> cell.getValue().idProperty());

        TableColumn<unitbv.mip.model.OrderViewModel, String> waiterCol = new TableColumn<>("Ospătar");
        waiterCol.setCellValueFactory(cell -> cell.getValue().waiterNameProperty());

        TableColumn<unitbv.mip.model.OrderViewModel, String> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(cell -> cell.getValue().totalAmountProperty());

        TableColumn<unitbv.mip.model.OrderViewModel, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cell -> cell.getValue().statusProperty());

        globalHistoryTable.getColumns().addAll(idCol, waiterCol, totalCol, statusCol);
        globalHistoryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        content.getChildren().addAll(topBar, globalHistoryTable);
        tab.setContent(content);
        tabPane.getTabs().add(tab);
    }

    public Button getRefreshHistoryButton() { return refreshHistoryButton; }
    public TableView<User> getStaffTable() { return staffTable; }
    public TextField getUsernameField() { return usernameField; }
    public PasswordField getPasswordField() { return passwordField; }
    public Button getAddStaffButton() { return addStaffButton; }
    public Button getDeleteStaffButton() { return deleteStaffButton; }

    public MenuTableView getMenuTable() { return menuTable; }
    public Button getAddProductButton() { return addProductButton; }
    public Button getDeleteProductButton() { return deleteProductButton; }
    public Button getEditProductButton() { return editProductButton; }
    public Button getImportJsonButton() { return importJsonButton; }
    public Button getExportJsonButton() { return exportJsonButton; }

    public CheckBox getHappyHourCheck() { return happyHourCheck; }
    public CheckBox getMealDealCheck() { return mealDealCheck; }
    public CheckBox getPartyPackCheck() { return partyPackCheck; }
    public Button getSaveOffersButton() { return saveOffersButton; }

    public TableView<OrderViewModel> getGlobalHistoryTable() { return globalHistoryTable; }
    public Button getLogoutButton() { return logoutButton; }
}