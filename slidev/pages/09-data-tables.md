# Data Tables

---

# Überblick

<v-clicks>

**Typische Data Table Features:**
- **Sorting** - Nach Spalten sortieren
- **Filtering** - Nach Text oder Kriterien filtern
- **Pagination** - Große Datensätze aufteilen
- **Debounce** - Verzögerung bei Eingaben
- **Caching** - Weniger Server Requests

**Warum mit HTMX?**
- Volle Server-Kontrolle über Logik
- Keine komplexe Client-Side State Management
- Progressive Enhancement
- Einfache Implementierung

**HTMX macht Data Tables simpel!** 🚀

</v-clicks>

---

# Basis Data Table

<v-clicks>

**HTML:**

```html
<table id="users-table">
    <thead>
        <tr>
            <th>Name</th>
            <th>Email</th>
            <th>Role</th>
            <th>Actions</th>
        </tr>
    </thead>
    <tbody hx-get="/users/table" hx-trigger="load">
        <!-- Lädt beim Page Load -->
    </tbody>
</table>
```

**Server:**

```java
@GetMapping("/users/table")
public String getUsersTable() {
    List<User> users = userService.getAll();
    
    StringBuilder html = new StringBuilder();
    for (User user : users) {
        html.append("""
            <tr>
                <td>%s</td>
                <td>%s</td>
                <td>%s</td>
                <td>
                    <button hx-get="/users/%d/edit" hx-target="#modal">
                        Edit
                    </button>
                </td>
            </tr>
        """.formatted(user.getName(), user.getEmail(), 
                     user.getRole(), user.getId()));
    }
    
    return html.toString();
}
```

</v-clicks>

---

# Sorting - Client Trigger

<v-clicks>

**HTML mit sortierbaren Headers:**

```html
<table>
    <thead>
        <tr>
            <th>
                <a href="#" 
                   hx-get="/users/table?sort=name&order=asc"
                   hx-target="#table-body">
                    Name ↑
                </a>
            </th>
            <th>
                <a href="#" 
                   hx-get="/users/table?sort=email&order=asc"
                   hx-target="#table-body">
                    Email ↑
                </a>
            </th>
            <th>Role</th>
        </tr>
    </thead>
    <tbody id="table-body" hx-get="/users/table" hx-trigger="load">
    </tbody>
</table>
```

**Problem:** Headers bleiben gleich nach Sort

</v-clicks>

---

# Sorting - Besserer Ansatz

<v-clicks>

**Komplette Tabelle neu rendern:**

```html
<div id="users-table" hx-get="/users/table" hx-trigger="load">
    <!-- Komplette Tabelle wird hier gerendert -->
</div>
```

**Server generiert Headers UND Body:**

```java
@GetMapping("/users/table")
public String getUsersTable(
        @RequestParam(required = false, defaultValue = "name") String sort,
        @RequestParam(required = false, defaultValue = "asc") String order) {
    
    List<User> users = userService.getAll(sort, order);
    
    String nextOrder = "asc".equals(order) ? "desc" : "asc";
    String arrow = "asc".equals(order) ? "↑" : "↓";
    
    return """
        <table>
            <thead>
                <tr>
                    <th>
                        <a href="#" 
                           hx-get="/users/table?sort=name&order=%s"
                           hx-target="#users-table">
                            Name %s
                        </a>
                    </th>
                    <th>
                        <a href="#" 
                           hx-get="/users/table?sort=email&order=%s"
                           hx-target="#users-table">
                            Email
                        </a>
                    </th>
                </tr>
            </thead>
            <tbody>
                %s
            </tbody>
        </table>
    """.formatted(
        "name".equals(sort) ? nextOrder : "asc",
        "name".equals(sort) ? arrow : "",
        "email".equals(sort) ? nextOrder : "asc",
        renderRows(users)
    );
}
```

</v-clicks>

---

# Filtering - Search Input

<v-clicks>

**HTML:**

