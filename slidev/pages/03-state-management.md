# State Management mit HTMX

<v-clicks>

**State Management in Web Apps:**
- **Client-Side State**: UI-Zustand, temporäre Daten (React, Vue)
- **Server-Side State**: Business Logic, Persistierung

**HTMX Ansatz:**
- Server ist die Single Source of Truth
- State in URL, Session oder Database
- Kein komplexes Client-Side State Management notwendig

**Was wir behandeln:**
- URL-basierter State (History API)
- Session-basierter State
- Boosting für Progressive Enhancement
- Redirects

</v-clicks>

---

# URLs & History API

<v-clicks>

**hx-push-url** - Fügt URL zur Browser History hinzu:

```html
<button hx-get="/page/2" hx-push-url="true">
  Next Page
</button>
```

- Browser URL wird zu `/page/2`
- Back-Button funktioniert
- `true`: Verwendet Response URL
- `"/custom"`: Custom URL

**hx-replace-url** - Ersetzt aktuelle URL ohne History:

```html
<form hx-post="/search" hx-replace-url="true">
  <input name="query">
  <button type="submit">Search</button>
</form>
```

- URL ändert sich ohne History-Eintrag
- Gut für Suchen, Filter, Tabs

</v-clicks>

---

# History API - Erweiterte Nutzung

<v-clicks>

**URL von Server steuern:**

```java
@GetMapping("/products")
public String products(@RequestParam(required = false) String category,
                       HttpServletResponse response) {
    if (category != null) {
        response.setHeader("HX-Push-Url", "/products?category=" + category);
    }
    return renderProducts(category);
}
```

**Mit Query Parametern:**

```html
<select hx-get="/products" 
        hx-target="#results"
        hx-push-url="true"
        name="category">
  <option value="all">Alle</option>
  <option value="electronics">Elektronik</option>
</select>
<!-- URL wird zu: /products?category=electronics -->
```

</v-clicks>

---

# Sessions & Server-Side State

<v-clicks>

**Session für User-spezifischen State:**

```java
@PostMapping("/cart/add")
public String addToCart(@RequestParam Long productId, 
                       HttpSession session) {
    Cart cart = (Cart) session.getAttribute("cart");
    if (cart == null) {
        cart = new Cart();
        session.setAttribute("cart", cart);
    }
    cart.addItem(productId);
    
    return renderCartWidget(cart);
}
```

**OOB Swap für Session-Updates:**

```html
<!-- Cart Widget immer aktuell -->
<div id="cart-widget" hx-swap-oob="true">
  Items: 3
</div>
```

</v-clicks>

---
title: Sessions & Server-Side State 2
---

# Sessions & Server-Side State

<v-clicks>

**Vorteile:**
- State überlebt Page Refreshes
- Keine Client-Side Synchronisation
- Security: State auf Server

</v-clicks>

---

# Boosting

<v-clicks>

**hx-boost** - Macht normale Links zu AJAX Requests:

```html
<div hx-boost="true">
  <a href="/page1">Page 1</a>
  <a href="/page2">Page 2</a>
  <a href="/page3">Page 3</a>
</div>
```

**Was passiert:**
- Klick auf Link → AJAX Request statt Page Load
- Response ersetzt `<body>` per Default
- URL wird automatisch gepusht
- Progressive Enhancement: Ohne JS normale Links

</v-clicks>

---
title: Boosting
---

# Boosting

<v-clicks>

**Für ganze App:**
```html
<body hx-boost="true">
  <!-- Alle Links sind jetzt AJAX -->
  <a href="/about">About</a>
  <a href="/contact">Contact</a>
</body>
```

**Best Practice:** Starter-Template für klassische Multi-Page Apps

</v-clicks>

---

# Redirects

<v-clicks>

**Standard HTTP Redirect (302/303):**

```java
@PostMapping("/login")
public String login(@RequestParam String username) {
    // ... authenticate
    return "redirect:/dashboard";
}
```

- HTMX folgt automatisch
- Neue Page wird geladen

</v-clicks>

---

# Redirects mit HTMX

<v-clicks>

**HX-Redirect Header:**

```java
@PostMapping("/logout")
public ResponseEntity<String> logout(HttpSession session) {
    session.invalidate();
    return ResponseEntity.ok()
        .header("HX-Redirect", "/login")
        .body("");
}
```

- Client-seitiger Redirect
- Full Page Reload

</v-clicks>

---

# HX-Location für HTMX-Redirects

<v-clicks>

**HX-Location** - Redirect mit HTMX Request:

```java
@PostMapping("/complete-task")
public ResponseEntity<String> completeTask(@RequestParam Long id) {
    taskService.complete(id);
    
    return ResponseEntity.ok()
        .header("HX-Location", "/tasks")
        .body("");
}
```

- HTMX macht neuen Request zu `/tasks`
- Kein Full Page Reload
- Target bleibt erhalten

</v-clicks>

--- 
title: HX-Location für HTMX-Redirects 2
---

# HX-Location für HTMX-Redirects

<v-clicks>

**Mit Custom Target:**
```java
@PostMapping("/action")
public ResponseEntity<String> action() {
    String location = """
        {
            "path": "/results",
            "target": "#main-content",
            "swap": "innerHTML"
        }
    """;
    
    return ResponseEntity.ok()
        .header("HX-Location", location)
        .body("");
}
```

</v-clicks>

---

# State Management Patterns

<v-clicks>

**Pattern 1: URL-basiert (Stateless)**
```html
<a href="/products?page=2" hx-get="/products?page=2" hx-push-url="true">Next</a>
<select hx-get="/products" hx-push-url="true" name="category">
```

✅ Shareable URLs, Back-Button funktioniert

**Pattern 2: Session-basiert**
```java
session.setAttribute("currentStep", 2);
```

✅ User-spezifisch, überlebt Refreshes

**Pattern 3: Hybrid**
```html
<a href="/wizard/step2" hx-get="/wizard/step2" hx-push-url="true">
```

✅ Beste UX für komplexe Apps

</v-clicks>

---

# Übung 4: Multi-Step Wizard mit Thymeleaf

**Erstelle einen Multi-Step Wizard:**

**Requirements:**
1. Dynamisch nachladen der Models
2. Wizard state in Session speichern
3. Client Side Redirects (zB wenn user in Step 2, aber Step 1 noch nicht ausgefuellt)
4. Boosting der ganzen Seite für das SPA Gefühlt
5. Nicht die gesamte Seite neu laden nur den Inhalte des Wizards

**Bonus:**
- OOB Swap für Progress Indicator
- Step-Navigation überspringen bei direktem URL-Aufruf
- Dynamische Validation mit HTMX
