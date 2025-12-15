# Lazy Loading & Infinite Scroll

---

# Überblick

<v-clicks>

**Warum Lazy Loading?**
- Schnellere Initial Page Load
- Nur laden was wirklich gebraucht wird
- Bessere Performance bei großen Seiten
- Wiederverwendbare Komponenten

**Zwei Konzepte:**
1. **Infinite Scroll** - Automatisches Nachladen beim Scrollen
2. **Lazy Loading** - On-Demand Laden von Seitenteilen

**HTMX macht beides einfach!** 🚀

</v-clicks>

---

# Infinite Scroll - Quick Example

<v-clicks>

**HTML:**

```html
<div id="products">
    <!-- Initial products -->
</div>

<div hx-get="/products?page=2" 
     hx-trigger="revealed"
     hx-swap="afterend">
    <div class="loading">Loading more...</div>
</div>
```

**Trigger `revealed`:**
- Feuert wenn Element in Viewport kommt
- Perfekt für Infinite Scroll
- HTMX lädt automatisch nach

**Server:**

```java
@GetMapping("/products")
public String getProducts(@RequestParam(defaultValue = "1") int page) {
    List<Product> products = productService.getPage(page);
    
    String html = renderProducts(products);
    
    // Nächster Loader
    if (page < totalPages) {
        html += """
            <div hx-get="/products?page=%d" 
                 hx-trigger="revealed"
                 hx-swap="afterend">
                Loading...
            </div>
        """.formatted(page + 1);
    }
    
    return html;
}
```

</v-clicks>

---

# Lazy Loading - Das Wichtigere Konzept

<v-clicks>

**Use Cases:**
- **Tabs** - Content erst beim Klick laden
- **Accordions** - Sections on-demand laden
- **Modals** - Content erst beim Öffnen laden
- **Dashboard Widgets** - Unabhängige Komponenten
- **Complex Forms** - Steps nacheinander laden
- **Heavy Content** - Große Tabellen, Charts, etc.

**Vorteile:**
- Schneller Initial Render
- Modularer Code
- Wiederverwendbare Widgets
- Bessere UX (keine Wartezeit für unsichtbare Inhalte)

</v-clicks>

---

# Lazy Loading: Tabs

<v-clicks>

**HTML:**

```html
<div class="tabs">
    <button hx-get="/dashboard/overview" 
            hx-target="#tab-content"
            class="active">
        Overview
    </button>
    <button hx-get="/dashboard/stats" 
            hx-target="#tab-content">
        Statistics
    </button>
    <button hx-get="/dashboard/settings" 
            hx-target="#tab-content">
        Settings
    </button>
</div>

<div id="tab-content" hx-get="/dashboard/overview" hx-trigger="load">
    <!-- Initial tab content lädt automatisch -->
</div>
```

**Vorteile:**
- Nur aktiver Tab wird geladen
- Jeder Tab ist eigener Endpoint
- Tabs können wiederverwendet werden

</v-clicks>

---

# Lazy Loading: Tabs Server-Side

<v-clicks>

```java
@GetMapping("/dashboard/overview")
public String getOverview() {
    DashboardData data = dashboardService.getOverview();
    return """
        <div class="tab-panel">
            <h3>Overview</h3>
            <div class="stats-grid">
                <div class="stat">
                    <span>Total Users</span>
                    <strong>%d</strong>
                </div>
                <div class="stat">
                    <span>Revenue</span>
                    <strong>€%s</strong>
                </div>
            </div>
        </div>
    """.formatted(data.getTotalUsers(), data.getRevenue());
}

@GetMapping("/dashboard/stats")
public String getStatistics() {
    // Nur laden wenn Tab angeklickt wird!
    List<Stat> stats = statisticsService.getDetailedStats();
    return renderStatsTab(stats);
}

@GetMapping("/dashboard/settings")
public String getSettings() {
    // Auch nur on-demand
    UserSettings settings = settingsService.getCurrentUserSettings();
    return renderSettingsTab(settings);
}
```

</v-clicks>

---

# Lazy Loading: Accordion

<v-clicks>

**HTML:**

```html
<div class="accordion">
    <div class="accordion-item">
        <button class="accordion-header" 
                hx-get="/faq/1" 
                hx-target="#faq-1"
                hx-swap="innerHTML">
            What is HTMX?
        </button>
        <div id="faq-1" class="accordion-content"></div>
    </div>
    
    <div class="accordion-item">
        <button class="accordion-header" 
                hx-get="/faq/2" 
                hx-target="#faq-2">
            How does it work?
        </button>
        <div id="faq-2" class="accordion-content"></div>
    </div>
</div>
```

