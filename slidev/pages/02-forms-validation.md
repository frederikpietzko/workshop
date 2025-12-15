# Forms & Validation - Überblick

<v-clicks>

**Was wir behandeln:**
- Form Submission mit HTMX
- Server-Side Validation
- Field-Level Validation
- Inline/Live Validation
- Error Display Patterns
- Success Feedback

**Warum Server-Side Validation?**
- Security: Client-Side ist nicht vertrauenswürdig
- Business Logic auf Server
- Single Source of Truth

</v-clicks>

---

# Form Submission Basics

<v-clicks>

**Traditionelle Form:**
```html
<form action="/register" method="POST">
  <input name="email" type="email">
  <button type="submit">Register</button>
</form>
<!-- → Page Reload -->
```

**Mit HTMX:**
```html
<form hx-post="/register" hx-target="#result">
  <input name="email" type="email">
  <button type="submit">Register</button>
</form>
<div id="result"></div>
<!-- → AJAX Request, kein Page Reload -->
```

- Form-Daten werden automatisch serialisiert
- Default Trigger: `submit`
- `hx-post` auf Form oder Button möglich

</v-clicks>

---

# Server-Side Validation

<v-clicks>

**Server validiert und gibt HTML zurück:**

```java
@PostMapping("/register")
public String register(@RequestParam String email) {
    if (!isValidEmail(email)) {
        return """
            <div class="error">
                Ungültige E-Mail-Adresse
            </div>
        """;
    }
    
    // ... speichern
    return """
        <div class="success">
            Erfolgreich registriert!
        </div>
    """;
}
```

</v-clicks>

---
title: Server-Side Validation
---

# Server-Side Validation

<v-clicks>

**Client erhält fertiges HTML:**
```html
<form hx-post="/register" hx-target="#feedback">
  <input name="email">
  <button type="submit">Register</button>
</form>
<div id="feedback"><!-- Error/Success hier --></div>
```

</v-clicks>

---

# Error Display Patterns

<v-clicks>

**Pattern 1: Error Summary oben**
```html
<div id="errors"></div>
<form hx-post="/submit" hx-target="#errors">
  <input name="email">
  <input name="password">
  <button type="submit">Submit</button>
</form>
```

</v-clicks>

---
title: Error Display Patterns 2
--- 

# Error Display Patterns

<v-clicks>

**Pattern 2: Inline Field Errors**
```html
<form hx-post="/submit" hx-swap="outerHTML">
  <div>
    <input name="email">
    <span class="error">Ungültige E-Mail</span>
  </div>
  <div>
    <input name="password">
    <span class="error">Zu kurz</span>
  </div>
</form>
```

</v-clicks>

---
title: Error Display Patterns 3
--- 

# Error Display Patterns

<v-clicks>

**Pattern 3: OOB Swaps für beide**
```html
<!-- Server Response: -->
<form id="register-form" hx-swap-oob="true">...</form>
<div id="summary-errors" hx-swap-oob="true">
  2 Fehler gefunden
</div>
```

</v-clicks>

---

# Field-Level Validation

<v-clicks>

**Einzelnes Feld validieren:**

```html
<form>
  <div>
    <input name="username" 
           hx-post="/validate/username"
           hx-trigger="blur"
           hx-target="next .error">
    <span class="error"></span>
  </div>
  
  <div>
    <input name="email"
           hx-post="/validate/email"
           hx-trigger="blur"
           hx-target="next .error">
    <span class="error"></span>
  </div>
  
  <button type="submit">Register</button>
</form>
```

</v-clicks>

---
title: Field-Level Validation 2
---

# Field-Level Validation

<v-clicks>

**Server Endpoint:**
```java
@PostMapping("/validate/username")
public String validateUsername(@RequestParam String username) {
    if (username.length() < 3) {
        return "<span class='error'>Mind. 3 Zeichen</span>";
    } else {
        return "<span class='success'>✓</span>";
    }
}
```

</v-clicks>

---

# Live/Inline Validation

<v-clicks>

**Validation während Typing:**

```html
<input name="password"
       hx-post="/validate/password"
       hx-trigger="keyup changed delay:500ms"
       hx-target="next .feedback">
<div class="feedback"></div>
```

</v-clicks>

---
title: Live/Inline Validation 2
---

# Live/Inline Validation

<v-clicks>

**Progressive Feedback:**
```java
@PostMapping("/validate/password")
public String validatePassword(@RequestParam String password) {
    String strength;
    if (password.length() < 6) {
        strength = "Zu schwach";
    } else if (password.length() < 10) {
        strength = "Mittel";
    } else {
        strength = "Stark ✓";
    }
    
    return """
        <div class="password-strength %s">
            %s
        </div>
    """.formatted(strength, strength);
}
```

**Best Practice:** Debouncing mit `delay:` nutzen!

</v-clicks>

---

# Success Feedback & Form Reset

<v-clicks>

**Success mit OOB Swap:**

```html
<form id="contact-form" hx-post="/contact">
  <input name="email">
  <button type="submit">Send</button>
</form>
<div id="notification"></div>
```

**Server Response:**
```html
<!-- Leeres Form zurück -->
<form id="contact-form" hx-swap-oob="true">
  <input name="email" value="">
  <button type="submit">Send</button>
</form>

<!-- Success Notification -->
<div id="notification" hx-swap-oob="true" class="success">
  ✓ Nachricht gesendet!
</div>
```

</v-clicks>

---
title: Success Feedback & Form Reset 2
---

# Success Feedback & Form Reset

<v-clicks>

**Oder mit JavaScript:**
```html
<form hx-post="/contact" 
      hx-on::after-request="if(event.detail.successful) this.reset()">
```

</v-clicks>

---

# Validation Best Practices

<v-clicks>

**Progressive Enhancement:**
```html
<form action="/register" method="POST" 
      hx-post="/register" hx-target="#result">
  <input name="email" type="email" required>
  <button type="submit">Register</button>
</form>
```

- HTML5 Validation als Fallback
- Server validiert IMMER

**Error Prevention:**
- Disable Submit während Request
- Clear Errors bei neuem Input
- Consistent Error Messages

</v-clicks>

---

# Komplettes Beispiel

<v-clicks>

```html
<form id="register" hx-post="/register" hx-swap="outerHTML">
  <div id="errors" class="error-summary"></div>
  <div class="field">
    <label>Username</label>
    <input name="username" 
           hx-post="/validate/username"
           hx-trigger="blur changed"
           hx-target="next .field-error">
    <span class="field-error"></span>
  </div>
  <div class="field">
    <label>Email</label>
    <input name="email" type="email"
           hx-post="/validate/email"
           hx-trigger="blur changed"
           hx-target="next .field-error">
    <span class="field-error"></span>
  </div>
  <button type="submit" hx-indicator="#spinner">
    Register <span id="spinner" class="htmx-indicator">⏳</span>
  </button>
</form>
<div id="success-message" hx-swap-oob="true"></div>
```

</v-clicks>

---

# Übung 3: Registration Form

Erstelle ein Registrierungsformular mit:

- Username & Email Felder
- Field-Level Validation (on blur)
- Server-Side Validation beim Submit
- Inline Error Messages
- Success Message mit Form Reset
- Loading Indicator

**Bonus:**
- Live Password Strength Meter
- Username Availability Check
