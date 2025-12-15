# Error Handling in HTMX

---

# Error Handling - Überblick

<v-clicks>

**Warum wichtig?**
- Unvorhergesehene Fehler passieren (Server-Errors, Netzwerkprobleme)
- User muss Feedback erhalten - auch bei Fehlern
- Verhindert "schwarzes Loch" User Experience

**Was wir behandeln:**
- HTMX Error Events
- Status Code Handling (4xx, 5xx)
- Server-Side Error Responses
- Global Error Notifications
- Retry Patterns
- Best Practices mit wenig JavaScript

</v-clicks>

---

# Das Problem ohne Error Handling

<v-clicks>

```html
<button hx-get="/api/data" hx-target="#content">
  Load Data
</button>
<div id="content"></div>
```

**Was passiert bei Error 500?**
- ❌ Keine Änderung im UI
- ❌ User sieht nichts
- ❌ Loading Indicator bleibt hängen
- ❌ Keine Möglichkeit zu retry

**Result:** User ist frustriert und verwirrt 😕

</v-clicks>

---

# HTMX Error Events

<v-clicks>

HTMX löst spezifische Events bei Problemen aus:

**Response Errors (Server antwortet mit Error):**
```javascript
htmx:responseError  // 4xx, 5xx Status Codes
```

**Request Errors (Netzwerk/Browser Probleme):**
```javascript
htmx:sendError      // Kann nicht senden (offline, CORS)
htmx:timeout        // Request dauert zu lange
```

**Event Details verfügbar:**
- `event.detail.xhr` - XMLHttpRequest Objekt
- `event.detail.xhr.status` - HTTP Status Code
- `event.detail.xhr.statusText` - Status Text
- `event.detail.target` - Ziel-Element

</v-clicks>

---

# Status Codes verstehen

<v-clicks>

**4xx - Client Errors:**
- `400 Bad Request` - Ungültige Daten
- `401 Unauthorized` - Nicht eingeloggt
- `403 Forbidden` - Keine Berechtigung
- `404 Not Found` - Ressource existiert nicht

**5xx - Server Errors:**
- `500 Internal Server Error` - Unvorhergesehener Server-Fehler
- `502 Bad Gateway` - Gateway/Proxy Problem
- `503 Service Unavailable` - Server überlastet/Wartung
- `504 Gateway Timeout` - Upstream-Server antwortet nicht

Fokus heute: **500er Errors** - die unvorhersehbaren!

</v-clicks>

---

# Server-Side Error Responses

<v-clicks>

**Option 1: HTML Error Response senden**

```java
@GetMapping("/users")
public ResponseEntity<String> getUsers() {
    try {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(renderUsers(users));
    } catch (Exception e) {
        String errorHtml = """
            <div class="alert alert-error">
                <strong>Error:</strong> Could not load users.
                <button hx-get="/users" hx-swap="outerHTML">
                    Retry
                </button>
            </div>
        """;
        return ResponseEntity.status(500).body(errorHtml);
    }
}
```

✅ User sieht Fehler direkt im Ziel-Element

</v-clicks>

---

# Server Error mit HX-Retarget

<v-clicks>

**Option 2: Error in separaten Container umleiten**

```java
@ExceptionHandler(Exception.class)
public ResponseEntity<String> handleError(Exception e) {
    logger.error("Error occurred", e);
    
    String errorHtml = """
        <div class="alert alert-error">
            Something went wrong. Please try again.
        </div>
    """;
    
    return ResponseEntity.status(500)
        .header("HX-Retarget", "#error-container")
        .header("HX-Reswap", "innerHTML")
        .body(errorHtml);
}
```

**HTML:**
```html
<div id="error-container"></div>
```

✅ Errors erscheinen an konsistenter Stelle

</v-clicks>

---

# Global Error Container Pattern

<v-clicks>

**HTML Setup:**
```html
<body>
  <!-- Global Error Container -->
  <div id="global-errors" class="error-toast"></div>
  
  <!-- Deine App -->
  <div id="app">...</div>
</body>
```

