package iits.workshop.htmx;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.view.FragmentsRendering;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

@Controller
public class ItemController {

    private final ItemService itemService;
    private final SpringTemplateEngine templateEngine;
    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public ItemController(ItemService itemService, SpringTemplateEngine templateEngine) {
        this.itemService = itemService;
        this.templateEngine = templateEngine;
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

        Item createdItem = itemService.createItem(item);
        notifyClients(createdItem);

        if (isHtmxRequest) {
            model.addAttribute("item", new Item());
            return "fragments/form :: item-form";
        }

        return "redirect:/";
    }

    @GetMapping("/items/stream")
    public SseEmitter streamItems() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));

        return emitter;
    }

    private void notifyClients(Item item) {
        Context context = new Context();
        context.setVariable("item", item);
        final var html = templateEngine.process("fragments/row-fragment", Set.of("row"), context);

        List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();
        emitters.forEach(emitter -> {
            try {
                emitter.send(html);
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        });
        emitters.removeAll(deadEmitters);
    }
}
