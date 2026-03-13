package com.restaurante.backend.controller;

import com.restaurante.backend.dto.SetupStatusResponse;
import com.restaurante.backend.dto.SetupWizardRequest;
import com.restaurante.backend.repository.TenantRepository;
import com.restaurante.backend.security.TenantContext;
import com.restaurante.backend.service.impl.SetupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/setup")
@RequiredArgsConstructor
public class SetupController {

    private final TenantRepository tenantRepository;
    private final SetupService setupService;

    @GetMapping("/status")
    public ResponseEntity<SetupStatusResponse> getStatus() {
        // Multi-tenant check: is THIS domain already registered?
        boolean isSetupComplete = TenantContext.getCurrentTenant() != null;
        return ResponseEntity.ok(new SetupStatusResponse(isSetupComplete,
                isSetupComplete ? "Setup is already complete for this domain." : "Domain requires setup."));
    }

    @PostMapping("/wizard")
    public ResponseEntity<?> runWizard(@RequestBody SetupWizardRequest request) {
        // If domain already exists, block wizard
        if (tenantRepository.findByDomain(request.getDomain()).isPresent()) {
            return ResponseEntity.badRequest().body("Setup has already been completed for this domain.");
        }

        try {
            setupService.runSetupWizard(request);
            return ResponseEntity.ok("Setup completed successfully.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error during setup: " + e.getMessage());
        }
    }
}