**CSS:**
```css
.error-toast {
  position: fixed;
  top: 20px;
  right: 20px;
  min-width: 300px;
  z-index: 9999;
}
.alert {
  padding: 1rem;
  margin-bottom: 0.5rem;
  background: #fee;
  border: 1px solid #f88;
  border-radius: 4px;
}
```

</v-clicks>

---

# Client-Side: Global Error Handler

<v-clicks>

**Minimaler JavaScript Error Handler:**

```javascript
// Global Error Handling für alle HTMX Requests
document.body.addEventListener('htmx:responseError', function(evt) {
  const status = evt.detail.xhr.status;
  const container = document.getElementById('global-errors');
  
  container.innerHTML = `
    <div class="alert">
      <strong>Error ${status}:</strong> 
      ${getErrorMessage(status)}
    </div>
  `;
  
  // Auto-hide nach 5 Sekunden
  setTimeout(() => container.innerHTML = '', 5000);
});

function getErrorMessage(status) {
  if (status >= 500) return 'Server error. Please try again later.';
  if (status === 404) return 'Resource not found.';
  if (status === 401) return 'Please log in.';
  return 'Something went wrong.';
}
```

</v-clicks>

---

# Alternative: HTMX ohne JavaScript

<v-clicks>

**Server sendet Error direkt an globalen Container:**

```java
@ExceptionHandler(Exception.class)
public ResponseEntity<String> handleError(Exception e) {
    String errorHtml = """
        <div id="global-errors" hx-swap-oob="innerHTML">
            <div class="alert">
                <strong>Error:</strong> Something went wrong.
                <button hx-get="%s">Retry</button>
            </div>
        </div>
    """.formatted(getRetryUrl(e));
    
    return ResponseEntity.status(500).body(errorHtml);
}

private String getRetryUrl(Exception e) {
    // Determine retry URL based on exception
    return "/retry";
}
```

**HTML benötigt nur:**
```html
<div id="global-errors"></div>
```

✅ Kein JavaScript notwendig!

</v-clicks>

---

# Netzwerk Errors behandeln

<v-clicks>

**htmx:sendError - Offline/Netzwerk Probleme:**

```javascript
document.body.addEventListener('htmx:sendError', function(evt) {
  const container = document.getElementById('global-errors');
  container.innerHTML = `
    <div class="alert alert-warning">
      <strong>Network Error:</strong> 
      Please check your internet connection.
    </div>
  `;
});
```

**htmx:timeout - Request dauert zu lange:**

```javascript
document.body.addEventListener('htmx:timeout', function(evt) {
  const container = document.getElementById('global-errors');
  container.innerHTML = `
    <div class="alert alert-warning">
      <strong>Timeout:</strong> 
      Server is taking too long to respond.
    </div>
  `;
});
```

</v-clicks>

---

# Retry Pattern

<v-clicks>

**Automatischer Retry bei Server Errors:**

```html
<button hx-get="/api/data" 
        hx-target="#content"
        data-retry-count="0">
  Load Data
</button>
```

```javascript
document.body.addEventListener('htmx:responseError', function(evt) {
  const element = evt.detail.elt;
  const status = evt.detail.xhr.status;
  
  // Nur bei 5xx Server Errors retry
  if (status >= 500 && status < 600) {
    const retries = parseInt(element.dataset.retryCount || 0);
    
    if (retries < 3) {
      element.dataset.retryCount = retries + 1;
      // Warte 1 Sekunde, dann retry
      setTimeout(() => htmx.trigger(element, 'click'), 1000);
    } else {
      showError('Server nicht erreichbar. Bitte später versuchen.');
    }
  }
});
```

</v-clicks>

---

# Spezifische Error Handling per Element

<v-clicks>

**Inline Error Messages:**

```html
<form hx-post="/submit" hx-target="#result">
  <input name="data">
  <button type="submit">Submit</button>
</form>

<div id="result"></div>

<script>
document.querySelector('form').addEventListener('htmx:responseError', 
  function(evt) {
    evt.preventDefault(); // Verhindert Bubble zu Global Handler
    
    document.getElementById('result').innerHTML = `
      <div class="error">
        Form submission failed. Please try again.
      </div>
    `;
  }
);
</script>
```

✅ Element-spezifisches Handling überschreibt globales

