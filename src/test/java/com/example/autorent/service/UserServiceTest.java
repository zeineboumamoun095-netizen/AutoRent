package com.example.autorent.service;


import com.example.autorent.dto.UserRegistrationDto;
import com.example.autorent.entity.Role;
import com.example.autorent.entity.User;
import com.example.autorent.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserRegistrationDto dto;

    @BeforeEach
    void setUp() {
        dto = new UserRegistrationDto();
        dto.setNom("Dupont");
        dto.setPrenom("Jean");
        dto.setEmail("jean@test.com");
        dto.setPassword("password123");
        dto.setConfirmPassword("password123");
        dto.setTelephone("0600000000");
        dto.setRole(Role.CLIENT);
    }

    // ── Test 1: Inscription réussie ──────────────────────────────
    @Test
    void inscrire_devrait_creer_utilisateur_avec_succes() {
        // Arrange
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        // Act
        User result = userService.inscrire(dto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("jean@test.com");
        assertThat(result.getNom()).isEqualTo("Dupont");
        assertThat(result.getRole()).isEqualTo(Role.CLIENT);
        assertThat(result.isActif()).isTrue();
        verify(userRepository).save(any(User.class));
    }

    // ── Test 2: Email déjà utilisé ───────────────────────────────
    @Test
    void inscrire_devrait_echouer_si_email_existe_deja() {
        // Arrange
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.inscrire(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("email");

        verify(userRepository, never()).save(any());
    }

    // ── Test 3: Mots de passe ne correspondent pas ───────────────
    @Test
    void inscrire_devrait_echouer_si_mdp_differents() {
        // Arrange
        dto.setConfirmPassword("autreMotDePasse");
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> userService.inscrire(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("passe");

        verify(userRepository, never()).save(any());
    }

    // ── Test 4: Trouver utilisateur par email ────────────────────
    @Test
    void findByEmail_devrait_retourner_utilisateur() {
        // Arrange
        User user = User.builder()
                .id(1L)
                .email("jean@test.com")
                .nom("Dupont")
                .role(Role.CLIENT)
                .build();
        when(userRepository.findByEmail("jean@test.com"))
                .thenReturn(Optional.of(user));

        // Act
        User result = userService.findByEmail("jean@test.com");

        // Assert
        assertThat(result.getEmail()).isEqualTo("jean@test.com");
        assertThat(result.getNom()).isEqualTo("Dupont");
    }

    // ── Test 5: Email introuvable ────────────────────────────────
    @Test
    void findByEmail_devrait_lancer_exception_si_introuvable() {
        // Arrange
        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.findByEmail("inconnu@test.com"))
                .isInstanceOf(RuntimeException.class);
    }

    // ── Test 6: Toggle actif/inactif ────────────────────────────
    @Test
    void toggleActif_devrait_inverser_statut() {
        // Arrange
        User user = User.builder().id(1L).actif(true).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        // Act
        userService.toggleActif(1L);

        // Assert
        assertThat(user.isActif()).isFalse();
        verify(userRepository).save(user);
    }
}
