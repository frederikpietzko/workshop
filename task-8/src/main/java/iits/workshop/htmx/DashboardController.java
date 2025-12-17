package iits.workshop.htmx;

import lombok.SneakyThrows;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard/users";
    }

    @GetMapping("/dashboard/users")
    public String showUsers(Model model) {
        List<User> users = dashboardService.getAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("activeTab", "users");
        return "dashboard";
    }

    @GetMapping("/users")
    @SneakyThrows
    public String showUsersPage(Model model) {
        Thread.sleep(1000);
        List<User> users = dashboardService.getAllUsers();
        model.addAttribute("users", users);
        return "fragments/tables :: usersTable";
    }

    @GetMapping("/roles")
    @SneakyThrows
    public String showRolesPage(Model model) {
        Thread.sleep(1000);
        List<Role> roles = dashboardService.getAllRoles();
        model.addAttribute("roles", roles);
        return "fragments/tables :: rolesTable";
    }

    @GetMapping("/permissions")
    @SneakyThrows
    public String showPermissionsPage(Model model) {
        Thread.sleep(1000);
        List<Permission> permissions = dashboardService.getAllPermissions();
        model.addAttribute("permissions", permissions);
        return "fragments/tables :: permissionsTable";
    }

    @GetMapping("/dashboard/roles")
    public String showRoles(Model model) {
        List<Role> roles = dashboardService.getAllRoles();
        model.addAttribute("roles", roles);
        model.addAttribute("activeTab", "roles");
        return "dashboard";
    }

    @GetMapping("/dashboard/permissions")
    public String showPermissions(Model model) {
        List<Permission> permissions = dashboardService.getAllPermissions();
        model.addAttribute("permissions", permissions);
        model.addAttribute("activeTab", "permissions");
        return "dashboard";
    }
}
