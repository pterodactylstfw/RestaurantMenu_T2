package unitbv.mip.utils;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {
    private static SceneManager instance;
    private Stage stage;

    private SceneManager() {}

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void changeScene(Parent rootNode, String title) {
        if (stage.getScene() == null) {
            stage.setScene(new Scene(rootNode, 800, 600));
        } else {
            stage.getScene().setRoot(rootNode);
        }
        stage.setTitle(title);
        stage.show();
    }
}