**Server:**

```java
@GetMapping("/faq/{id}")
public String getFaqAnswer(@PathVariable int id) {
    Faq faq = faqService.getById(id);
    return "<p>" + faq.getAnswer() + "</p>";
}
```

- Content wird erst bei Klick geladen
- Keine großen initial HTML Payloads

</v-clicks>

---

# Lazy Loading: Modal Content

<v-clicks>

**HTML:**

```html
<button hx-get="/user/123/details" 
        hx-target="#modal-content"
        onclick="showModal()">
    Show Details
</button>

<div id="modal" class="modal" style="display: none;">
    <div class="modal-content">
        <span class="close" onclick="hideModal()">&times;</span>
        <div id="modal-content">
            Loading...
        </div>
    </div>
</div>

<script>
function showModal() {
    document.getElementById('modal').style.display = 'block';
}
function hideModal() {
    document.getElementById('modal').style.display = 'none';
}
</script>
```

**Server:**

```java
@GetMapping("/user/{id}/details")
public String getUserDetails(@PathVariable Long id) {
    User user = userService.getById(id);
    return """
        <h3>%s</h3>
        <p>Email: %s</p>
        <p>Joined: %s</p>
    """.formatted(user.getName(), user.getEmail(), user.getJoinDate());
}
```

</v-clicks>

---

# Wiederverwendbare Widgets

<v-clicks>

**Problem:** Gleiche Komponente an mehreren Stellen

**Lösung:** Widget als eigener Endpoint

```java
@GetMapping("/widgets/weather")
public String getWeatherWidget(@RequestParam(required = false) String city) {
    if (city == null) city = "Berlin";
    
    Weather weather = weatherService.getCurrent(city);
    return """
        <div class="widget weather-widget">
            <h4>%s</h4>
            <div class="temp">%d°C</div>
            <div class="condition">%s</div>
        </div>
    """.formatted(city, weather.getTemp(), weather.getCondition());
}

@GetMapping("/widgets/notifications")
public String getNotificationsWidget() {
    List<Notification> notifications = notificationService.getLatest(5);
    return renderNotificationsWidget(notifications);
}
```

**Nutzung:**

```html
<div hx-get="/widgets/weather?city=Munich" hx-trigger="load"></div>
<div hx-get="/widgets/notifications" hx-trigger="load"></div>
```

</v-clicks>

---

# Wiederverwendbare Widgets: Vorteile

<v-clicks>

**1. Single Responsibility:**
- Jedes Widget hat eigene Route
- Unabhängig testbar
- Klare Separation

**2. Flexibel einsetzbar:**
```html
<!-- Dashboard -->
<div hx-get="/widgets/user-stats" hx-trigger="load"></div>

<!-- Sidebar -->
<div hx-get="/widgets/user-stats" hx-trigger="load"></div>

<!-- Admin Panel -->
<div hx-get="/widgets/user-stats" hx-trigger="load"></div>
```

**3. Unabhängige Updates:**
```html
<div hx-get="/widgets/stock-ticker" 
     hx-trigger="every 30s">
</div>
```

**4. Progressive Loading:**
- Kritische Widgets zuerst
- Nice-to-have später

</v-clicks>

---

# Lazy Loading: Dashboard Beispiel

<v-clicks>

**Initial HTML (fast!):**

```html
<div id="dashboard">
    <h1>Dashboard</h1>
    
    <!-- Kritisch: Sofort laden -->
    <div hx-get="/widgets/user-info" hx-trigger="load"></div>
    
    <!-- Wichtig: Mit kleiner Verzögerung -->
    <div hx-get="/widgets/recent-activity" 
         hx-trigger="load delay:100ms"></div>
    
    <!-- Nice-to-have: Später laden -->
    <div hx-get="/widgets/recommendations" 
         hx-trigger="load delay:500ms"></div>
    
    <!-- Heavy: Nur bei Bedarf -->
    <div hx-get="/widgets/analytics" 
         hx-trigger="revealed"></div>
</div>
```

**Resultat:**
- Seite erscheint sofort
- Content erscheint progressiv
- Bessere UX als alles auf einmal

</v-clicks>

---

# Lazy Loading mit Loading States

<v-clicks>

**Placeholder während Laden:**

```html
<div hx-get="/widgets/complex-chart" 
     hx-trigger="load"
     hx-indicator="#chart-spinner">
    <div id="chart-spinner" class="htmx-indicator">
        <div class="spinner"></div>
        Loading chart...
    </div>
</div>
```

**CSS:**

