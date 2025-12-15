# HTMX Workshop - Intro Slides Content

## Slide 1: Title
# HTMX Workshop
Spring MVC + Thymeleaf + HTMX

2 Tage Deep Dive

---

## Slide 2: Was ist HTMX?

> **"high power tools for HTML"**

Eine JavaScript-Bibliothek (14kB), die HTML mit AJAX-Fähigkeiten erweitert

```html
<button hx-get="/api/data" hx-target="#result">
  Load Data
</button>

<div id="result"></div>
```

---

## Slide 3: Warum HTMX?

- **Einfachheit**: HTML schreiben statt JavaScript
- **Server-Side Rendering**: Volle Kontrolle über HTML-Generierung
- **Weniger JavaScript**: Kleinerer Bundle, weniger Komplexität
- **Progressive Enhancement**: Funktioniert mit bestehenden Frameworks
- **Hypermedia-Driven**: REST-Prinzipien konsequent umgesetzt

**Perfekt für:** Spring MVC, Thymeleaf, klassische Web-Apps die interaktiv werden sollen

---

## Slide 4: Der "Alte" Weg

```html
<form action="/contact" method="POST">
  <input name="email" type="email" />
  <button type="submit">Subscribe</button>
</form>
```

**Problem:** Komplette Seite wird neu geladen ↻

**SPA-Lösung:** React/Vue + REST API + JSON + Client-Side Rendering

```jsx
const [email, setEmail] = useState('')

const handleSubmit = async (e) => {
  e.preventDefault()
  const response = await fetch('/api/contact', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email })
  })
  const data = await response.json()
  // Update DOM manually
}
```

---

## Slide 5: Der HTMX Weg

```html
<form hx-post="/contact" hx-target="#message">
  <input name="email" type="email" />
  <button type="submit">Subscribe</button>
</form>

<div id="message"></div>
```

**Server antwortet mit HTML:**

```html
<div class="success">
  ✓ Vielen Dank! Sie haben sich erfolgreich angemeldet.
</div>
```

**Kein JavaScript nötig!**

---

## Slide 6: Vergleich

### Traditionell (SPA)
- Client empfängt JSON
- JavaScript manipuliert DOM
- State Management nötig
- Bundle Size: 100-500KB+
- 2 Sprachen: JS + Template

### HTMX
- Client empfängt HTML
- Browser ersetzt HTML
- Server verwaltet State
- Bundle Size: 14KB
- 1 Sprache: Server-Template

**HTMX = Hypermedia as the Engine of Application State**