```html
<div id="users-container">
    <input type="text" 
           name="search"
           placeholder="Search users..."
           hx-get="/users/table"
           hx-trigger="keyup changed delay:500ms"
           hx-target="#users-table"
           hx-include="[name='role']">
    
    <select name="role"
            hx-get="/users/table"
            hx-trigger="change"
            hx-target="#users-table"
            hx-include="[name='search']">
        <option value="">All Roles</option>
        <option value="admin">Admin</option>
        <option value="user">User</option>
    </select>
    
    <div id="users-table" hx-get="/users/table" hx-trigger="load">
    </div>
</div>
```

**Wichtig:**
- `delay:500ms` - Debounce
- `hx-include` - Andere Filter mitschicken

</v-clicks>

---

# Filtering - Server Side

<v-clicks>

```java
@GetMapping("/users/table")
public String getUsersTable(
        @RequestParam(required = false) String search,
        @RequestParam(required = false) String role,
        @RequestParam(required = false, defaultValue = "name") String sort,
        @RequestParam(required = false, defaultValue = "asc") String order) {
    
    // Filter & Sort
    List<User> users = userService.search(search, role, sort, order);
    
    return renderTable(users, search, role, sort, order);
}
```

**Service Layer:**

```java
public List<User> search(String search, String role, String sort, String order) {
    Specification<User> spec = Specification.where(null);
    
    if (search != null && !search.isEmpty()) {
        spec = spec.and((root, query, cb) -> 
            cb.or(
                cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%"),
                cb.like(cb.lower(root.get("email")), "%" + search.toLowerCase() + "%")
            )
        );
    }
    
    if (role != null && !role.isEmpty()) {
        spec = spec.and((root, query, cb) -> 
            cb.equal(root.get("role"), role)
        );
    }
    
    Sort sortObj = Sort.by(
        "desc".equals(order) ? Sort.Direction.DESC : Sort.Direction.ASC,
        sort
    );
    
    return userRepository.findAll(spec, sortObj);
}
```

</v-clicks>

---

# Pagination - Basics

<v-clicks>

**HTML:**

```html
<div id="users-container">
    <div id="users-table" hx-get="/users/table?page=1" hx-trigger="load">
    </div>
</div>
```

**Server:**

```java
@GetMapping("/users/table")
public String getUsersTable(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size) {
    
    Page<User> userPage = userService.getPage(page - 1, size);
    
    return """
        <table>
            <tbody>%s</tbody>
        </table>
        <div class="pagination">
            %s
        </div>
    """.formatted(
        renderRows(userPage.getContent()),
        renderPagination(userPage)
    );
}

private String renderPagination(Page<User> page) {
    StringBuilder html = new StringBuilder();
    
    if (page.hasPrevious()) {
        html.append("""
            <button hx-get="/users/table?page=%d" hx-target="#users-table">
                Previous
            </button>
        """.formatted(page.getNumber()));
    }
    
    html.append("<span>Page %d of %d</span>"
        .formatted(page.getNumber() + 1, page.getTotalPages()));
    
    if (page.hasNext()) {
        html.append("""
            <button hx-get="/users/table?page=%d" hx-target="#users-table">
                Next
            </button>
        """.formatted(page.getNumber() + 2));
    }
    
    return html.toString();
}
```

</v-clicks>

---

# Pagination - Mit Nummern

<v-clicks>

```java
private String renderPagination(Page<User> page, String search, String role) {
    StringBuilder html = new StringBuilder("<div class='pagination'>");
    
    int current = page.getNumber() + 1;
    int total = page.getTotalPages();
    
    // Previous
    if (page.hasPrevious()) {
        html.append(createPageButton(current - 1, "«", search, role));
    }
    
    // Numbered pages
    int start = Math.max(1, current - 2);
    int end = Math.min(total, current + 2);
    
    for (int i = start; i <= end; i++) {
        String cssClass = i == current ? "active" : "";
        html.append(createPageButton(i, String.valueOf(i), search, role, cssClass));
    }
    
    // Next
    if (page.hasNext()) {
        html.append(createPageButton(current + 1, "»", search, role));
    }
    
    html.append("</div>");
    return html.toString();
}

private String createPageButton(int page, String label, String search, 
                               String role, String cssClass) {
    String url = "/users/table?page=" + page;
    if (search != null) url += "&search=" + search;
    if (role != null) url += "&role=" + role;
    
    return """
        <button class="%s" 
                hx-get="%s" 
                hx-target="#users-table">
            %s
        </button>
    """.formatted(cssClass, url, label);
}
```