```css
.htmx-indicator {
    display: none;
}

.htmx-request .htmx-indicator {
    display: block;
}

.htmx-request.htmx-indicator {
    display: block;
}
```

**Oder inline mit HTMX Klassen:**

```html
<div hx-get="/widgets/data" hx-trigger="load">
    <div class="htmx-indicator">⏳ Loading...</div>
</div>
```

</v-clicks>

---

# Lazy Loading: Form Wizard

<v-clicks>

**Multi-Step Form - Step by Step laden:**

```html
<div id="wizard">
    <div id="step-1" hx-get="/wizard/step/1" hx-trigger="load">
        <!-- Step 1 lädt initial -->
    </div>
</div>
```

**Server:**

```java
@GetMapping("/wizard/step/1")
public String getStep1() {
    return """
        <form hx-post="/wizard/step/1" hx-target="#step-2">
            <h3>Step 1: Basic Info</h3>
            <input name="name" required>
            <input name="email" required>
            <button type="submit">Next</button>
        </form>
        <div id="step-2"></div>
    """;
}

@PostMapping("/wizard/step/1")
public String submitStep1(@RequestParam String name, @RequestParam String email) {
    // Validieren & speichern in Session
    wizardService.saveStep1(name, email);
    
    // Nächster Step wird geladen
    return getStep2();
}

@GetMapping("/wizard/step/2")
public String getStep2() {
    return """
        <form hx-post="/wizard/step/2" hx-target="#step-3">
            <h3>Step 2: Address</h3>
            <input name="street" required>
            <input name="city" required>
            <button type="submit">Next</button>
        </form>
        <div id="step-3"></div>
    """;
}
```

</v-clicks>

---

# Conditional Lazy Loading

<v-clicks>

**Nur laden wenn Bedingung erfüllt:**

```html
<div class="premium-feature">
    <div hx-get="/premium/analytics" 
         hx-trigger="load"
         hx-headers='{"X-User-Role": "premium"}'>
    </div>
</div>
```

**Server prüft Berechtigung:**

```java
@GetMapping("/premium/analytics")
public ResponseEntity<String> getPremiumAnalytics(
        @RequestHeader("X-User-Role") String role) {
    
    if (!"premium".equals(role)) {
        return ResponseEntity.status(403).body("""
            <div class="upgrade-prompt">
                <p>Premium feature - Upgrade to access</p>
                <button>Upgrade Now</button>
            </div>
        """);
    }
    
    AnalyticsData data = analyticsService.getAdvanced();
    return ResponseEntity.ok(renderAnalytics(data));
}
```

</v-clicks>

---

# Lazy Loading: Performance Trick

<v-clicks>

**Problem:** Viele Widgets laden parallel → Server Load

**Lösung:** Gestaffelt laden

```html
<div id="dashboard">
    <!-- 0ms -->
    <div hx-get="/widgets/critical" hx-trigger="load"></div>
    
    <!-- 100ms -->
    <div hx-get="/widgets/important" hx-trigger="load delay:100ms"></div>
    
    <!-- 200ms -->
    <div hx-get="/widgets/secondary" hx-trigger="load delay:200ms"></div>
    
    <!-- 300ms -->
    <div hx-get="/widgets/tertiary" hx-trigger="load delay:300ms"></div>
</div>
```

**Resultat:**
- Server bekommt Requests gestaffelt
- Browser rendert progressiv
- Bessere Perceived Performance

</v-clicks>

---

# Lazy Loading mit Caching

<v-clicks>

**Client-Side Caching für bereits geladene Widgets:**

```html
<div class="tabs">
    <button hx-get="/tab/overview" 
            hx-target="#content"
            hx-select="#tab-content"
            onclick="this.setAttribute('hx-get', '')">
        Overview
    </button>
</div>
```

**Oder: Server-Side Caching:**

```java
@GetMapping("/widgets/expensive")
@Cacheable(value = "widgets", key = "#userId")
public String getExpensiveWidget(@RequestParam Long userId) {
    // Teure Operation nur einmal pro User
    ExpensiveData data = expensiveService.compute(userId);
    return renderWidget(data);
}
```

**HTTP Cache Headers:**

```java
@GetMapping("/widgets/static")
public ResponseEntity<String> getStaticWidget() {
    return ResponseEntity.ok()
        .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
        .body(renderStaticWidget());
}
```

</v-clicks>

---

# Lazy Loading: Out-of-Band Updates

<v-clicks>

**Problem:** Widget in Sidebar updaten nach Action

**Lösung:** OOB Swap

```html
<!-- Main content -->
<form hx-post="/cart/add">
    <button>Add to Cart</button>
</form>

<!-- Sidebar -->
<div id="cart-widget" hx-get="/widgets/cart" hx-trigger="load">
</div>
```

