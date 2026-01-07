package unitbv.mip.service;

import unitbv.mip.model.Role;
import unitbv.mip.model.User;
import unitbv.mip.repository.UserRepository;

import java.util.Optional;

public class AuthService {
    private final UserRepository userRepository;

    private static User currentUser;

    public AuthService() {
        this.userRepository = new UserRepository();
    }

    public User login(String username, String password) throws Exception {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if(userOpt.isPresent()) {
            User user = userOpt.get();

            if(user.getPassword().equals(password)){
                currentUser = user;
                return user;
            }
        }
        throw new Exception("Utilizator sau parolă incorectă");
    }

    public void logout() {
        currentUser = null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public void ensureAdminExists() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("admin");
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            System.out.println("Admin user created: admin / admin");
        }

        if (userRepository.findByUsername("staff").isEmpty()) {
            User staff = new User();
            staff.setUsername("staff");
            staff.setPassword("1234");
            staff.setRole(Role.STAFF);
            userRepository.save(staff);
            System.out.println("Staff user created: staff / 1234");
        }
    }
}
