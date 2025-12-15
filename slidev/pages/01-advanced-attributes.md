# HTMX Trigger

<v-clicks>

**Grundlagen:**
- `hx-trigger` bestimmt welches Event einen HTMX Request auslöst
- Default Trigger abhängig vom Element:
  - `<button>`, `<a>`: `click`
  - `<input>`, `<select>`, `<textarea>`: `change`
  - `<form>`: `submit`

- Grunsätzlich kann jedes Event als Trigger genutzt werden

**Beispiele:**

```html
<button hx-get="/data" hx-trigger="click">Click Me</button>
<input hx-get="/search" hx-trigger="keyup">
<div hx-get="/news" hx-trigger="load">Lädt automatisch</div>
```

</v-clicks>

---

# Trigger Events

<v-clicks>

**Standard Browser Events:**
- `click`, `dblclick`, `mouseenter`, `mouseleave`
- `keyup`, `keydown`, `change`, `input`
- `submit`, `focus`, `blur`
- `load` - Wird beim Laden des Elements getriggert
- `revealed` - Wenn Element in Viewport scrollt

**Custom Events:**
- `myEvent` - Beliebige Custom Events
- Von JavaScript oder HTMX Response Header getriggert

```html
<div hx-get="/data" hx-trigger="myEvent">...</div>
<script>
  document.querySelector('div').dispatchEvent(new Event('myEvent'));
</script>
```

</v-clicks>

---

# Trigger Modifier

<v-clicks>

- `once` - Nur einmal ausführen
- `changed` - Nur wenn sich der Wert geändert hat
- `delay:<time>` - Verzögerung vor Request (Debounce)
- `throttle:<time>` - Maximal alle X Sekunden
- `from:<selector>` - Event von anderem Element
- `target:<selector>` - Nur wenn Event-Target matched
- `consume` - Event nicht weiterpropagieren
- `queue:<strategy>` - Request Queue Management (`first`, `last`, `all`, `none`)

```html
<input hx-get="/search" 
       hx-trigger="keyup changed delay:500ms" 
       hx-target="#results">
```

</v-clicks>

---

# Trigger Filter & Bedingungen

<v-clicks>

**Event Filter:**

```html
<!-- Nur bei Enter-Taste -->
<input hx-get="/search" hx-trigger="keyup[keyCode==13]">

<!-- Nur bei Ctrl+Enter -->
<textarea hx-post="/comment" 
          hx-trigger="keyup[ctrlKey && keyCode==13]">
</textarea>

<!-- Mehrere Bedingungen -->
<div hx-get="/data" 
     hx-trigger="click[event.shiftKey], 
                 mouseenter[event.altKey]">
</div>
```

**Zugriff auf Element-Properties:**
```html
<input hx-get="/validate" 
       hx-trigger="change[this.value.length > 3]">
```

</v-clicks>

---

# Trigger Beispiele

<v-clicks>

**Live Search mit Debouncing:**
```html
<input type="search" 
       hx-get="/search" 
       hx-trigger="keyup changed delay:300ms"
       hx-target="#results">
```

**Polling (Auto-Refresh):**
```html
<div hx-get="/status" 
     hx-trigger="every 2s"
     hx-swap="innerHTML">
  Loading...
</div>
```

**Komplexes Szenario:**
```html
<button hx-post="/action"
        hx-trigger="click throttle:1s, 
                    customEvent from:body">
  Submit
</button>
```

</v-clicks>

---

# Custom Request Parameter

<v-clicks>

HTMX bietet mehrere Möglichkeiten, Request-Parameter zu steuern:

- `hx-params` - Welche Parameter werden mitgeschickt
- `hx-vals` - Statische oder dynamische zusätzliche Werte
- `hx-include` - Werte von anderen Elementen einbeziehen

Diese Attribute geben dir volle Kontrolle über die Request-Payload.

</v-clicks>

---

# hx-params

<v-clicks>

Steuert **welche** Input-Parameter mitgeschickt werden:

```html
<!-- Alle Parameter senden (default) -->
<form hx-post="/submit" hx-params="*">
  <input name="username">
  <input name="password">
</form>

<!-- Keine Parameter senden -->
<button hx-get="/action" hx-params="none">Click</button>

<!-- Nur bestimmte Parameter -->
<form hx-post="/submit" hx-params="username,email">
  <input name="username">
  <input name="email">
  <input name="internal"> <!-- wird nicht gesendet -->
</form>

<!-- Alle außer bestimmte -->
<form hx-post="/submit" hx-params="not internal">
```

</v-clicks>

---

# hx-vals

<v-clicks>

Fügt **zusätzliche** statische oder dynamische Werte hinzu:

**Statische Werte (JSON):**
```html
<button hx-post="/action" 
        hx-vals='{"category": "news", "priority": 1}'>
  Submit
</button>
```

**Dynamische Werte (JavaScript):**
```html
<button hx-post="/action"
        hx-vals='js:{timestamp: Date.now(), random: Math.random()}'>
  Submit
</button>
```

</v-clicks>

---
title: hx-vals 2
---

# hx-vals

**Kombiniert mit Form-Daten:**
```html
<form hx-post="/submit" 
      hx-vals='{"source": "web", "version": "2.0"}'>
  <input name="email">
  <!-- Sendet: email + source + version -->
</form>
```

---

# hx-include

<v-clicks>

Bezieht Werte von **anderen Elementen** mit ein:

```html
<input id="filter" name="filter" value="active">
<select id="sort" name="sort">
  <option value="date">Date</option>
</select>

<!-- Button sendet seine eigenen + die anderen Werte -->
<button hx-get="/search" 
        hx-include="#filter, #sort">
  Search
</button>
```

</v-clicks>

---
title: hx-include 2
---

# hx-include

<v-clicks>

**CSS Selektoren verwenden:**
```html
<!-- Alle Inputs in einem bestimmten Container -->
<button hx-post="/action" 
        hx-include=".search-filters input">
  Apply Filters
</button>

<!-- Closest Form finden -->
<button hx-post="/action" 
        hx-include="closest form">
  Submit
</button>
```

</v-clicks>

---

# Kombinierte Beispiele

<v-clicks>

**Komplexer Filter mit mehreren Quellen:**
```html
<input id="search" name="query">
<select id="category" name="category">...</select>

<button hx-get="/results"
        hx-include="#search, #category"
        hx-vals='js:{timestamp: Date.now()}'>
  Search
</button>
<!-- Sendet: query + category + timestamp -->
```

</v-clicks>

---
title: Kombinierte Beispiele 2
---

# Kombinierte Beispiele

<v-clicks>

**Form mit zusätzlichen Meta-Daten:**
```html
<form hx-post="/order" 
      hx-vals='{"source": "mobile", "campaign": "summer2024"}'
      hx-params="not internal-debug">
  <input name="product">
  <input name="quantity">
  <input name="internal-debug">
  <button type="submit">Order</button>
</form>
<!-- Sendet: product + quantity + source + campaign -->
<!-- internal-debug wird ausgeschlossen -->
```

</v-clicks>

--- 

# Loading States & Ladeindikatoren

<v-clicks>

HTMX fügt automatisch CSS-Klassen während Request-Lifecycle:

- **`htmx-request`** - Auf dem Element während Request
- **`htmx-swapping`** - Während des Swapping
- **`htmx-settling`** - Während des Settling

**Manuell mit hx-indicator:**
- Zeigt bestimmte Elemente während Requests an
- Nutzt `.htmx-request` Klasse auf Indicator

</v-clicks>

---

# Automatische Loading Classes

<v-clicks>

```html
<style>
  .htmx-request {
    opacity: 0.5;
    pointer-events: none;
  }
  .htmx-request::after {
    content: " ⏳";
  }
</style>

<button hx-get="/data" hx-target="#result">
  Load Data
</button>
```

**Während Request:** Button wird transparent und zeigt ⏳

```css
/* Für das Ziel-Element */
#result.htmx-swapping {
  opacity: 0;
  transition: opacity 200ms;
}
```

</v-clicks>

---

# hx-indicator

<v-clicks>

