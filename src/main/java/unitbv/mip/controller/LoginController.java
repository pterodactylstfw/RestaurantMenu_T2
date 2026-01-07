package unitbv.mip.controller;

import unitbv.mip.model.Role;
import unitbv.mip.model.User;
import unitbv.mip.service.AuthService;
import unitbv.mip.utils.SceneManager;
import unitbv.mip.view.AdminView;
import unitbv.mip.view.GuestView;
import unitbv.mip.view.LoginView;

public class LoginController {
    
    private final LoginView view;
    private final AuthService authService;
    
    public LoginController(LoginView view) {
        this.view = view;
        this.authService = new AuthService();
        
        initialize();
    }
    
    private void initialize() {
        view.getLoginButton().setOnAction(event -> handleLogin());
        view.getGuestButton().setOnAction(event -> handleGuestEntry());
    }

    private void handleLogin() {
        String username = view.getUsername();
        String password = view.getPassword();

        try {
            User loggedUser = authService.login(username, password);

            view.setMessage("Succes! Bine ai venit, " + loggedUser.getUsername() + '!');

            if (loggedUser.getRole() == Role.ADMIN) {
                System.out.println("--> Navigare către ADMIN Dashboard");
                AdminView adminView = new AdminView();
                new AdminController(adminView);
                SceneManager.getInstance().changeScene(adminView, "Panou Administrator");
            } else {
                System.out.println("--> Navigare către STAFF Dashboard");
                new StaffController();
            }
        } catch (Exception e) {
            view.setMessage(e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleGuestEntry() {
        System.out.println("--> Navigare către GUEST Menu");
        GuestView guestView = new GuestView();
        new GuestController(guestView);
        SceneManager.getInstance().changeScene(guestView, "Meniu Client - La Andrei");
    }


}
