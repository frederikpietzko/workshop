package iits.workshop.htmx;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public DataInitializer(PermissionRepository permissionRepository,
                          RoleRepository roleRepository,
                          UserRepository userRepository) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        // Create permissions
        Permission readPermission = new Permission("READ", "Read access to resources");
        Permission writePermission = new Permission("WRITE", "Write access to resources");
        Permission deletePermission = new Permission("DELETE", "Delete access to resources");
        Permission adminPermission = new Permission("ADMIN", "Full administrative access");
        Permission createPermission = new Permission("CREATE", "Create new resources");
        Permission updatePermission = new Permission("UPDATE", "Update existing resources");
        Permission executePermission = new Permission("EXECUTE", "Execute operations");
        Permission viewReportsPermission = new Permission("VIEW_REPORTS", "View system reports");

        permissionRepository.saveAll(List.of(
            readPermission, writePermission, deletePermission, adminPermission,
            createPermission, updatePermission, executePermission, viewReportsPermission
        ));

        // Create roles
        Role adminRole = new Role("ADMIN", "System Administrator with full access");
        adminRole.addPermission(readPermission);
        adminRole.addPermission(writePermission);
        adminRole.addPermission(deletePermission);
        adminRole.addPermission(adminPermission);
        adminRole.addPermission(createPermission);
        adminRole.addPermission(updatePermission);
        adminRole.addPermission(executePermission);
        adminRole.addPermission(viewReportsPermission);

        Role managerRole = new Role("MANAGER", "Manager with elevated privileges");
        managerRole.addPermission(readPermission);
        managerRole.addPermission(writePermission);
        managerRole.addPermission(createPermission);
        managerRole.addPermission(updatePermission);
        managerRole.addPermission(viewReportsPermission);

        Role userRole = new Role("USER", "Regular user with basic access");
        userRole.addPermission(readPermission);
        userRole.addPermission(writePermission);

        Role viewerRole = new Role("VIEWER", "Read-only access");
        viewerRole.addPermission(readPermission);

        roleRepository.saveAll(List.of(adminRole, managerRole, userRole, viewerRole));

        // Create many users for a long table
        List<User> users = new ArrayList<>();
        String[] firstNames = {"John", "Jane", "Michael", "Sarah", "David", "Emily", "Robert", "Lisa", "James", "Mary",
                               "William", "Patricia", "Richard", "Jennifer", "Joseph", "Linda", "Thomas", "Barbara", "Charles", "Elizabeth"};
        String[] lastNames = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodriguez", "Martinez",
                             "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson", "Thomas", "Taylor", "Moore", "Jackson", "Martin"};
        Role[] roles = {adminRole, managerRole, userRole, viewerRole};

        int userCount = 0;
        for (String firstName : firstNames) {
            for (String lastName : lastNames) {
                userCount++;
                String username = firstName.toLowerCase() + "." + lastName.toLowerCase() + userCount;
                String email = username + "@example.com";
                Role role = roles[userCount % roles.length];
                users.add(new User(username, email, role));
            }
        }

        // Add some more users to make it even longer
        for (int i = 1; i <= 50; i++) {
            String username = "testuser" + i;
            String email = "testuser" + i + "@example.com";
            Role role = roles[i % roles.length];
            users.add(new User(username, email, role));
        }

        userRepository.saveAll(users);
    }
}
