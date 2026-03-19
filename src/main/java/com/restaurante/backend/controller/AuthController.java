package com.restaurante.backend.controller;

import com.restaurante.backend.dto.AuthRequest;
import com.restaurante.backend.dto.AuthResponse;
import com.restaurante.backend.dto.PermissionDto;
import com.restaurante.backend.dto.RefreshTokenRequest;
import com.restaurante.backend.security.CustomUserDetails;
import com.restaurante.backend.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String jwt = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        AuthResponse response = buildAuthResponse(userDetails, jwt, refreshToken);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/auth/me
     *
     * Retorna los datos actualizados del usuario autenticado, incluyendo
     * permisos y rol frescos desde la base de datos.
     *
     * Úsalo desde el frontend al iniciar la app para refrescar el estado
     * del usuario sin obligarlo a hacer login de nuevo.
     *
     * Requiere: Authorization: Bearer <token> en el header.
     * Responde: 200 con AuthResponse actualizado, o 401 si el token es inválido.
     */
    @GetMapping("/me")
    public ResponseEntity<AuthResponse> me(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Recargamos desde la DB para obtener permisos y rol frescos
        CustomUserDetails freshDetails = (CustomUserDetails) userDetailsService
                .loadUserByUsername(userDetails.getUsername());

        // Generamos un token nuevo con los datos frescos (rota el token silenciosamente)
        String newToken = jwtUtil.generateToken(freshDetails);
        String newRefreshToken = jwtUtil.generateRefreshToken(freshDetails);

        AuthResponse response = buildAuthResponse(freshDetails, newToken, newRefreshToken);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        try {
            String username = jwtUtil.extractUsername(requestRefreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(requestRefreshToken, userDetails)) {
                CustomUserDetails customUser = (CustomUserDetails) userDetails;

                String newAccessToken = jwtUtil.generateToken(customUser);
                String newRefreshToken = jwtUtil.generateRefreshToken(customUser); // Rotate refresh token

                AuthResponse response = buildAuthResponse(customUser, newAccessToken, newRefreshToken);

                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            // Token invalid or expired
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private AuthResponse buildAuthResponse(CustomUserDetails user, String token, String refreshToken) {
        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .type("Bearer")
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .role(user.getRoleName())
                .tenantId(user.getTenantId())
                .branchId(user.getBranchId())
                .branchName(user.getBranchName())
                .permissions(user.getPermissions())
                .build();
    }
}
