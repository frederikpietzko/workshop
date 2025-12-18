# Real-time Updates

---

# Real-time Updates - Überblick

<v-clicks>

**Warum Real-time?**
- Live Notifications
- Chat-Anwendungen
- Live Dashboards / Monitoring
- Collaborative Editing
- Live Scores / Feeds

**Drei Ansätze:**
1. **Polling** - Client fragt regelmäßig nach Updates
2. **Server-Sent Events (SSE)** - Server pushed Updates (one-way)
3. **WebSockets** - Bi-direktionale Kommunikation

**HTMX unterstützt alle drei!** 🎉

</v-clicks>

---

# Vergleich der Technologien

<div class="grid grid-cols-3 gap-4 text-sm">

<div>

**Polling**

<v-clicks>

✅ Einfach  
✅ Keine spezielle Infrastruktur  
✅ Funktioniert überall

❌ Ineffizient  
❌ Verzögerung bis zu Poll-Intervall  
❌ Viele unnötige Requests

**Use Case:** Selten ändernde Daten

</v-clicks>

</div>

<div>

**Server-Sent Events**

<v-clicks>

✅ Effizient (Push)  
✅ Automatisches Reconnect  
✅ Einfacher als WebSockets

❌ Nur Server → Client  
❌ HTTP/1.1 Connection Limits

**Use Case:** Notifications, Live Feeds

</v-clicks>

</div>

<div>

**WebSockets**

<v-clicks>

✅ Bi-direktional  
✅ Sehr effizient  
✅ Low Latency

❌ Komplexer  
❌ Spezielle Infrastruktur  
❌ Load Balancing schwieriger

**Use Case:** Chat, Gaming, Collaborative

</v-clicks>

</div>

</div>

---

# Polling mit HTMX

<v-clicks>

**Basis Polling:**

```html
<div hx-get="/status" 
     hx-trigger="every 2s"
     hx-swap="innerHTML">
  Loading...
</div>
```

- Sendet Request alle 2 Sekunden
- Ersetzt Content mit Response
- Läuft kontinuierlich

**Problem:** Läuft auch wenn keine Updates da sind ⚠️

</v-clicks>

---

# Adaptive Polling

<v-clicks>

**Polling stoppen bei Condition:**

```html
<div id="status" 
     hx-get="/job/status/123"
     hx-trigger="every 2s"
     hx-swap="innerHTML">
  Checking...
</div>
```

**Server Response:**

```java
@GetMapping("/job/status/{id}")
public ResponseEntity<String> getJobStatus(@PathVariable Long id) {
    Job job = jobService.getJob(id);
    
    if (job.isComplete()) {
        // Polling stoppen via Event
        return ResponseEntity.ok()
            .header("HX-Trigger", "jobComplete")
            .body("<div class='success'>Complete!</div>");
    }
    
    int progress = job.getProgress();
    return ResponseEntity.ok(
        "<div>Progress: " + progress + "%</div>"
    );
}
```

</v-clicks>

---

# Polling stoppen

<v-clicks>

**Client-Side:**

```javascript
document.body.addEventListener('jobComplete', function(evt) {
  const statusDiv = document.getElementById('status');
  
  // Polling stoppen
  statusDiv.removeAttribute('hx-trigger');
  
  // Oder: Disable HTMX auf Element
  htmx.trigger(statusDiv, 'htmx:abort');
  
  console.log('Job completed, polling stopped');
});
```

**Alternative: Server steuert Polling:**

```java
// Server Response enthält neues hx-trigger
return ResponseEntity.ok()
    .body("""
        <div id="status" hx-swap-oob="true">
            Complete! No more polling.
        </div>
    """);
```

</v-clicks>

---

# Long Polling

<v-clicks>

**Unterschied zu normalem Polling:**
- Server hält Request offen bis Update verfügbar
- Client startet sofort neuen Request nach Response
- Effizienter als Standard Polling

```html
<div hx-get="/updates" 
     hx-trigger="load"
     hx-swap="innerHTML">
</div>
```

**Server (Spring Boot):**

```java
@GetMapping("/updates")
public DeferredResult<String> getUpdates() {
    DeferredResult<String> result = new DeferredResult<>(30000L);
    
    // Warte auf Update (async)
    updateService.onUpdate(update -> {
        result.setResult("<div>" + update + "</div>");
    });
    
    return result;
}
```

