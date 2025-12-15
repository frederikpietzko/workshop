package iits.workshop.htmx;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public String index() {
        return "redirect:/register";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        // Check for validation errors
        if (bindingResult.hasErrors()) {
            return "register";
        }

        // Custom validation: check if username already exists
        if (userService.existsByUsername(user.getUsername())) {
            bindingResult.rejectValue("username", "error.user",
                    "Username is already taken");
            return "register";
        }

        // Custom validation: check if email already exists
        if (userService.existsByEmail(user.getEmail())) {
            bindingResult.rejectValue("email", "error.user",
                    "Email is already registered");
            return "register";
        }

        // Save the user
        userService.createUser(user);

        redirectAttributes.addFlashAttribute("successMessage",
                "Registration successful! Welcome, " + user.getUsername() + "!");

        return "redirect:/register";
    }
}
