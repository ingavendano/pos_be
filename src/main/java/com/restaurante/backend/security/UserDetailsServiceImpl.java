package com.restaurante.backend.security;

import com.restaurante.backend.domain.entity.User;
import com.restaurante.backend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Long tenantId = TenantContext.getCurrentTenantId();
        
        User user;
        if (tenantId != null) {
            // If domain identified a tenant, strictly search within that tenant
            user = userRepository.findByUsernameAndTenantId(username, tenantId)
                    .orElseThrow(() -> {
                        System.err.println("User not found for username: " + username + " and tenantId: " + tenantId);
                        return new UsernameNotFoundException("Usuario no encontrado en esta empresa: " + username);
                    });
        } else {
            // Fallback to global search if no domain matched (e.g. localhost development without domain specific setup)
            user = userRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        System.err.println("User not found globally for username: " + username);
                        return new UsernameNotFoundException("Usuario no encontrado: " + username);
                    });
        }

        return new CustomUserDetails(user);
    }
}
