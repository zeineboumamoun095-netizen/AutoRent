package com.example.autorent.service;

import com.example.autorent.entity.Categorie;
import com.example.autorent.entity.Vehicule;
import com.example.autorent.repository.VehiculeRepository;
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
class VehiculeServiceTest {

    @Mock
    private VehiculeRepository vehiculeRepository;

    @InjectMocks
    private VehiculeService vehiculeService;

    private Vehicule vehicule;

    @BeforeEach
    void setUp() {
        Categorie cat = Categorie.builder().id(1L).nom("SUV").build();
        vehicule = Vehicule.builder()
                .id(1L)
                .marque("Toyota")
                .modele("RAV4")
                .annee(2023)
                .immatriculation("AB-123-CD")
                .prixParJour(new BigDecimal("85.00"))
                .nombrePlaces(5)
                .typeCarburant("Hybride")
                .transmission("Automatique")
                .disponible(true)
                .categorie(cat)
                .build();
    }

    // ── Test 1: Récupérer tous les véhicules ─────────────────────
    @Test
    void findAll_devrait_retourner_liste_vehicules() {
        // Arrange
        when(vehiculeRepository.findAll()).thenReturn(List.of(vehicule));

        // Act
        List<Vehicule> result = vehiculeService.findAll();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMarque()).isEqualTo("Toyota");
    }

    // ── Test 2: Trouver par ID ───────────────────────────────────
    @Test
    void findById_devrait_retourner_vehicule() {
        // Arrange
        when(vehiculeRepository.findById(1L)).thenReturn(Optional.of(vehicule));

        // Act
        Vehicule result = vehiculeService.findById(1L);

        // Assert
        assertThat(result.getModele()).isEqualTo("RAV4");
        assertThat(result.getPrixParJour()).isEqualTo(new BigDecimal("85.00"));
    }

    // ── Test 3: ID inexistant ────────────────────────────────────
    @Test
    void findById_devrait_lancer_exception_si_absent() {
        // Arrange
        when(vehiculeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> vehiculeService.findById(999L))
                .isInstanceOf(RuntimeException.class);
    }

    // ── Test 4: Sauvegarder un véhicule ─────────────────────────
    @Test
    void save_devrait_persister_vehicule() {
        // Arrange
        when(vehiculeRepository.save(any(Vehicule.class))).thenReturn(vehicule);

        // Act
        Vehicule result = vehiculeService.save(vehicule);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getImmatriculation()).isEqualTo("AB-123-CD");
        verify(vehiculeRepository).save(vehicule);
    }

    // ── Test 5: Véhicules disponibles ───────────────────────────
    @Test
    void findDisponibles_devrait_retourner_vehicules_libres() {
        // Arrange
        when(vehiculeRepository.findByDisponibleTrue()).thenReturn(List.of(vehicule));

        // Act
        List<Vehicule> result = vehiculeService.findDisponibles();

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).isDisponible()).isTrue();
    }

    // ── Test 6: Recherche par dates ──────────────────────────────
    @Test
    void rechercherDisponibles_devrait_filtrer_par_dates() {
        // Arrange
        LocalDate debut = LocalDate.now().plusDays(1);
        LocalDate fin   = LocalDate.now().plusDays(5);
        when(vehiculeRepository.findVehiculesDisponibles(debut, fin))
                .thenReturn(List.of(vehicule));

        // Act
        List<Vehicule> result = vehiculeService.rechercherDisponibles(debut, fin);

        // Assert
        assertThat(result).hasSize(1);
        verify(vehiculeRepository).findVehiculesDisponibles(debut, fin);
    }

    // ── Test 7: Toggle disponibilité ─────────────────────────────
    @Test
    void toggleDisponible_devrait_inverser_disponibilite() {
        // Arrange
        when(vehiculeRepository.findById(1L)).thenReturn(Optional.of(vehicule));
        when(vehiculeRepository.save(any())).thenReturn(vehicule);

        // Act
        vehiculeService.toggleDisponible(1L);

        // Assert
        assertThat(vehicule.isDisponible()).isFalse();
        verify(vehiculeRepository).save(vehicule);
    }

    // ── Test 8: Supprimer un véhicule ────────────────────────────
    @Test
    void delete_devrait_appeler_repository() {
        // Act
        vehiculeService.delete(1L);

        // Assert
        verify(vehiculeRepository).deleteById(1L);
    }
}
