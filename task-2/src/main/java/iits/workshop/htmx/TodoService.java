package iits.workshop.htmx;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoService {
    private final TodoRepository todoRepository;

    public List<TodoItem> findAll() {
        return todoRepository.findAll();
    }

    public TodoItem findById(Long id) {
        return todoRepository.findById(id).orElse(null);
    }

    public TodoItem updateText(String text, Long id) {
        final var todoItem = todoRepository.findById(id).orElseThrow();
        todoItem.setText(text);
        return todoRepository.save(todoItem);
    }

    public TodoItem save(String text) {
        TodoItem todoItem = new TodoItem(text);
        return todoRepository.save(todoItem);
    }

    public void toggleDone(Long id) {
        todoRepository.findById(id).ifPresent(todo -> {
            todo.setDone(!todo.isDone());
            todoRepository.save(todo);
        });
    }

    public void delete(Long id) {
        todoRepository.deleteById(id);
    }
}
