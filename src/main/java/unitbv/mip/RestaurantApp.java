package unitbv.mip;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import unitbv.mip.config.ConfigManager; // Import nou
import unitbv.mip.config.PersistenceManager;
import unitbv.mip.controller.LoginController;
import unitbv.mip.service.AuthService;
import unitbv.mip.utils.SceneManager;
import unitbv.mip.view.LoginView;

public class RestaurantApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        ConfigManager.loadConfig();

        SceneManager.getInstance().setStage(primaryStage);

        new AuthService().ensureAdminExists();

        primaryStage.setWidth(1280);
        primaryStage.setHeight(720);
        primaryStage.setResizable(true);

        LoginView loginView = new LoginView();
        new LoginController(loginView);

        SceneManager.getInstance().changeScene(loginView, "Autentificare - La Andrei");

        primaryStage.setOnCloseRequest(event -> {
            System.out.println("S-a apăsat X. Inițiere oprire...");
            Platform.exit();
        });
    }

    @Override
    public void stop() throws Exception {
        PersistenceManager.getInstance().close();
        super.stop();
        System.out.println("Aplicația se oprește. Curățare resurse...");
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}