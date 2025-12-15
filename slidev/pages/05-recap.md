# Kotlinx.html für HTMX

<v-clicks>

Kotlin bietet mit **kotlinx.html** eine typsichere DSL für HTML-Generierung:

```kotlin
@GetMapping("/users")
fun getUsers(): String {
    return buildString {
        appendHTML().div {
            id = "user-list"
            h2 { +"Users" }
            ul {
                users.forEach { user ->
                    li {
                        attributes["hx-get"] = "/user/${user.id}"
                        attributes["hx-target"] = "#user-detail"
                        attributes["hx-swap"] = "innerHTML"
                        +user.name
                    }
                }
            }
        }
    }
}
```

</v-clicks>

---

# Kotlinx.html - Extension Functions

<v-clicks>

**Eigene Extension Functions für HTMX:**

```kotlin
fun FlowContent.htmxGet(url: String, target: String, block: DIV.() -> Unit) {
    div {
        attributes["hx-get"] = url
        attributes["hx-target"] = target
        attributes["hx-swap"] = "innerHTML"
        block()
    }
}

// Verwendung:
fun renderSearch() = buildString {
    appendHTML().htmxGet("/search", "#results") {
        input {
            type = InputType.search
            name = "query"
            attributes["hx-trigger"] = "keyup changed delay:300ms"
        }
    }
}
```

</v-clicks>

---

# Kotlinx.html - Komplexes Beispiel

<v-clicks>

```kotlin
fun HTML.todoList(todos: List<Todo>) {
    div {
        id = "todo-list"
        attributes["hx-target"] = "this"
        attributes["hx-swap"] = "outerHTML"
        
        form {
            attributes["hx-post"] = "/todos"
            attributes["hx-swap"] = "beforeend"
            attributes["hx-target"] = "#todo-items"
            
            input { 
                name = "title"
                placeholder = "New todo..."
            }
            button { +"Add" }
        }
        
        ul {
            id = "todo-items"
            todos.forEach { todo ->
                li {
                    span { +todo.title }
                    button {
                        attributes["hx-delete"] = "/todos/${todo.id}"
                        attributes["hx-target"] = "closest li"
                        +"Delete"
                    }
                }
            }
        }
    }
}
```

</v-clicks>

---

# Tag 1 - Recap

**Was wir heute gelernt haben:**

<v-clicks>

**1. HTMX Grundlagen**
- Hypermedia-Driven Approach statt JSON APIs
- Core Attributes: `hx-get`, `hx-post`, `hx-target`, `hx-swap`
- Progressive Enhancement

**2. Request & Response Handling**
- Request Headers (`HX-Request`, `HX-Trigger`, etc.)
- Response Headers (`HX-Trigger`, `HX-Redirect`, etc.)
- Custom Parameter mit `hx-vals`, `hx-params`, `hx-include`

**3. Trigger & Events**
- Event Modifiers: `changed`, `delay`, `throttle`
- Event Lifecycle: `beforeRequest`, `afterSwap`, etc.
- Custom Events vom Server

</v-clicks>

---

# Tag 1 - Recap (2)

<v-clicks>

**4. Swap Strategien & Targeting**
- Verschiedene Swap-Modi (`innerHTML`, `outerHTML`, `beforeend`, etc.)
- Relative Selektoren (`closest`, `next`, `previous`)
- Out-of-Band Swaps (OOB)

**5. Advanced Patterns**
- Loading States & Progress Indicators
- Polling & Debouncing
- Error Handling mit Events
- Event-Driven Component Communication

**6. Server-Side Rendering**
- Thymeleaf Integration
- Session State Management
- Multi-Step Wizards

</v-clicks>

---

# Was kommt an Tag 2?

<v-clicks>

**Geplante Themen:**

- **Error Handling** - HTTP Error Codes managen
- **WebSockets & Server-Sent Events** - Real-time Updates
- **Architecture Patterns** - Größere Anwendungen strukturieren
- **lazy Loading** - Inhalte on-demand laden
- **Infinite Scrolling** - Dynamisches Nachladen von Inhalten
- **Data Tables** - Sortierung, Filterung, Pagination
- **Open Workshop** - Eure Anwendungsfälle & Fragen

</v-clicks>

---
layout: center
class: text-center
---

# Fragen & Wünsche für Tag 2? 🤔

<div class="mt-8">

**Was möchtet ihr vertiefen?**

**Welche Use Cases interessieren euch?**

**Gibt es spezifische Probleme zu lösen?**

</div>

<div class="mt-12 text-sm opacity-75">

Vielen Dank für heute! 🎉

</div>