</v-clicks>

---

# Alles zusammen: Filter + Sort + Pagination

<v-clicks>

**HTML:**

```html
<div id="users-container">
    <div class="filters">
        <input type="text" 
               name="search"
               placeholder="Search..."
               hx-get="/users/table"
               hx-trigger="keyup changed delay:500ms"
               hx-target="#users-table"
               hx-include="[name='role']">
        
        <select name="role"
                hx-get="/users/table"
                hx-trigger="change"
                hx-target="#users-table"
                hx-include="[name='search']">
            <option value="">All Roles</option>
            <option value="admin">Admin</option>
            <option value="user">User</option>
        </select>
    </div>
    
    <div id="users-table" hx-get="/users/table" hx-trigger="load">
    </div>
</div>
```

**Server nimmt alle Parameter entgegen:**

```java
@GetMapping("/users/table")
public String getUsersTable(
        @RequestParam(required = false) String search,
        @RequestParam(required = false) String role,
        @RequestParam(defaultValue = "name") String sort,
        @RequestParam(defaultValue = "asc") String order,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size) {
    
    Page<User> users = userService.search(search, role, sort, order, page - 1, size);
    
    return renderTable(users, search, role, sort, order);
}
```

</v-clicks>

---

# Debounce & Throttle

<v-clicks>

**Debounce - Warte bis User aufhört zu tippen:**

```html
<input type="text" 
       name="search"
       hx-get="/users/table"
       hx-trigger="keyup changed delay:500ms"
       hx-target="#users-table">
```

- `delay:500ms` - Wartet 500ms nach letztem Keypress
- Reduziert Server Requests drastisch

**Throttle - Maximal alle X ms:**

```html
<input type="range" 
       name="price"
       hx-get="/products/table"
       hx-trigger="change throttle:1s"
       hx-target="#products-table">
```

- `throttle:1s` - Maximal 1 Request pro Sekunde
- Gut für Slider, Scroll Events

</v-clicks>

---

# Loading States

<v-clicks>

**Option 1: HTMX Indicator:**

```html
<div id="users-table" hx-indicator="#spinner">
    <!-- Table content -->
</div>

<div id="spinner" class="htmx-indicator">
    <div class="spinner"></div>
    Loading...
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
```

**Option 2: Inline:**

```html
<div id="users-table">
    <div class="htmx-indicator">Loading...</div>
    <!-- Table content -->
</div>
```

</v-clicks>

---

# Caching - Client Side

<v-clicks>

**Problem:** Bei jedem Page Change Request zum Server

**Lösung 1: Browser Cache mit Cache-Control:**

```java
@GetMapping("/users/table")
public ResponseEntity<String> getUsersTable(...) {
    String html = renderTable(...);
    
    return ResponseEntity.ok()
        .cacheControl(CacheControl.maxAge(5, TimeUnit.MINUTES))
        .body(html);
}
```

**Lösung 2: HTMX History Cache:**

```html
<div hx-get="/users/table" 
     hx-push-url="true"
     hx-target="#users-table">
</div>
```

- `hx-push-url` aktiviert Browser History
- Bei Back/Forward: Aus Cache laden
- Kein Server Request

</v-clicks>

---

# Caching - Server Side

<v-clicks>

**Spring Cache:**

```java
@Service
public class UserService {
    
    @Cacheable(value = "users", key = "#page + '-' + #size + '-' + #search + '-' + #role")
    public Page<User> search(String search, String role, 
                            String sort, String order, 
                            int page, int size) {
        // Query wird nur bei Cache Miss ausgeführt
        Specification<User> spec = buildSpec(search, role);
        Pageable pageable = PageRequest.of(page, size, buildSort(sort, order));
        return userRepository.findAll(spec, pageable);
    }
    
    @CacheEvict(value = "users", allEntries = true)
    public User save(User user) {
        // Cache leeren bei Änderungen
        return userRepository.save(user);
    }
}
```

