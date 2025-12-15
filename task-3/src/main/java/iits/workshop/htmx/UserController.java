package iits.workshop.htmx;

import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final Validator validator;

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
            return "fragments/registration-form :: form";
        }

        // Custom validation: check if username already exists
        if (userService.existsByUsername(user.getUsername())) {
            bindingResult.rejectValue("username", "error.user",
                    "Username is already taken");
            return "fragments/registration-form :: form";
        }

        // Custom validation: check if email already exists
        if (userService.existsByEmail(user.getEmail())) {
            bindingResult.rejectValue("email", "error.user",
                    "Email is already registered");
            return "fragments/registration-form :: form";
        }

        // Save the user
        userService.createUser(user);

        model.addAttribute("successMessage",
                "Registration successful! Welcome, " + user.getUsername() + "!");
        model.addAttribute("user", new User());

        return "fragments/registration-form :: form";
    }

    @PostMapping("/validate-field")
    public String validateField(@RequestParam String field,
                               @ModelAttribute("user") User user,
                               BindingResult bindingResult,
                               Model model) {
        // Get the field value
        String value = switch (field) {
            case "username" -> user.getUsername();
            case "email" -> user.getEmail();
            case "password" -> user.getPassword();
            default -> "";
        };

        // Validate using Jakarta Validator
        var violations = validator.validateValue(User.class, field, value);

        for (var violation : violations) {
            bindingResult.rejectValue(field, "error.user", violation.getMessage());
        }

        // Check for custom validation (uniqueness)
        if (!bindingResult.hasFieldErrors(field)) {
            if ("username".equals(field) && value != null && !value.isEmpty() && userService.existsByUsername(value)) {
                bindingResult.rejectValue(field, "error.user", "Username is already taken");
            } else if ("email".equals(field) && value != null && !value.isEmpty() && userService.existsByEmail(value)) {
                bindingResult.rejectValue(field, "error.user", "Email is already registered");
            }
        }

        // Set field metadata
        String label = switch (field) {
            case "username" -> "Username";
            case "email" -> "Email";
            case "password" -> "Password";
            default -> field;
        };

        String type = switch (field) {
            case "password" -> "password";
            case "email" -> "email";
            default -> "text";
        };

        String placeholder = switch (field) {
            case "username" -> "Enter your username";
            case "email" -> "Enter your email address";
            case "password" -> "Enter your password";
            default -> "";
        };

        String helperText = switch (field) {
            case "username" -> "Username must be 3-50 characters, letters, numbers and underscores only";
            case "email" -> "We will never share your email with anyone";
            case "password" -> "Password must be at least 8 characters with uppercase, lowercase, and number";
            default -> "";
        };

        model.addAttribute("fieldName", field);
        model.addAttribute("label", label);
        model.addAttribute("type", type);
        model.addAttribute("placeholder", placeholder);
        model.addAttribute("helperText", helperText);

        return "fragments/input :: fieldGroup";
    }
}
