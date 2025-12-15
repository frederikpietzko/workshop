package iits.workshop.htmx;

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