Response enthält `hx-trigger="load"` → Startet sofort nächsten Request

</v-clicks>

---

# Server-Sent Events (SSE)

<v-clicks>

**Was ist SSE?**
- HTTP Streaming Connection
- Server kann mehrere Updates über eine Connection senden
- Browser reconnected automatisch
- Standard Browser API

**HTMX SSE Extension:**

```html
<div hx-ext="sse" 
     sse-connect="/notifications/stream"
     sse-swap="notification">
  Waiting for notifications...
</div>
```

- `sse-connect` - URL zum SSE Endpoint
- `sse-swap` - Event Name aus SSE
- Automatisches Reconnect bei Disconnect

</v-clicks>

---

# SSE Server-Side (Spring Boot)

<v-clicks>

**Server Endpoint mit SseEmitter:**

```java
@GetMapping(path = "/notifications/stream", 
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public SseEmitter streamNotifications() {
    SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
    sseService.addEmitter(emitter);
    
    emitter.onCompletion(() -> sseService.removeEmitter(emitter));
    emitter.onTimeout(() -> sseService.removeEmitter(emitter));
    
    return emitter;
}

// Notification senden
public void sendNotification(Notification notification) {
    String html = """
        <div class="notification">
            <strong>%s</strong>: %s
        </div>
    """.formatted(notification.getTitle(), notification.getMessage());
    
    sseService.broadcast("notification", html);
}
```

</v-clicks>

---

# SSE mit Multiple Events

<v-clicks>

**HTML:**

```html
<div hx-ext="sse" sse-connect="/events/stream">
  <!-- Verschiedene Event Types -->
  <div sse-swap="message" hx-swap="beforeend">
    Messages:
  </div>
  
  <div sse-swap="notification" hx-swap="beforeend">
    Notifications:
  </div>
  
  <div sse-swap="update" hx-swap="beforeend">
    Updates:
  </div>
</div>
```

**Server sendet verschiedene Events:**

```java
public void broadcast(String eventName, String data) {
    emitters.forEach(emitter -> {
        try {
            SseEmitter.SseEventBuilder event = SseEmitter.event()
                .name(eventName)
                .data(data);
            emitter.send(event);
        } catch (IOException e) {
            // Emitter entfernen wenn tot
        }
    });
}
```

</v-clicks>

---

# WebSockets mit HTMX

<v-clicks>

**HTMX WebSocket Extension:**

```html
<div hx-ext="ws" ws-connect="/chat/room/123">
  <div id="messages"></div>
  
  <form ws-send>
    <input name="message" placeholder="Type message...">
    <button type="submit">Send</button>
  </form>
</div>
```

- `ws-connect` - WebSocket URL
- `ws-send` - Form wird als WebSocket Message gesendet
- Response vom Server wird automatisch geswapped

**Extension laden:**

```html
<script src="https://unpkg.com/htmx.org/dist/ext/ws.js"></script>
```

</v-clicks>

---

# WebSocket Handler (Spring Boot)

<v-clicks>

**WebSocket Configuration:**

```java
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketHandler(), "/chat/{roomId}")
            .setAllowedOrigins("*");
    }
    
    @Bean
    public WebSocketHandler chatWebSocketHandler() {
        return new ChatWebSocketHandler();
    }
}
```

</v-clicks>

---

# WebSocket Handler Implementation

<v-clicks>

```java
public class ChatWebSocketHandler extends TextWebSocketHandler {
    
    private final Map<String, Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();
    
    @Override
    protected void handleTextMessage(WebSocketSession session, 
                                    TextMessage message) throws Exception {
        String roomId = getRoomId(session);
        String messageText = message.getPayload();
        
        // HTML rendern
        String html = """
            <div class="message" hx-swap-oob="beforeend:#messages">
                <strong>User</strong>: %s
            </div>
        """.formatted(messageText);
        
        // An alle Clients im Room senden
        broadcastToRoom(roomId, new TextMessage(html));
    }
    
    private void broadcastToRoom(String roomId, TextMessage message) {
        Set<WebSocketSession> sessions = rooms.get(roomId);
        if (sessions != null) {
            sessions.forEach(session -> {
                try {
                    session.sendMessage(message);
                } catch (IOException e) {
                    // Handle error
                }
            });
        }
    }
}
```

