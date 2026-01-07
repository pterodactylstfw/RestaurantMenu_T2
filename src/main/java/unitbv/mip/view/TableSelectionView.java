package unitbv.mip.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

public class TableSelectionView extends VBox{
    private final List<Button> tableButtons = new ArrayList<>();
    private Button historyButton;

    public TableSelectionView() {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(20);
        this.setPadding(new Insets(30));

        Label title = new Label("Selectează Masa");
        title.setFont(new Font("Arial", 24));

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(15);

        int tableCount = 1;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                Button btn = createTableButton(tableCount++);
                grid.add(btn, col, row);
                tableButtons.add(btn);
            }
        }

        Button logoutBtn = new Button("Ieșire (Logout)");
        logoutBtn.setId("logout");
        tableButtons.add(logoutBtn);

        historyButton = new Button("Istoric Comenzi");

        this.getChildren().addAll(title, grid, historyButton, logoutBtn);
    }

    private Button createTableButton(int number) {
        Button btn = new Button("Masa " + number);
        btn.setPrefSize(100, 80);
        btn.setStyle("-fx-font-size: 16px; -fx-base: #4CAF50;");
        btn.setUserData(number);
        return btn;
    }

    public List<Button> getTableButtons() {
        return tableButtons;
    }
    public Button getHistoryButton() {
        return historyButton;
    }
}