</v-clicks>

---

# Swap verhindern bei Errors

<v-clicks>

**Default:** HTMX swapped Error-Responses nicht (nur 200-299)

**Manuell Error-Content swappen:**

```javascript
document.body.addEventListener('htmx:beforeSwap', function(evt) {
  // Bei 404: Zeige trotzdem Response (z.B. "Not Found" Seite)
  if (evt.detail.xhr.status === 404) {
    evt.detail.shouldSwap = true;
    evt.detail.isError = false;
  }
  
  // Bei 500: Nicht swappen, nur Notification
  if (evt.detail.xhr.status >= 500) {
    evt.detail.shouldSwap = false;
    showErrorNotification(evt.detail.xhr);
  }
});
```

✅ Volle Kontrolle über Swap-Verhalten

</v-clicks>

---

# Loading States bei Errors

<v-clicks>

**Problem:** Loading Indicator bleibt bei Error hängen

**Lösung:** HTMX entfernt automatisch `.htmx-request` Class

```css
.htmx-request {
  opacity: 0.5;
  pointer-events: none;
}
.htmx-request::after {
  content: " ⏳";
}
```

**Zusätzlich: Error State anzeigen**

```javascript
document.body.addEventListener('htmx:responseError', function(evt) {
  evt.detail.elt.classList.add('htmx-error');
});

document.body.addEventListener('htmx:beforeRequest', function(evt) {
  evt.detail.elt.classList.remove('htmx-error');
});
```

```css
.htmx-error {
  border: 2px solid red !important;
}
```

</v-clicks>

---

# Best Practices - Error Handling

<v-clicks>

**1. Immer User Feedback geben**
```javascript
// Global Error Handler als Minimum
document.body.addEventListener('htmx:responseError', showError);
```

**2. Server-Side HTML für Errors**
```java
// Server rendert Error-HTML, nicht nur Status Code
ResponseEntity.status(500).body("<div class='error'>...</div>");
```

**3. Retry nur bei 5xx**
```javascript
// 4xx = Client Problem → kein Retry
// 5xx = Server Problem → Retry möglich
if (status >= 500) { /* retry */ }
```

**4. Error Messages benutzerfreundlich**
```javascript
// ❌ "Error 500: Internal Server Error"
// ✅ "Something went wrong. Please try again."
```

</v-clicks>

---

# Best Practices (2)

<v-clicks>

**5. OOB Swaps für konsistente Error-Anzeige**
```html
<div id="global-errors" hx-swap-oob="innerHTML">Error!</div>
```

**6. Progressive Enhancement**
```html
<!-- Funktioniert auch ohne HTMX -->
<form action="/submit" method="POST" 
      hx-post="/submit" hx-target="#result">
```

**7. Logging auf Server**
```java
logger.error("Error processing request", exception);
```

**8. Unterschiedliche Handling-Strategien**
- Global Handler für unerwartete Errors
- Element-spezifisch für bekannte Errors
- Retry nur wo sinnvoll

</v-clicks>

---

# Komplettes Beispiel

<v-clicks>

**HTML:**
```html
<body hx-ext="loading-states">
  <div id="global-errors" class="toast-container"></div>
  
  <button hx-get="/users" 
          hx-target="#user-list"
          hx-indicator="#spinner">
    Load Users
  </button>
  
  <div id="spinner" class="htmx-indicator">Loading...</div>
  <div id="user-list"></div>
  
  <script src="/js/error-handler.js"></script>
</body>
```

</v-clicks>

---

# Komplettes Beispiel - JavaScript

<v-clicks>

```javascript
// error-handler.js
(function() {
  const errorContainer = document.getElementById('global-errors');
  
  // Global Error Handler
  document.body.addEventListener('htmx:responseError', function(evt) {
    const status = evt.detail.xhr.status;
    showError(status >= 500 
      ? 'Server error. Retrying...' 
      : 'Request failed.');
    
    // Auto-retry bei 5xx
    if (status >= 500) {
      retryRequest(evt.detail.elt);
    }
  });
  
  // Network Error Handler
  document.body.addEventListener('htmx:sendError', function() {
    showError('Network error. Check your connection.');
  });
  
  function showError(message) {
    errorContainer.innerHTML = `
      <div class="alert alert-error">${message}</div>
    `;
    setTimeout(() => errorContainer.innerHTML = '', 5000);
  }
  
  function retryRequest(element) {
    const retries = parseInt(element.dataset.retry || 0);
    if (retries < 2) {
      element.dataset.retry = retries + 1;
      setTimeout(() => htmx.trigger(element, 'click'), 2000);
    }
  }
})();
```

