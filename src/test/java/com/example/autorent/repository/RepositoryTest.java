package com.example.autorent.repository;

import com.example.autorent.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest                // charge uniquement JPA + H2
@ActiveProfiles("dev")      // utilise application-dev.properties (H2)
class RepositoryTest {

    @Autowired private UserRepository       userRepository;
    @Autowired private VehiculeRepository   vehiculeRepository;
    @Autowired private CategorieRepository  categorieRepository;
    @Autowired private ReservationRepository reservationRepository;

    private Categorie categorie;
    private Vehicule  vehicule;
    private User      client;

    @BeforeEach
    void setUp() {
        // Catégorie
        categorie = categorieRepository.save(
                Categorie.builder().nom("Berline").description("Berlines confortables").build()
        );

        // Véhicule
        vehicule = vehiculeRepository.save(
                Vehicule.builder()
                        .marque("BMW").modele("Serie3")
                        .annee(2023).immatriculation("ZZ-999-AA")
                        .prixParJour(new BigDecimal("95.00"))
                        .disponible(true).nombrePlaces(5)
                        .typeCarburant("Diesel").transmission("Automatique")
                        .categorie(categorie).build()
        );

        // Client
        client = userRepository.save(
                User.builder()
                        .nom("Test").prenom("User")
                        .email("test@autorent.com")
                        .password("hashed").role(Role.CLIENT).actif(true).build()
        );
    }

    // ── UserRepository ──────────────────────────────────────────

    @Test
    void userRepo_devrait_trouver_par_email() {
        Optional<User> found = userRepository.findByEmail("test@autorent.com");
        assertThat(found).isPresent();
        assertThat(found.get().getNom()).isEqualTo("Test");
    }

    @Test
    void userRepo_devrait_verifier_email_existant() {
        assertThat(userRepository.existsByEmail("test@autorent.com")).isTrue();
        assertThat(userRepository.existsByEmail("inconnu@test.com")).isFalse();
    }

    @Test
    void userRepo_devrait_trouver_par_role() {
        List<User> clients = userRepository.findByRole(Role.CLIENT);
        assertThat(clients).isNotEmpty();
        assertThat(clients.get(0).getRole()).isEqualTo(Role.CLIENT);
    }

    // ── VehiculeRepository ──────────────────────────────────────

    @Test
    void vehiculeRepo_devrait_trouver_disponibles() {
        List<Vehicule> dispos = vehiculeRepository.findByDisponibleTrue();
        assertThat(dispos).isNotEmpty();
        assertThat(dispos.get(0).isDisponible()).isTrue();
    }

    @Test
    void vehiculeRepo_devrait_trouver_par_categorie() {
        List<Vehicule> result = vehiculeRepository.findByCategorieId(categorie.getId());
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getModele()).isEqualTo("Serie3");
    }

    @Test
    void vehiculeRepo_devrait_trouver_disponibles_par_dates() {
        LocalDate debut = LocalDate.now().plusDays(1);
        LocalDate fin   = LocalDate.now().plusDays(5);

        List<Vehicule> result =
                vehiculeRepository.findVehiculesDisponibles(debut, fin);

        assertThat(result).isNotEmpty();  // aucun conflit → BMW disponible
    }

    // ── CategorieRepository ─────────────────────────────────────

    @Test
    void categorieRepo_devrait_trouver_par_nom() {
        Optional<Categorie> found = categorieRepository.findByNom("Berline");
        assertThat(found).isPresent();
        assertThat(found.get().getDescription()).isEqualTo("Berlines confortables");
    }

    @Test
    void categorieRepo_devrait_verifier_existence() {
        assertThat(categorieRepository.existsByNom("Berline")).isTrue();
        assertThat(categorieRepository.existsByNom("Inexistant")).isFalse();
    }

    // ── ReservationRepository ───────────────────────────────────

    @Test
    void reservationRepo_devrait_trouver_par_client() {
        Reservation resa = reservationRepository.save(
                Reservation.builder()
                        .dateDebut(LocalDate.now().plusDays(1))
                        .dateFin(LocalDate.now().plusDays(3))
                        .coutTotal(new BigDecimal("190.00"))
                        .statut(ReservationStatus.EN_ATTENTE)
                        .client(client).vehicule(vehicule)
                        .dateCreation(LocalDate.now()).build()
        );

        List<Reservation> result =
                reservationRepository.findByClientId(client.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(resa.getId());
    }

    @Test
    void reservationRepo_devrait_compter_par_statut() {
        reservationRepository.save(
                Reservation.builder()
                        .dateDebut(LocalDate.now().plusDays(1))
                        .dateFin(LocalDate.now().plusDays(2))
                        .coutTotal(new BigDecimal("95.00"))
                        .statut(ReservationStatus.EN_ATTENTE)
                        .client(client).vehicule(vehicule)
                        .dateCreation(LocalDate.now()).build()
        );

        long count =
                reservationRepository.countByStatut(ReservationStatus.EN_ATTENTE);

        assertThat(count).isEqualTo(1L);
    }

    @Test
    void reservationRepo_devrait_detecter_conflit() {
        // Réservation existante: jours 2 → 6
        reservationRepository.save(
                Reservation.builder()
                        .dateDebut(LocalDate.now().plusDays(2))
                        .dateFin(LocalDate.now().plusDays(6))
                        .coutTotal(new BigDecimal("380.00"))
                        .statut(ReservationStatus.CONFIRMEE)
                        .client(client).vehicule(vehicule)
                        .dateCreation(LocalDate.now()).build()
        );

        // Nouvelle demande: jours 3 → 5 → conflit!
        List<Reservation> conflits =
                reservationRepository.findConflictingReservations(
                        vehicule.getId(),
                        LocalDate.now().plusDays(3),
                        LocalDate.now().plusDays(5)
                );

        assertThat(conflits).isNotEmpty();  // conflit détecté
    }

    @Test
    void reservationRepo_devrait_ne_pas_detecter_conflit_si_dates_libres() {
        // Réservation existante: jours 1 → 3
        reservationRepository.save(
                Reservation.builder()
                        .dateDebut(LocalDate.now().plusDays(1))
                        .dateFin(LocalDate.now().plusDays(3))
                        .coutTotal(new BigDecimal("190.00"))
                        .statut(ReservationStatus.CONFIRMEE)
                        .client(client).vehicule(vehicule)
                        .dateCreation(LocalDate.now()).build()
        );

        // Nouvelle demande: jours 5 → 8 → pas de conflit
        List<Reservation> conflits =
                reservationRepository.findConflictingReservations(
                        vehicule.getId(),
                        LocalDate.now().plusDays(5),
                        LocalDate.now().plusDays(8)
                );

        assertThat(conflits).isEmpty();  // pas de conflit
    }
}
