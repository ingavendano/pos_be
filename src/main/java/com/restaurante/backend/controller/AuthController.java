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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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

        AuthResponse response = AuthResponse.builder()
                .token(jwt)
                .refreshToken(refreshToken)
                .type("Bearer")
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .name(userDetails.getName())
                .role(userDetails.getRoleName())
                .tenantId(userDetails.getTenantId())
                .branchId(userDetails.getBranchId())
                .branchName(userDetails.getBranchName())
                .permissions(userDetails.getPermissions())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        try {
            String username = jwtUtil.extractUsername(requestRefreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(requestRefreshToken, userDetails)) {
                String newAccessToken = jwtUtil.generateToken(userDetails);
                String newRefreshToken = jwtUtil.generateRefreshToken(userDetails); // Optionally rotate refresh token

                CustomUserDetails customUser = (CustomUserDetails) userDetails;

                AuthResponse response = AuthResponse.builder()
                        .token(newAccessToken)
                        .refreshToken(newRefreshToken)
                        .type("Bearer")
                        .id(customUser.getId())
                        .username(customUser.getUsername())
                        .name(customUser.getName())
                        .role(customUser.getRoleName())
                        .tenantId(customUser.getTenantId())
                        .branchId(customUser.getBranchId())
                        .branchName(customUser.getBranchName())
                        .permissions(customUser.getPermissions())
                        .build();

                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            // Token invalid or expired
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