</v-clicks>

---

# Komplettes Beispiel - Server

<v-clicks>

```java
@RestController
public class UserController {
    
    @Autowired
    private UserService userService;
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @GetMapping("/users")
    public ResponseEntity<String> getUsers() {
        try {
            List<User> users = userService.findAll();
            return ResponseEntity.ok(renderUsers(users));
        } catch (ServiceException e) {
            return handleError(e, true);
        } catch (Exception e) {
            return handleError(e, false);
        }
    }
    
    private ResponseEntity<String> handleError(Exception e, boolean canRetry) {
        logger.error("Error loading users", e);
        
        String retryMessage = canRetry ? "Retrying automatically..." : "";
        String errorHtml = """
            <div id="global-errors" hx-swap-oob="innerHTML">
                <div class="alert alert-error">
                    Error loading users. %s
                </div>
            </div>
        """.formatted(retryMessage);
        
        return ResponseEntity.status(500).body(errorHtml);
    }
    
    private String renderUsers(List<User> users) {
        // Render logic here
        return "<div>Users...</div>";
    }
}
```

</v-clicks>

---

# Fehler vermeiden durch Validation

<v-clicks>

**Prevention ist besser als Error Handling:**

```html
<form hx-post="/submit">
  <!-- Client-Side Validation -->
  <input name="email" 
         type="email" 
         required
         hx-post="/validate/email"
         hx-trigger="blur"
         hx-target="next .error">
  <span class="error"></span>
  
  <button type="submit">Submit</button>
</form>
```

**Server validiert trotzdem:**
```java
@PostMapping("/submit")
public ResponseEntity<String> submit(@Valid @RequestBody FormData data) {
    // Spring Validation wirft bei Fehler
    return ResponseEntity.ok("Success");
}
```

✅ Verhindert viele 400 Bad Request Errors

</v-clicks>

---

# Testing Error Scenarios

<v-clicks>

**Server-Side Test Endpoints:**

```java
@GetMapping("/test/error")
public ResponseEntity<String> testError(
        @RequestParam(defaultValue = "500") int status) {
    if (status >= 400) {
        return ResponseEntity.status(status)
            .body("<div class='error'>Test Error " + status + "</div>");
    }
    return ResponseEntity.ok("OK");
}
```

**Im Browser testen:**
```javascript
// Console
htmx.ajax('GET', '/test/error?status=500', '#result')
htmx.ajax('GET', '/test/error?status=404', '#result')
```

**Network Throttling in DevTools:**
- Slow 3G simulieren
- Offline Mode testen

</v-clicks>

---

# Zusammenfassung

<v-clicks>

**Error Handling Essentials:**

1. ✅ **Global Error Handler** - Minimum für Production
2. ✅ **User Feedback** - Immer sichtbar machen
3. ✅ **Server-Side HTML** - Auch für Errors rendern
4. ✅ **OOB Swaps** - Konsistente Error-Anzeige
5. ✅ **Retry Pattern** - Nur bei 5xx
6. ✅ **Wenig JavaScript** - HTMX kann viel ohne JS
7. ✅ **Logging** - Errors auf Server tracken
8. ✅ **Testing** - Error-Szenarien testen

**Remember:** Errors passieren - gute UX ist, wie du damit umgehst! 🎯

</v-clicks>

---

# Übung 6: Error Handling

**Implementiere robustes Error Handling:**

<v-clicks>

**Requirements:**
1. Global Error Notification Container
2. Error Handler für 500er Status Codes
3. Network Error Handler (offline)
4. Auto-Retry bei Server Errors (max 3x)
5. Loading State während Retry
6. Benutzerfreundliche Error Messages

</v-clicks>
