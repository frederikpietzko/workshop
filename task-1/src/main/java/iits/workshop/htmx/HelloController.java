package iits.workshop.htmx;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/load-content")
    public String loadContent() {
        return "fragments/content :: content";
    }
}
