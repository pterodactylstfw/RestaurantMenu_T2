package unitbv.mip.view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import unitbv.mip.model.Order;
import unitbv.mip.model.User;

public class AdminView extends TabPane {

    private TableView<User> staffTable;
    private TextField usernameField;
    private PasswordField passwordField;
    private Button addStaffButton, deleteStaffButton, editProductButton;

    private MenuTableView menuTable;
    private Button deleteProductButton;
    private Button importJsonButton, exportJsonButton;

    private CheckBox happyHourCheck, mealDealCheck, partyPackCheck;
    private Button saveOffersButton;

    private TableView<Order> globalHistoryTable;

    private Button logoutButton;

    public AdminView() {
        this.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

        logoutButton = new Button("Deconectare");

        createStaffTab();
        createMenuTab();
        createOffersTab();
        createHistoryTab();


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
        this.getTabs().add(tab);
    }

    private void createMenuTab() {
        Tab tab = new Tab("Gestiune Meniu");
        BorderPane content = new BorderPane();
        content.setPadding(new Insets(15));

        menuTable = new MenuTableView();
        content.setCenter(menuTable);

        HBox controls = new HBox(10);
        controls.setPadding(new Insets(10));
        deleteProductButton = new Button("Șterge Produs Selectat");
        editProductButton = new Button("Editează Produs");
        importJsonButton = new Button("Import JSON");
        exportJsonButton = new Button("Export JSON");

        controls.getChildren().addAll(editProductButton, deleteProductButton, new Separator(), importJsonButton, exportJsonButton);
        content.setBottom(controls);

        tab.setContent(content);
        this.getTabs().add(tab);
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
        this.getTabs().add(tab);
    }

    private void createHistoryTab() {
        Tab tab = new Tab("Istoric Global");
        VBox content = new VBox(10);
        content.setPadding(new Insets(15));

        content.getChildren().add(logoutButton);

        globalHistoryTable = new TableView<>();
        TableColumn<Order, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Order, Double> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

        globalHistoryTable.getColumns().addAll(idCol, totalCol);
        globalHistoryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        content.getChildren().add(globalHistoryTable);
        tab.setContent(content);
        this.getTabs().add(tab);
    }

    // --- Getters ---
    public TableView<User> getStaffTable() { return staffTable; }
    public TextField getUsernameField() { return usernameField; }
    public PasswordField getPasswordField() { return passwordField; }
    public Button getAddStaffButton() { return addStaffButton; }
    public Button getDeleteStaffButton() { return deleteStaffButton; }

    public MenuTableView getMenuTable() { return menuTable; }
    public Button getDeleteProductButton() { return deleteProductButton; }
    public Button getEditProductButton() { return editProductButton; }
    public Button getImportJsonButton() { return importJsonButton; }
    public Button getExportJsonButton() { return exportJsonButton; }

    public CheckBox getHappyHourCheck() { return happyHourCheck; }
    public CheckBox getMealDealCheck() { return mealDealCheck; }
    public CheckBox getPartyPackCheck() { return partyPackCheck; }
    public Button getSaveOffersButton() { return saveOffersButton; }

    public TableView<Order> getGlobalHistoryTable() { return globalHistoryTable; }
    public Button getLogoutButton() { return logoutButton; }
}