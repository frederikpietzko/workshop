package iits.workshop.htmx;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.FragmentsRendering;

import java.util.List;

@Controller
public class RegistrationController {

    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                              BindingResult bindingResult,
                              Model model) {
        if (bindingResult.hasErrors()) {
            return "fragments/form-container :: form-container";
        }

        userService.registerUser(user);
        model.addAttribute("success", true);
        model.addAttribute("errors", List.of());
        return "fragments/form-container :: form-container";
    }

    @GetMapping("/register-error")
    public String showRegistrationErrorForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("errors", List.of());
        return "register-error";
    }

    @PostMapping("/register-error")
    public String registerUserWithError(@Valid @ModelAttribute("user") User user,
                                       BindingResult bindingResult) {
        throw new IllegalStateException("An error occurred during registration.");
    }
}
