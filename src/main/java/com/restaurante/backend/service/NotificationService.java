package com.restaurante.backend.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {

    /** Map of tenantId → list of active SSE emitters for that tenant */
    private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    /**
     * Registers a new SSE emitter for the given tenant.
     * The emitter has a 5-minute timeout; it removes itself on completion or error.
     */
    public SseEmitter subscribe(Long tenantId) {
        SseEmitter emitter = new SseEmitter(5 * 60 * 1_000L); // 5 min timeout
        emitters.computeIfAbsent(tenantId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        Runnable remove = () -> emitters.getOrDefault(tenantId, List.of()).remove(emitter);
        emitter.onCompletion(remove);
        emitter.onTimeout(remove);
        emitter.onError(e -> remove.run());

        return emitter;
    }

    /**
     * Sends a named event to all SSE clients subscribed for the given tenant.
     *
     * @param tenantId  target tenant
     * @param eventType e.g. "new_order", "order_paid", "cash_register"
     * @param data      JSON-serializable payload (just use a simple String)
     */
    @Async
    public void sendToTenant(Long tenantId, String eventType, String data) {
        List<SseEmitter> tenantEmitters = emitters.get(tenantId);
        if (tenantEmitters == null || tenantEmitters.isEmpty())
            return;

        List<SseEmitter> dead = new CopyOnWriteArrayList<>();
        tenantEmitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event().name(eventType).data(data));
            } catch (IOException e) {
                dead.add(emitter);
            }
        });
        tenantEmitters.removeAll(dead);
    }
}
