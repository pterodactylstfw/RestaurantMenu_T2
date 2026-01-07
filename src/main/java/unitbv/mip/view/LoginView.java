package unitbv.mip.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class LoginView extends GridPane {

    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;
    private Button guestButton;
    private Text messageText;

    public LoginView() {
        this.setAlignment(Pos.CENTER);
        this.setHgap(10);
        this.setVgap(10);
        this.setPadding(new Insets(25, 25, 25, 25));

        Text sceneTitle = new Text("Bine ați venit la Restaurant 'La Andrei'! ");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        this.add(sceneTitle, 0, 0, 2, 1);

        Label usernameLabel = new Label("Nume utilizator:");
        this.add(usernameLabel, 0, 1);

        usernameField = new TextField();
        this.add(usernameField, 1, 1);

        Label passwordLabel = new Label("Parolă:");
        this.add(passwordLabel, 0, 2);

        passwordField = new PasswordField();
        this.add(passwordField, 1, 2);

        loginButton = new Button("Autentificare");
        this.add(loginButton, 1, 3);

        Label guestLabel = new Label("Sau accesați ca oaspete:");
        this.add(guestLabel, 0, 4);

        guestButton = new Button("Oaspete (Guest)");
        this.add(guestButton, 1, 4);

        messageText = new Text();
        this.add(messageText, 1, 6);

    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return passwordField.getText();
    }

    public Button getLoginButton() {
        return loginButton;
    }

    public Button getGuestButton() {
        return guestButton;
    }

    public void setMessage(String message) {
        messageText.setText(message);
    }

    public void clearFields() {
        usernameField.clear();
        passwordField.clear();
        messageText.setText("");
    }
}