Zeigt ein **spezifisches Element** als Loading Indicator:

```html
<button hx-get="/search" 
        hx-indicator="#spinner">
  Search
</button>

<div id="spinner" class="htmx-indicator">
  <img src="spinner.gif" />
</div>
```

```css
.htmx-indicator {
  display: none;
}

.htmx-request .htmx-indicator,
.htmx-request.htmx-indicator {
  display: inline-block;
}
```

**Result:** Spinner wird nur während Request sichtbar

</v-clicks>

---

# Inline Loading States

<v-clicks>

**Direkt im Element:**

```html
<button hx-get="/save">
  <span class="button-text">Save</span>
  <span class="htmx-indicator">
    ⏳ Saving...
  </span>
</button>
```

</v-clicks>

---
title: Inline Loading States 2
---

# Inline Loading States

<v-clicks>

**Mit Disabled State:**
```html
<style>
  button.htmx-request {
    pointer-events: none;
  }
  button .button-text { display: inline; }
  button.htmx-request .button-text { display: none; }
</style>

<button hx-post="/submit">
  <span class="button-text">Submit</span>
  <span class="htmx-indicator">⏳ Processing...</span>
</button>
```

</v-clicks>

---

# Progress Indicators & Skeleton Loading

<v-clicks>

**Skeleton Screen während Loading:**

```html
<div hx-get="/profile" hx-trigger="load">
  <div class="skeleton">
    <div class="skeleton-avatar"></div>
    <div class="skeleton-text"></div>
  </div>
</div>
```

</v-clicks>

---
title: Progress Indicators & Skeleton Loading 2
---

# Progress Indicators & Skeleton Loading

<v-clicks>

**Custom Progress Bar:**
```html
<div id="progress" class="htmx-indicator">
  <div class="progress-bar"></div>
</div>

<button hx-post="/upload" 
        hx-indicator="#progress">
  Upload
</button>
```

```css
.progress-bar {
  animation: progress 2s linear;
}

@keyframes progress {
  from { width: 0%; }
  to { width: 100%; }
}
```

</v-clicks>

---

# Erweiterte Patterns

<v-clicks>

**Globaler Indicator für alle Requests:**

```html
<div id="global-spinner" class="htmx-indicator">
  Loading...
</div>

<script>
  document.body.addEventListener('htmx:beforeRequest', () => {
    document.getElementById('global-spinner').classList.add('htmx-request');
  });
  
  document.body.addEventListener('htmx:afterRequest', () => {
    document.getElementById('global-spinner').classList.remove('htmx-request');
  });
</script>
```

</v-clicks>

---
title: Erweitere Patterns 2
---

# Erweiterte Patterns

<v-clicks>

**Timeout Warning:**
```html
<button hx-get="/slow-api" class="with-timeout">
  Load Data
</button>

<script>
  let timeout;
  document.body.addEventListener('htmx:beforeRequest', (e) => {
    timeout = setTimeout(() => {
      alert('Request is taking longer than expected...');
    }, 3000);
  });
  
  document.body.addEventListener('htmx:afterRequest', () => {
    clearTimeout(timeout);
  });
</script>
```

</v-clicks>

---

# Confirmation Dialoge
<v-clicks>

HTMX bietet mehrere Wege für Bestätigungsdialoge:

- **`hx-confirm`** - Einfacher Browser-Confirm Dialog
- **Custom Dialogs** - Mit Events und eigenem UI
- **`hx-prompt`** - Nutzer-Input vor Request abfragen

Verhindert versehentliche gefährliche Aktionen (Delete, Submit, etc.)

</v-clicks>

---

# hx-confirm

<v-clicks>

Zeigt nativen Browser Confirm Dialog:

```html
<button hx-delete="/user/123"
        hx-confirm="Wirklich löschen?">
  Delete User
</button>
```

**Mit mehrzeiligem Text:**
```html
<button hx-post="/send-email"
        hx-confirm="Email an 1000 Empfänger senden?
                    
Diese Aktion kann nicht rückgängig gemacht werden.">
  Send Bulk Email
</button>
```

