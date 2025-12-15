# HTMX Workshop Curriculum (2 Days)

## Day 1: HTMX Deep Dive (09:00-17:00)

### Morning Session (09:00-12:30)
- **09:00-09:30** Short Intro + What is HTMX? + Why HTMX?
- **09:30-10:30** Core Concepts Deep Dive
  - Basic Attributes (`hx-get`, `hx-post`, `hx-target`, `hx-swap`)
  - HTMX Request Lifecycle
  - Request/Response Headers
  - Spring MVC fragment rendering patterns
- **10:30-10:45** ☕ Break
- **10:45-11:00** **Exercise 1: Hello HTMX (Thymeleaf)**
  - Simple button that loads content
  - Different swap strategies
  - Target different elements
- **11:00-11:45** Advanced Attributes & Patterns
  - Triggers (`hx-trigger`: events, filters, modifiers)
  - Custom Request Parameters (`hx-params`, `hx-vals`, `hx-headers`)
  - Indicators & Loading States (`hx-indicator`, CSS classes)
  - Confirmation dialogs (`hx-confirm`)
- **11:45-12:30** **Exercise 2: Interactive Task List (Thymeleaf)**
  - Add/delete tasks with htmx
  - Inline editing with `hx-put`
  - Mark complete with optimistic updates
  - Loading indicators

### Lunch (12:30-13:30)

### Afternoon Session (13:30-17:00)
- **13:30-14:15** Forms & Validation
  - Form submission with `hx-post`
  - Validation patterns & error display
  - `hx-encoding` (multipart/form-data)
  - Field-level validation
  - CSRF handling in Spring MVC
- **14:15-14:45** **Exercise 3: Contact Form (Thymeleaf)**
  - Form submission without page reload
  - Server-side validation
  - Error messages per field
  - Success feedback
- **14:45-15:00** ☕ Break
- **15:00-15:30** State Management & Navigation
  - URLs & History API (`hx-push-url`, `hx-replace-url`)
  - Boosting (`hx-boost`)
  - Redirects
  - Session handling patterns
- **15:30-16:00** **Exercise 4: Multi-Step Form (JTE)**
  - Progressive form wizard
  - URL-based navigation between steps
  - Validation with server responses
  - *Demo JTE syntax & compare to Thymeleaf*
- **16:00-16:30** HTMX Events & Advanced Patterns
  - Lifecycle Events (`htmx:beforeRequest`, `htmx:afterSwap`, etc.)
  - Custom Events & `hx-trigger` from events
  - Out-of-band swaps (`hx-swap-oob`) for multiple updates
  - Web Component integration
- **16:30-16:50** **Exercise 5: Live Search (Thymeleaf)**
  - Search input with debouncing
  - Result count update (out-of-band)
  - Custom event on selection
- **16:50-17:00** Day 1 Recap + Q&A
  - *Mention: Kotlinx HTML as alternative (show quick example)*

---

## Day 2: Advanced Patterns & Real-World Problems (09:00-17:00)

### Morning Session (09:00-12:30)
- **09:00-09:30** Error Handling & Security
  - HTTP error codes & custom responses
  - Error feedback patterns
  - CSRF tokens in Spring MVC
  - Input sanitization
  - Authentication patterns
- **09:30-10:00** **Exercise 6: Error Handling (Thymeleaf)**
  - 4xx/5xx error displays
  - Retry logic with custom events
  - Timeout handling
- **10:00-10:15** ☕ Break
- **10:15-10:45** Real-Time Updates
  - Polling (`hx-trigger="every 2s"`)
  - Server-Sent Events (SSE)
  - WebSocket integration
- **10:45-11:15** **Exercise 7: Live Notifications (Thymeleaf)**
  - SSE notification feed
  - Polling fallback
  - Mark as read
- **11:15-11:45** Advanced UI Patterns
  - Lazy Loading & Infinite Scroll
  - Modals & Overlays (HTML5 `<dialog>`)
  - Optimistic UI patterns
- **11:45-12:30** **Exercise 8: Infinite Scroll Feed (Thymeleaf)**
  - Load more on scroll
  - Intersection observer integration
  - Loading skeleton

### Lunch (12:30-13:30)

### Afternoon Session (13:30-17:00)
- **13:30-14:00** Data Tables & Performance
  - Server-side sorting, filtering, pagination
  - Request deduplication
  - Caching strategies
  - Debouncing & throttling
- **14:00-14:45** **Exercise 9: Advanced Data Table (Thymeleaf)**
  - Server-side sorting & filtering
  - Pagination with `hx-push-url`
  - Inline editing
  - Search with debouncing
- **14:45-15:00** Debugging & Best Practices
  - Browser DevTools for HTMX
  - `htmx.logAll()`
  - Common pitfalls
  - Project organization patterns
- **15:00-15:15** ☕ Break
- **15:15-17:00** **Open Workshop: Bring Your Own Problems**
  - Participants work on their real-world challenges
  - Pair debugging and code review
  - Architecture discussions
  - Performance optimization

---

## Exercise Ideas (3 Options)

### Option 1: E-Commerce Product Catalog
**Tech:** Thymeleaf
**Features:**
- Filterable product grid (category, price range)
- Add to cart without page reload
- Cart preview with live updates
- Lazy-loaded product images
- Optimistic UI for cart actions

### Option 2: Customer Support Ticket System
**Tech:** Thymeleaf (main), JTE (ticket detail view)
**Features:**
- Ticket list with status filters
- Real-time status updates (SSE)
- Inline comment thread
- File upload with progress indicators
- Assignee dropdown with search

### Option 3: Project Management Kanban Board
**Tech:** Thymeleaf
**Features:**
- Drag-and-drop between columns (htmx + sortable.js)
- Inline task creation
- Task detail modal (HTML5 `<dialog>`)
- Activity feed with polling
- Archive/restore with animations