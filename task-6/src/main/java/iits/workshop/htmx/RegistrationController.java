package iits.workshop.htmx;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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
            return "register";
        }
        
        userService.registerUser(user);
        model.addAttribute("success", true);
        return "register";
    }

    @GetMapping("/register-error")
    public String showRegistrationErrorForm(Model model) {
        model.addAttribute("user", new User());
        return "register-error";
    }

    @PostMapping("/register-error")
    public String registerUserWithError(@Valid @ModelAttribute("user") User user, 
                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "register-error";
        }
        
        // This will throw a 500 error intentionally
        userService.registerUserWithError(user);
        return "register-error";
    }
}