**Configuration:**

```java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("users");
    }
}
```

</v-clicks>

---

# Caching Best Practices

<v-clicks>

**1. Cache Invalidation:**

```java
@PostMapping("/users/{id}")
public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody User user) {
    userService.save(user);
    
    // Client-seitigen Cache invalidieren
    return ResponseEntity.ok()
        .header("HX-Trigger", "refreshTable")
        .body("<div>User updated</div>");
}
```

```javascript
document.body.addEventListener('refreshTable', function() {
    htmx.trigger('#users-table', 'load');
});
```

**2. Selective Caching:**

- Nur stabile Daten cachen
- Kurze TTL für volatile Daten
- No-Cache für personalisierte Inhalte

</v-clicks>

---

# Row Actions

<v-clicks>

**Inline Edit:**

```html
<tr id="user-123">
    <td>John Doe</td>
    <td>john@example.com</td>
    <td>
        <button hx-get="/users/123/edit" 
                hx-target="#user-123"
                hx-swap="outerHTML">
            Edit
        </button>
        <button hx-delete="/users/123"
                hx-confirm="Delete this user?"
                hx-target="#user-123"
                hx-swap="outerHTML">
            Delete
        </button>
    </td>
</tr>
```

**Server - Edit Response:**

```java
@GetMapping("/users/{id}/edit")
public String editUser(@PathVariable Long id) {
    User user = userService.getById(id);
    
    return """
        <tr id="user-%d">
            <td><input name="name" value="%s"></td>
            <td><input name="email" value="%s"></td>
            <td>
                <button hx-post="/users/%d" 
                        hx-target="#user-%d"
                        hx-include="closest tr">
                    Save
                </button>
                <button hx-get="/users/%d" 
                        hx-target="#user-%d">
                    Cancel
                </button>
            </td>
        </tr>
    """.formatted(id, user.getName(), user.getEmail(), 
                 id, id, id, id);
}
```

</v-clicks>

---

# Bulk Actions

<v-clicks>

**HTML mit Checkboxes:**

```html
<form id="bulk-form">
    <button hx-post="/users/bulk-delete"
            hx-include="#bulk-form"
            hx-confirm="Delete selected users?">
        Delete Selected
    </button>
    
    <table>
        <tbody id="users-table">
            <tr>
                <td><input type="checkbox" name="ids" value="1"></td>
                <td>John Doe</td>
                <td>john@example.com</td>
            </tr>
            <tr>
                <td><input type="checkbox" name="ids" value="2"></td>
                <td>Jane Smith</td>
                <td>jane@example.com</td>
            </tr>
        </tbody>
    </table>
</form>
```

**Server:**

```java
@PostMapping("/users/bulk-delete")
public String bulkDelete(@RequestParam List<Long> ids) {
    userService.deleteAll(ids);
    
    // Tabelle neu laden
    return getUsersTable(null, null, "name", "asc", 1, 10);
}
```

</v-clicks>

---

# Advanced: Virtualized Scrolling

<v-clicks>

**Für sehr große Tabellen (1000+ Zeilen):**

```html
<div style="height: 600px; overflow-y: auto;" id="table-container">
    <div style="height: 20000px; position: relative;">
        <!-- Placeholder für Gesamthöhe -->
        
        <div id="visible-rows" 
             hx-get="/users/rows?start=0&end=50"
             hx-trigger="load, scroll from:#table-container throttle:100ms"
             style="position: absolute; top: 0;">
            <!-- Nur sichtbare Rows werden gerendert -->
        </div>
    </div>
</div>
```

**Server berechnet sichtbaren Bereich:**

```java
@GetMapping("/users/rows")
public String getVisibleRows(
        @RequestParam int start,
        @RequestParam int end) {
    
    List<User> users = userService.getRange(start, end);
    int offset = start * ROW_HEIGHT;
    
    return """
        <div style="position: absolute; top: %dpx;">
            %s
        </div>
    """.formatted(offset, renderRows(users));
}
```