**Dynamischer Text mit JavaScript:**
```html
<button hx-delete="/item/123"
        hx-confirm="js:confirm('Löschen: ' + this.dataset.itemName + '?')">
  Delete
</button>
```

</v-clicks>

---

# hx-prompt

<v-clicks>

Fordert User-Input vor Request an:

```html
<button hx-delete="/account"
        hx-prompt="Geben Sie 'DELETE' ein um zu bestätigen:">
  Delete Account
</button>
```

**Input wird als Parameter gesendet:**
```html
<button hx-post="/rename"
        hx-prompt="Neuer Name:"
        hx-include="#item-id">
  Rename
</button>
<!-- Server erhält: prompt="neuer-wert" -->
```

</v-clicks>

---
title: hx-prompt-2
---

# hx-prompt

<v-clicks>

**Validierung auf Server-Seite:**
```java
@PostMapping("/delete")
public String delete(@RequestParam String prompt) {
    if (prompt != "DELETE") {
        return "<div class='error'>Falsche Eingabe</div>";
    }
    // ... löschen
}
```

</v-clicks>

---

# Custom Confirmation Dialogs

<v-clicks>

**Mit htmx:confirm Event:**

```html
<button hx-delete="/user/123" 
        class="needs-confirm">
  Delete User
</button>

<script>
document.body.addEventListener('htmx:confirm', function(e) {
  if (e.target.classList.contains('needs-confirm')) {
    e.preventDefault();
    if (confirm('Wirklich löschen? Dies kann nicht rückgängig gemacht werden!')) {
      e.detail.issueRequest();
    }
  }
});
</script>
```

</v-clicks>

---

# Custom Dialog mit Modal


```html
<div id="confirm-modal" class="modal">
  <div class="modal-content">
    <h2 id="confirm-title"></h2>
    <p id="confirm-message"></p>
    <button id="confirm-yes">Ja</button>
    <button id="confirm-no">Nein</button>
  </div>
</div>
<button hx-delete="/user/123"
  data-confirm-title="Benutzer löschen"
  data-confirm-message="Wirklich löschen?">
  Delete
</button>
```

---

# Custom Dialog mit Modal

```js

let pendingRequest = null;

document.body.addEventListener('htmx:confirm', function(e) {
  e.preventDefault();
  pendingRequest = e.detail.issueRequest;
  document.getElementById('confirm-title').textContent = e.target.dataset.confirmTitle;
  document.getElementById('confirm-message').textContent = e.target.dataset.confirmMessage;
  document.getElementById('confirm-modal').style.display = 'block';
});

document.getElementById('confirm-yes').addEventListener('click', () => {
  pendingRequest(true);
  document.getElementById('confirm-modal').style.display = 'none';
});

document.getElementById('confirm-no').addEventListener('click', () => {
  document.getElementById('confirm-modal').style.display = 'none';
});
```



---

# Patterns & Best Practices

<v-clicks>

**Unterschiedliche Aktionen, unterschiedliche Dialoge:**

```html
<button hx-delete="/item" 
        hx-confirm="Löschen?">
  Delete Item
</button>

<button hx-delete="/item?permanent=true"
        hx-confirm="PERMANENT löschen? Keine Wiederherstellung möglich!">
  Delete Permanently
</button>
```

</v-clicks>

---
title: Patterns & Best Practices 2
---

# Patterns & Best Practices

<v-clicks>

**Bestätigung nur bei bestimmten Bedingungen:**
```html
<button hx-post="/publish"
        hx-confirm="js:document.getElementById('draft').checked ? 
                        'Als Draft speichern?' : 'Jetzt publizieren?'">
  Publish
</button>
```

**Server-seitige Validierung immer notwendig:**
- Client-Bestätigung ist UX, keine Security
- Server muss Berechtigung prüfen
- Double-Submit Prevention auf Server implementieren

</v-clicks>

---

# Übung 2: Interaktive Todo Liste

- Dynamisch neues todo item hinzufuegen
- (optional) gleichzeitig das input clearen
- Löschen von einem items
- Abhaken eines items
- (optional) inline edits von todo item text
