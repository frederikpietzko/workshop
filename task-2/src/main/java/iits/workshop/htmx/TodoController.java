package iits.workshop.htmx;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.FragmentsRendering;

@Controller
@RequiredArgsConstructor
public class TodoController {
    private final TodoService todoService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("todos", todoService.findAll());
        return "index";
    }

    @GetMapping("/edit/{id}")
    public String edit(Model model, @PathVariable Long id) {
        model.addAttribute("todo", todoService.findById(id));
        return "fragments/edit-todo-item";
    }

    @PutMapping("/todos/{id}")
    public String updateTodo(@PathVariable Long id, @RequestParam String text, Model model) {
        final var todoItem  = todoService.updateText(text, id);
        model.addAttribute("todo", todoItem);
        return "fragments/todo-item";
    }


    @PostMapping("/todos")
    public View createTodo(@RequestParam String text, Model model) {
        if (text != null && !text.trim().isEmpty()) {
            todoService.save(text);
        }
        model.addAttribute("todos", todoService.findAll());

        return FragmentsRendering
                .fragment("fragments/todo-form")
                .fragment("fragments/todo-list")
                .build();
    }

    @PutMapping("/todos/{id}/toggle")
    public String toggleTodo(@PathVariable Long id, Model model) {
        todoService.toggleDone(id);
        model.addAttribute("todo", todoService.findById(id));
        return "fragments/todo-item";
    }

    @DeleteMapping("/todos/{id}/delete")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        todoService.delete(id);
        return ResponseEntity.ok().build();
    }
}
