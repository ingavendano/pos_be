package com.restaurante.backend.controller;

import com.restaurante.backend.security.TenantSecurityService;
import com.restaurante.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final TenantSecurityService tenantSecurity;

    /**
     * Frontend connects here via EventSource to receive real-time events.
     * URL: GET /api/notifications/stream/tenant/{tenantId}
     */
    @GetMapping(value = "/stream/tenant/{tenantId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@PathVariable Long tenantId) {
        tenantSecurity.verifyTenantAccess(tenantId);
        return notificationService.subscribe(tenantId);
    }
}
