package iits.workshop.htmx;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<Item> items = itemService.getAllItems();
        model.addAttribute("items", items);
        model.addAttribute("item", new Item());
        return "index";
    }

    @PostMapping("/items")
    public String createItem(@Valid @ModelAttribute("item") Item item,
                            BindingResult bindingResult,
                            Model model,
                            HttpServletRequest request) {
        boolean isHtmxRequest = "true".equals(request.getHeader("HX-Request"));

        if (bindingResult.hasErrors()) {
            if (isHtmxRequest) {
                return "fragments/form :: item-form";
            }
            List<Item> items = itemService.getAllItems();
            model.addAttribute("items", items);
            return "index";
        }

        itemService.createItem(item);

        if (isHtmxRequest) {
            model.addAttribute("item", new Item());
            return "fragments/form :: item-form";
        }

        return "redirect:/";
    }
}
