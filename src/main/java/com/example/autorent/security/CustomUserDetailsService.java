package com.example.autorent.security;


import com.example.autorent.entity.User;
import com.example.autorent.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

    @Service
    @RequiredArgsConstructor
    public class CustomUserDetailsService implements UserDetailsService {

        private final UserRepository userRepository;

        @Override
        public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable: " + email));

            if (!user.isActif()) {
                throw new UsernameNotFoundException("Compte désactivé: " + email);
            }

            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
            );
        }
    }