**Server Response nach Add:**

```java
@PostMapping("/cart/add")
public String addToCart(@RequestParam Long productId) {
    cartService.add(productId);
    
    // Main response
    String mainContent = "<div>Added to cart!</div>";
    
    // OOB: Update cart widget
    String cartWidget = """
        <div id="cart-widget" hx-swap-oob="true">
            %s
        </div>
    """.formatted(renderCartWidget());
    
    return mainContent + cartWidget;
}
```

**Widget wird automatisch aktualisiert!** ✨

</v-clicks>

---

# Lazy Loading Best Practices

<v-clicks>

**1. Kritische Inhalte zuerst:**
```html
<div hx-get="/critical" hx-trigger="load"></div>
<div hx-get="/nice-to-have" hx-trigger="load delay:500ms"></div>
```

**2. Loading States zeigen:**
```html
<div hx-get="/widget" hx-trigger="load">
    <div class="htmx-indicator">Loading...</div>
</div>
```

**3. Error Handling:**
```html
<div hx-get="/widget" hx-trigger="load">
    <div class="htmx-indicator">Loading...</div>
</div>

<script>
document.body.addEventListener('htmx:responseError', function(evt) {
    if (evt.detail.xhr.status === 500) {
        evt.detail.target.innerHTML = '<div class="error">Failed to load</div>';
    }
});
</script>
```

**4. Wiederverwendbare Endpoints:**
- Ein Widget = Ein Endpoint
- Parameter für Variationen
- Unabhängig testbar

</v-clicks>

---

# Routing-Setup für Widgets

<v-clicks>

**Option 1: Dedicated Widget Controller**

```java
@RestController
@RequestMapping("/widgets")
public class WidgetController {
    
    @GetMapping("/weather")
    public String weather(@RequestParam String city) { ... }
    
    @GetMapping("/notifications")
    public String notifications() { ... }
    
    @GetMapping("/user-stats")
    public String userStats() { ... }
}
```

**Option 2: Feature-basiert**

```java
@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    
    @GetMapping("/overview")
    public String overview() { ... }
    
    @GetMapping("/stats")
    public String stats() { ... }
}
```

**Beide Ansätze ermöglichen Wiederverwendung!**

</v-clicks>

---

# Practical Example: E-Commerce Product Page

<v-clicks>

```html
<div id="product-page">
    <!-- Critical: Sofort laden -->
    <div hx-get="/product/123/details" hx-trigger="load"></div>
    
    <!-- Important: Etwas verzögert -->
    <div hx-get="/product/123/price" 
         hx-trigger="load delay:50ms"></div>
    
    <!-- Secondary: Wenn sichtbar -->
    <div hx-get="/product/123/reviews" 
         hx-trigger="revealed"></div>
    
    <div hx-get="/product/123/related" 
         hx-trigger="revealed"></div>
    
    <!-- On-demand: Erst bei Tab Click -->
    <div class="tabs">
        <button hx-get="/product/123/specs" 
                hx-target="#tab-content">
            Specifications
        </button>
    </div>
    <div id="tab-content"></div>
</div>
```

**Resultat:**
- Seite lädt blitzschnell
- Content erscheint progressiv
- Heavy Content nur on-demand

</v-clicks>

---

# Zusammenfassung

<v-clicks>

**Infinite Scroll:**
- `hx-trigger="revealed"` für automatisches Laden
- Gut für Listen, Feeds, Social Media

**Lazy Loading (wichtiger!):**
- Nur laden was gebraucht wird
- Schnellerer Initial Load
- Bessere UX

**Wiederverwendbare Widgets:**
- Ein Widget = Ein Endpoint
- Flexibel einsetzbar
- Unabhängig testbar
- Progressive Loading möglich

**Use Cases:**
- Tabs, Accordions, Modals
- Dashboard Widgets
- Complex Forms (Wizard)
- E-Commerce Pages

</v-clicks>

---

# Übung 8: Dashboard mit Lazy Widgets

**Erstelle ein Dashboard mit verschiedenen Widgets:**

<v-clicks>

**Requirements:**
1. 3 Tabs für User, Rollen und Permissions
2. Jeder Tab lädt Content lazy beim Klick
3. Welcher Tab offen ist soll in der URL reflektiert werden (hx-push-url) (dh. Browser back button funktioniert)
4. Loading States für jeden Tab
5. (optional) Der User Tab zeigt User Liste mit Infinite Scroll

**Ziel:** Verstehen wie Lazy Loading Seiten schneller macht! ⚡

</v-clicks>
