package com.example.autorent.service;

import com.example.autorent.dto.ReservationDto;
import com.example.autorent.entity.*;
import com.example.autorent.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private VehiculeService vehiculeService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ReservationService reservationService;

    private User client;
    private Vehicule vehicule;
    private ReservationDto dto;

    @BeforeEach
    void setUp() {
        client = User.builder()
                .id(1L).email("client@test.com")
                .nom("Martin").prenom("Alice")
                .role(Role.CLIENT).build();

        vehicule = Vehicule.builder()
                .id(1L).marque("Peugeot").modele("208")
                .prixParJour(new BigDecimal("50.00"))
                .disponible(true).build();

        dto = new ReservationDto();
        dto.setVehiculeId(1L);
        dto.setDateDebut(LocalDate.now().plusDays(1));
        dto.setDateFin(LocalDate.now().plusDays(4));   // 3 jours → 150€
    }

    // ── Test 1: Création réservation réussie ─────────────────────
    @Test
    void creerReservation_devrait_reussir() {
        // Arrange
        when(userService.findByEmail("client@test.com")).thenReturn(client);
        when(vehiculeService.findById(1L)).thenReturn(vehicule);
        when(reservationRepository.findConflictingReservations(anyLong(), any(), any()))
                .thenReturn(List.of());
        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(inv -> {
                    Reservation r = inv.getArgument(0);
                    r.setId(1L);
                    return r;
                });

        // Act
        Reservation result = reservationService.creerReservation(dto, "client@test.com");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStatut()).isEqualTo(ReservationStatus.EN_ATTENTE);
        assertThat(result.getCoutTotal()).isEqualByComparingTo("150.00");
        assertThat(result.getNombreJours()).isEqualTo(3);
        verify(reservationRepository).save(any(Reservation.class));
    }

    // ── Test 2: Véhicule non disponible (conflit) ────────────────
    @Test
    void creerReservation_devrait_echouer_si_vehicule_occupe() {
        // Arrange
        when(userService.findByEmail(anyString())).thenReturn(client);
        when(vehiculeService.findById(1L)).thenReturn(vehicule);
        when(reservationRepository.findConflictingReservations(anyLong(), any(), any()))
                .thenReturn(List.of(new Reservation()));  // conflit existant

        // Act & Assert
        assertThatThrownBy(() ->
                reservationService.creerReservation(dto, "client@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("disponible");

        verify(reservationRepository, never()).save(any());
    }

    // ── Test 3: Date de début dans le passé ──────────────────────
    @Test
    void creerReservation_devrait_echouer_si_date_passee() {
        // Arrange
        dto.setDateDebut(LocalDate.now().minusDays(2));
        when(userService.findByEmail(anyString())).thenReturn(client);
        when(vehiculeService.findById(anyLong())).thenReturn(vehicule);

        // Act & Assert
        assertThatThrownBy(() ->
                reservationService.creerReservation(dto, "client@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("passé");
    }

    // ── Test 4: Date fin avant date début ────────────────────────
    @Test
    void creerReservation_devrait_echouer_si_dates_invalides() {
        // Arrange
        dto.setDateDebut(LocalDate.now().plusDays(5));
        dto.setDateFin(LocalDate.now().plusDays(2));   // fin < début
        when(userService.findByEmail(anyString())).thenReturn(client);
        when(vehiculeService.findById(anyLong())).thenReturn(vehicule);

        // Act & Assert
        assertThatThrownBy(() ->
                reservationService.creerReservation(dto, "client@test.com"))
                .isInstanceOf(RuntimeException.class);
    }

    // ── Test 5: Confirmer une réservation ────────────────────────
    @Test
    void confirmer_devrait_changer_statut_en_CONFIRMEE() {
        // Arrange
        User agent = User.builder().id(2L).email("agent@test.com")
                .role(Role.AGENT).build();
        Reservation resa = Reservation.builder()
                .id(1L).statut(ReservationStatus.EN_ATTENTE).build();

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(resa));
        when(userService.findByEmail("agent@test.com")).thenReturn(agent);
        when(reservationRepository.save(any())).thenReturn(resa);

        // Act
        Reservation result = reservationService.confirmer(1L, "agent@test.com");

        // Assert
        assertThat(result.getStatut()).isEqualTo(ReservationStatus.CONFIRMEE);
        assertThat(result.getAgent()).isEqualTo(agent);
    }

    // ── Test 6: Annuler une réservation EN_ATTENTE ───────────────
    @Test
    void annuler_devrait_changer_statut_en_ANNULEE() {
        // Arrange
        Reservation resa = Reservation.builder()
                .id(1L).statut(ReservationStatus.EN_ATTENTE).build();
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(resa));
        when(reservationRepository.save(any())).thenReturn(resa);

        // Act
        Reservation result = reservationService.annuler(1L);

        // Assert
        assertThat(result.getStatut()).isEqualTo(ReservationStatus.ANNULEE);
    }

    // ── Test 7: Annuler une réservation EN_COURS → interdit ──────
    @Test
    void annuler_devrait_echouer_si_en_cours() {
        // Arrange
        Reservation resa = Reservation.builder()
                .id(1L).statut(ReservationStatus.EN_COURS).build();
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(resa));

        // Act & Assert
        assertThatThrownBy(() -> reservationService.annuler(1L))
                .isInstanceOf(RuntimeException.class);
    }

    // ── Test 8: Compter par statut ───────────────────────────────
    @Test
    void countByStatut_devrait_retourner_bon_nombre() {
        // Arrange
        when(reservationRepository.countByStatut(ReservationStatus.EN_ATTENTE))
                .thenReturn(5L);

        // Act
        long result = reservationService.countByStatut(ReservationStatus.EN_ATTENTE);

        // Assert
        assertThat(result).isEqualTo(5L);
    }
}
