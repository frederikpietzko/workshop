# HTMX Workshop - Core Concepts Slides Content

## Slide 1: HTMX Request Lifecycle

### 1. Trigger
User-Aktion löst Request aus

```html
<button hx-get="/data">
  Click
</button>
```

### 2. Request
AJAX Request an Server

**Header:**
- `HX-Request: true`
- `HX-Trigger: button-id`
- `HX-Current-URL: /page`

### 3. Response
Server sendet HTML-Fragment

```html
<div class="result">
  Neue Daten
</div>
```

### 4. Swap
HTMX ersetzt DOM-Element mit Response

---

## Slide 2: Die 7 HTMX Basis-Attribute

**1. HTTP-Methoden**
```html
hx-get="/data"
hx-post="/save"
hx-put="/update"
hx-patch="/modify"
hx-delete="/remove"
```

**2. Target (wo?)**
```html
hx-target="#result"
hx-target="closest div"
hx-target="this"
```

**3. Swap (wie?)**
```html
hx-swap="innerHTML"
hx-swap="outerHTML"
hx-swap="beforeend"
hx-swap="afterbegin"
```

**4. Trigger (wann?)**
```html
hx-trigger="click"
hx-trigger="change"
hx-trigger="load"
hx-trigger="every 2s"
```

---

## Slide 3: Beispiel - Alle 4 Attribute zusammen

```html
<input
  hx-get="/search"
  hx-trigger="keyup changed delay:500ms"
  hx-target="#results"
  hx-swap="innerHTML"
  name="query"
  placeholder="Suche..." />

<div id="results"></div>
```

**Spring MVC Controller:**

```java
@GetMapping("/search")
public String search(@RequestParam String query, Model model) {
    model.addAttribute("results", searchService.search(query));
    return "fragments/search-results"; // Thymeleaf Fragment
}
```

---

## Slide 4: hx-target - Verschiedene Selektoren

```html
<!-- CSS Selector -->
<button hx-get="/data" hx-target="#result">Load</button>

<!-- this: Element selbst ersetzen -->
<div hx-get="/refresh" hx-target="this">Click to refresh</div>

<!-- closest: Nächstes Eltern-Element -->
<div class="card">
  <button hx-delete="/item/1" hx-target="closest .card">
    Delete
  </button>
</div>

<!-- find: Kind-Element suchen -->
<div hx-get="/status" hx-target="find .status">
  <span class="status">Loading...</span>
</div>
```

---

## Slide 5: hx-swap - Swap-Strategien

```html
<!-- innerHTML: Inhalt ersetzen (default) -->
<div id="result" hx-get="/data" hx-swap="innerHTML">
  <!-- Wird ersetzt -->
</div>

<!-- outerHTML: Element selbst ersetzen -->
<div id="result" hx-get="/data" hx-swap="outerHTML">
  <!-- Gesamtes div wird ersetzt -->
</div>

<!-- beforeend: Am Ende einfügen -->
<ul id="list" hx-get="/item" hx-swap="beforeend">
  <li>Item 1</li>
  <!-- Neues Item hier -->
</ul>

<!-- afterbegin: Am Anfang einfügen -->
<ul id="list" hx-get="/item" hx-swap="afterbegin">
  <!-- Neues Item hier -->
  <li>Item 1</li>
</ul>

<!-- delete: Element löschen -->
<div id="notification" hx-get="/dismiss" hx-swap="delete">
  Notification
</div>

<!-- none: Kein Swap, nur Request -->
<button hx-post="/log" hx-swap="none">
  Log Event
</button>
```

---

## Slide 6: hx-trigger - Wann passiert was?

**Standard Events:**
```html
<button hx-get="/data" hx-trigger="click">
  Click
</button>

<input hx-get="/search" hx-trigger="keyup">

<select hx-get="/filter" hx-trigger="change">
```

**Modifiers:**
```html
<input hx-post="/save"
  hx-trigger="keyup changed delay:500ms">

<div hx-get="/poll"
  hx-trigger="every 2s">

<div hx-get="/data"
  hx-trigger="load">

<button hx-post="/save"
  hx-trigger="click once">
```

**Multiple Triggers:**
```html
<div hx-get="/refresh" hx-trigger="click, every 30s">Auto-refresh</div>
```

---

## Slide 7: Spring MVC Request/Response Headers

**HTMX sendet Header:**

```http
HX-Request: true
HX-Trigger: search-button
HX-Current-URL: /products
HX-Target: results
```

**Spring Controller kann prüfen:**

```java
@GetMapping("/data")
public String getData(
    @RequestHeader(value = "HX-Request", required = false) boolean isHtmx
) {
    if (isHtmx) {
        return "fragments/data"; // Nur Fragment
    }
    return "pages/data"; // Komplette Seite
}
```

---

## Slide 8: Thymeleaf Fragment Rendering

**Layout mit Fragmenten:**

```html
<!-- templates/fragments/product-card.html -->
<div th:fragment="card(product)" class="product-card">
  <h3 th:text="${product.name}">Product Name</h3>
  <p th:text="${product.price}">Price</p>
  <button hx-delete="${'/products/' + product.id}"
          hx-target="closest .product-card"
          hx-swap="outerHTML">
    Delete
  </button>
</div>
```

**Controller:**

```java
@GetMapping("/products/{id}")
public String getProduct(@PathVariable Long id, Model model) {
    model.addAttribute("product", productService.findById(id));
    return "fragments/product-card :: card"; // Fragment zurückgeben
}
```

---

## Slide 9: Zusammenfassung Core Konzepte

✅ **4 Kern-Attribute**: `hx-get/post`, `hx-target`, `hx-swap`, `hx-trigger`

✅ **Request Lifecycle**: Trigger → Request → Response → Swap

✅ **Spring MVC Integration**: HX-Request Header, Fragment Rendering

✅ **Thymeleaf Fragmente**: Wiederverwendbare HTML-Komponenten

**Als Nächstes:** Advanced Attributes & Patterns