</v-clicks>

---

# Praktisches Beispiel: Live Notifications

<v-clicks>

**HTML:**

```html
<!DOCTYPE html>
<html>
<head>
    <script src="https://unpkg.com/htmx.org@2.0.4"></script>
    <script src="https://unpkg.com/htmx.org/dist/ext/sse.js"></script>
</head>
<body>
    <div id="notifications" 
         hx-ext="sse" 
         sse-connect="/notifications/stream"
         sse-swap="notification"
         hx-swap="afterbegin">
        <h2>Notifications</h2>
        <!-- Notifications erscheinen hier -->
    </div>
</body>
</html>
```

</v-clicks>

---

# Live Notifications - Server

<v-clicks>

```java
@RestController
public class NotificationController {
    
    @Autowired
    private SseEmitterService sseService;
    
    @GetMapping(path = "/notifications/stream", 
                produces = TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        sseService.addEmitter(emitter);
        
        emitter.onCompletion(() -> sseService.removeEmitter(emitter));
        emitter.onTimeout(() -> sseService.removeEmitter(emitter));
        
        return emitter;
    }
    
    // Notification erstellen
    @PostMapping("/notifications")
    public ResponseEntity<String> createNotification(
            @RequestBody Notification notification) {
        notificationService.save(notification);
        
        String html = renderNotification(notification);
        sseService.broadcast("notification", html);
        
        return ResponseEntity.ok("Sent");
    }
}
```

</v-clicks>

---

# SSE Emitter Service

<v-clicks>

```java
@Service
public class SseEmitterService {
    
    private final CopyOnWriteArrayList<SseEmitter> emitters = 
        new CopyOnWriteArrayList<>();
    
    public void addEmitter(SseEmitter emitter) {
        emitters.add(emitter);
    }
    
    public void removeEmitter(SseEmitter emitter) {
        emitters.remove(emitter);
    }
    
    public void broadcast(String eventName, String data) {
        List<SseEmitter> deadEmitters = new ArrayList<>();
        
        emitters.forEach(emitter -> {
            try {
                SseEmitter.SseEventBuilder event = SseEmitter.event()
                    .name(eventName)
                    .data(data);
                emitter.send(event);
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        });
        
        emitters.removeAll(deadEmitters);
    }
}
```

</v-clicks>

---

# Praktisches Beispiel: Live Dashboard

<v-clicks>

**Dashboard mit Polling:**

```html
<div id="dashboard">
    <div hx-get="/stats/users" 
         hx-trigger="every 5s"
         hx-swap="innerHTML">
        Loading users...
    </div>
    
    <div hx-get="/stats/orders" 
         hx-trigger="every 5s"
         hx-swap="innerHTML">
        Loading orders...
    </div>
    
    <div hx-get="/stats/revenue" 
         hx-trigger="every 10s"
         hx-swap="innerHTML">
        Loading revenue...
    </div>
</div>
```

**Server:**

```java
@GetMapping("/stats/users")
public String getUserStats() {
    long count = userService.getTodayCount();
    return "<div class='stat'>Users Today: " + count + "</div>";
}
```

</v-clicks>



---

# Zusammenfassung

<v-clicks>

**Real-time mit HTMX:**

1. ✅ **Polling** - Einfach, für langsame Updates
2. ✅ **SSE** - Effizient für Server → Client Push
3. ✅ **WebSockets** - Für bi-direktionale Kommunikation

**Best Practices:**
- **Polling** für selten ändernde Daten
- **SSE** für Server → Client Push (Notifications, Feeds)
- **WebSockets** für bi-direktionale Kommunikation (Chat)
- HTMX reconnected automatisch bei SSE

**HTMX macht's einfach:** Extensions für SSE & WebSockets! 🚀

</v-clicks>

---

# Übung 7: Live Update Tabelle

**Implementiere eine Tabelle mit echtzeit Updates:**

<v-clicks>

**Requirements:**
1. **SSE Endpoint** für Notifications (`SseEmitter`)
2. **HTML mit HTMX SSE Extension**
3. **Service** zum Broadcasten an alle Clients
4. **Form** zum Erstellen neuer Inhalte

</v-clicks>