**Für die meisten Cases nicht nötig!** Pagination reicht.

</v-clicks>

---

# Responsive Tables

<v-clicks>

**Mobile: Cards statt Table:**

```java
@GetMapping("/users/table")
public String getUsersTable(
        @RequestHeader(value = "User-Agent", required = false) String userAgent) {
    
    boolean isMobile = userAgent != null && userAgent.contains("Mobile");
    
    List<User> users = userService.getAll();
    
    if (isMobile) {
        return renderCards(users);
    } else {
        return renderTable(users);
    }
}

private String renderCards(List<User> users) {
    StringBuilder html = new StringBuilder("<div class='user-cards'>");
    for (User user : users) {
        html.append("""
            <div class="card">
                <h3>%s</h3>
                <p>%s</p>
                <span class="badge">%s</span>
            </div>
        """.formatted(user.getName(), user.getEmail(), user.getRole()));
    }
    html.append("</div>");
    return html.toString();
}
```

**Oder: CSS Media Queries für responsive Table**

</v-clicks>

---

# Performance Best Practices

<v-clicks>

**1. Index Database Columns:**

```java
@Entity
@Table(indexes = {
    @Index(name = "idx_name", columnList = "name"),
    @Index(name = "idx_email", columnList = "email")
})
public class User {
    // ...
}
```

**2. Limit Result Size:**

```java
@GetMapping("/users/table")
public String getUsersTable(..., 
        @RequestParam(defaultValue = "20") int size) {
    
    // Nie mehr als 100 pro Page
    size = Math.min(size, 100);
    
    Page<User> users = userService.getPage(..., size);
    return renderTable(users);
}
```

**3. Debounce Search:**

```html
<input hx-trigger="keyup changed delay:500ms">
```

**4. Use Projections für große Tables:**

```java
public interface UserProjection {
    Long getId();
    String getName();
    String getEmail();
    // Keine Heavy Relations
}
```

</v-clicks>

---

# Komplettes Beispiel: User Management Table

<v-clicks>

**Features:**
- ✅ Search (debounced)
- ✅ Role Filter
- ✅ Column Sorting
- ✅ Pagination
- ✅ Inline Edit
- ✅ Delete with Confirmation
- ✅ Loading States

**Code:** https://github.com/workshop/htmx-data-table-example

**Demo:** Live coding! 💻

</v-clicks>

---

# Zusammenfassung

<v-clicks>

**Data Tables mit HTMX:**
- **Sorting** - Server-side mit URL Parameters
- **Filtering** - Search + Debounce für Performance
- **Pagination** - Page-basiert mit Navigation
- **Caching** - Client & Server Side
- **Actions** - Inline Edit, Delete, Bulk Operations

**Best Practices:**
- Debounce bei Search (500ms)
- Throttle bei Scroll Events
- Cache nur stabile Daten
- Loading States zeigen
- Database Indexes nutzen

**HTMX = Einfache, mächtige Data Tables!** 🎯

</v-clicks>

---

# Übung 9: Product Management Table

**Erstelle eine vollständige Product Management Tabelle:**

<v-clicks>

**Requirements:**
1. **Tabelle mit Columns:** Name, Category, Price, Stock, Actions
2. **Search** - Produktname durchsuchen (debounced 500ms)
3. **Filter** - Nach Category filtern (Dropdown)
4. **Sorting** - Nach Name, Price, Stock sortierbar
5. **Pagination** - 10 Items pro Seite
6. **Inline Edit** - Produkt direkt in Tabelle editieren
7. **Delete** - Mit Confirmation Dialog
8. **Loading State** - Während Requests

**Bonus:**
- Bulk Delete (mehrere Produkte auf einmal)
- Price Range Filter (Slider mit Throttle)
- Export als CSV Button
- Row Highlighting bei Edit
- Optimistic UI für Delete (Row sofort entfernen)

**Ziel:** Vollständiges CRUD mit HTMX! 🚀

</v-clicks>
