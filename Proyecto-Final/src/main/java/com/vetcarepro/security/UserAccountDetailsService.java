package com.vetcarepro.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.vetcarepro.repository.UserAccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserAccountDetailsService implements UserDetailsService {

    private final UserAccountRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findFirstByEmailIgnoreCase(email)
            .map(user -> new UserAccountPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                user.getReferenceId(),
                user.isEnabled(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
            ))
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }
}
