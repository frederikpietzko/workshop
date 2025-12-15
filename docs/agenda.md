# HTMX Workshop Agenda

## Tag 1: HTMX Deep Dive

### 09:00 - 09:30 | Einführung
- Begrüßung & Vorstellungsrunde
- Was ist HTMX?
- Warum HTMX?
- Workshop-Überblick

### 09:30 - 10:30 | Core Concepts Deep Dive
- Basic Attributes (`hx-get`, `hx-post`, `hx-target`, `hx-swap`)
- HTMX Request Lifecycle
- Request/Response Headers
- Spring MVC Fragment Rendering Patterns

### 10:30 - 10:45 | ☕ Kaffeepause

### 10:45 - 11:00 | 💻 Übung 1: Hello HTMX (Thymeleaf)
- Einfacher Button der Content lädt
- Verschiedene Swap-Strategien ausprobieren
- Unterschiedliche Elemente targeten

### 11:00 - 11:45 | Advanced Attributes & Patterns
- Triggers (`hx-trigger`: Events, Filters, Modifiers)
- Custom Request Parameters (`hx-params`, `hx-vals`, `hx-headers`)
- Indicators & Loading States (`hx-indicator`, CSS Classes)
- Confirmation Dialogs (`hx-confirm`)

### 11:45 - 12:30 | 💻 Übung 2: Interactive Task List (Thymeleaf)
- Tasks hinzufügen/löschen mit HTMX
- Inline-Editing mit `hx-put`
- Tasks als komplett markieren mit optimistic updates
- Loading-Indikatoren

### 12:30 - 13:30 | 🍽️ Mittagspause

### 13:30 - 14:15 | Forms & Validation
- Formular-Submission mit `hx-post`
- Validation Patterns & Error Display
- `hx-encoding` (multipart/form-data)
- Field-Level Validation
- CSRF Handling in Spring MVC

### 14:15 - 14:45 | 💻 Übung 3: Contact Form (Thymeleaf)
- Formular ohne Page-Reload absenden
- Server-Side Validation
- Fehler pro Feld anzeigen
- Success Feedback

### 14:45 - 15:00 | ☕ Kaffeepause

### 15:00 - 15:30 | State Management & Navigation
- URLs & History API (`hx-push-url`, `hx-replace-url`)
- Boosting (`hx-boost`)
- Redirects
- Session Handling Patterns

### 15:30 - 16:00 | 💻 Übung 4: Multi-Step Form (JTE)
- Progressive Form Wizard
- URL-basierte Navigation zwischen Steps
- Validation mit Server Responses
- JTE Syntax Demo & Vergleich zu Thymeleaf

### 16:00 - 16:30 | HTMX Events & Advanced Patterns
- Lifecycle Events (`htmx:beforeRequest`, `htmx:afterSwap`, etc.)
- Custom Events & `hx-trigger` from Events
- Out-of-Band Swaps (`hx-swap-oob`) für Multiple Updates
- Web Component Integration

### 16:30 - 16:50 | 💻 Übung 5: Live Search (Thymeleaf)
- Search Input mit Debouncing
- Result Count Update (Out-of-Band)
- Custom Event bei Selection

### 16:50 - 17:00 | Recap Tag 1 & Q&A
- Zusammenfassung der Konzepte
- Offene Fragen klären
- Kotlinx HTML als Alternative (Quick Example)

---

## Tag 2: Advanced Patterns & Real-World Problems

### 09:00 - 09:30 | Error Handling & Security
- HTTP Error Codes & Custom Responses
- Error Feedback Patterns
- CSRF Tokens in Spring MVC
- Input Sanitization
- Authentication Patterns

### 09:30 - 10:00 | 💻 Übung 6: Error Handling (Thymeleaf)
- 4xx/5xx Error Displays
- Retry Logic mit Custom Events
- Timeout Handling

### 10:00 - 10:15 | ☕ Kaffeepause

### 10:15 - 10:45 | Real-Time Updates
- Polling (`hx-trigger="every 2s"`)
- Server-Sent Events (SSE)
- WebSocket Integration

### 10:45 - 11:15 | 💻 Übung 7: Live Notifications (Thymeleaf)
- SSE Notification Feed
- Polling Fallback
- Mark as Read

### 11:15 - 11:45 | Advanced UI Patterns
- Lazy Loading & Infinite Scroll
- Modals & Overlays (HTML5 `<dialog>`)
- Optimistic UI Patterns

### 11:45 - 12:30 | 💻 Übung 8: Infinite Scroll Feed (Thymeleaf)
- Load More on Scroll
- Intersection Observer Integration
- Loading Skeleton

### 12:30 - 13:30 | 🍽️ Mittagspause

### 13:30 - 14:00 | Data Tables & Performance
- Server-Side Sorting, Filtering, Pagination
- Request Deduplication
- Caching Strategien
- Debouncing & Throttling

### 14:00 - 14:45 | 💻 Übung 9: Advanced Data Table (Thymeleaf)
- Server-Side Sorting & Filtering
- Pagination mit `hx-push-url`
- Inline Editing
- Search mit Debouncing

### 14:45 - 15:00 | Debugging & Best Practices
- Browser DevTools für HTMX
- `htmx.logAll()`
- Common Pitfalls
- Project Organization Patterns

### 15:00 - 15:15 | ☕ Kaffeepause

### 15:15 - 17:00 | 🔧 Open Workshop: Bring Your Own Problems
- Teilnehmer arbeiten an ihren echten Herausforderungen
- Pair Debugging und Code Review
- Architektur-Diskussionen
- Performance Optimization
- Q&A Session

---

## Übersicht Übungen

| # | Titel | Tech | Dauer | Thema |
|---|-------|------|-------|-------|
| 1 | Hello HTMX | Thymeleaf | 15min | Basics |
| 2 | Interactive Task List | Thymeleaf | 45min | CRUD Operations |
| 3 | Contact Form | Thymeleaf | 30min | Forms & Validation |
| 4 | Multi-Step Form | JTE | 30min | State Management |
| 5 | Live Search | Thymeleaf | 20min | Events & OOB Swaps |
| 6 | Error Handling | Thymeleaf | 30min | Error Patterns |
| 7 | Live Notifications | Thymeleaf | 30min | SSE/Polling |
| 8 | Infinite Scroll | Thymeleaf | 45min | Lazy Loading |
| 9 | Advanced Data Table | Thymeleaf | 45min | Performance |

**Gesamt:** 9 praktische Übungen + Open Workshop

---

## Voraussetzungen

### Software
- JDK 17+
- Maven oder Gradle
- IDE (IntelliJ IDEA, Eclipse, VS Code)
- Browser mit DevTools (Chrome/Firefox/Edge)

### Kenntnisse
- Java & Spring MVC Grundlagen
- HTML & CSS Basics
- HTTP Request/Response Cycle
- Optional: Thymeleaf Erfahrung

---

## Materialien

- Code-Repository mit Starter-Projekten
- Übungsaufgaben mit Lösungen
- Slide-Decks
- HTMX Referenz-Dokumentation
- Best Practices Cheat Sheet
