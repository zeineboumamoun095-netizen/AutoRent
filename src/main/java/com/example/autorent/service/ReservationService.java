package com.example.autorent.service;


import com.example.autorent.dto.ReservationDto;
import com.example.autorent.entity.*;
import com.example.autorent.entity.ReservationStatus;
import com.example.autorent.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final VehiculeService vehiculeService;
    private final UserService userService;

    public Reservation creerReservation(ReservationDto dto, String clientEmail) {
        User client = userService.findByEmail(clientEmail);
        Vehicule vehicule = vehiculeService.findById(dto.getVehiculeId());

        // Validation des dates
        if (dto.getDateDebut().isBefore(LocalDate.now())) {
            throw new RuntimeException("La date de début ne peut pas être dans le passé");
        }
        if (dto.getDateFin().isBefore(dto.getDateDebut()) || dto.getDateFin().equals(dto.getDateDebut())) {
            throw new RuntimeException("La date de fin doit être après la date de début");
        }

        // Vérifier la disponibilité
        List<Reservation> conflits = reservationRepository.findConflictingReservations(
                vehicule.getId(), dto.getDateDebut(), dto.getDateFin());
        if (!conflits.isEmpty()) {
            throw new RuntimeException("Le véhicule n'est pas disponible pour ces dates");
        }

        Reservation reservation = Reservation.builder()
                .dateDebut(dto.getDateDebut())
                .dateFin(dto.getDateFin())
                .vehicule(vehicule)
                .client(client)
                .remarques(dto.getRemarques())
                .statut(ReservationStatus.EN_ATTENTE)
                .dateCreation(LocalDate.now())
                .build();

        reservation.calculerCout();
        return reservationRepository.save(reservation);
    }

    public Reservation findById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable"));
    }

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    public List<Reservation> findByClient(String email) {
        User client = userService.findByEmail(email);
        return reservationRepository.findByClientIdOrderByDateCreationDesc(client.getId());
    }

    public List<Reservation> findByVehicule(Long vehiculeId) {
        return reservationRepository.findByVehiculeId(vehiculeId);
    }

    public Reservation changerStatut(Long id, ReservationStatus statut) {
        Reservation reservation = findById(id);
        reservation.setStatut(statut);
        return reservationRepository.save(reservation);
    }

    public Reservation confirmer(Long id, String agentEmail) {
        Reservation reservation = findById(id);
        User agent = userService.findByEmail(agentEmail);
        reservation.setStatut(ReservationStatus.CONFIRMEE);
        reservation.setAgent(agent);
        return reservationRepository.save(reservation);
    }

    public Reservation annuler(Long id) {
        Reservation reservation = findById(id);
        if (reservation.getStatut() == ReservationStatus.EN_COURS ||
                reservation.getStatut() == ReservationStatus.TERMINEE) {
            throw new RuntimeException("Impossible d'annuler cette réservation");
        }
        reservation.setStatut(ReservationStatus.ANNULEE);
        return reservationRepository.save(reservation);
    }

    public void delete(Long id) {
        reservationRepository.deleteById(id);
    }

    public List<Reservation> findUpcoming() {
        return reservationRepository.findUpcomingReservations(LocalDate.now());
    }

    public long countByStatut(ReservationStatus statut) {
        return reservationRepository.countByStatut(statut);
    }

    public long countTotal() {
        return reservationRepository.count();
    }
}
