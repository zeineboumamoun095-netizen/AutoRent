package com.example.autorent.repository;


import com.example.autorent.entity.ReservationStatus;
import com.example.autorent.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByClientId(Long clientId);

    List<Reservation> findByVehiculeId(Long vehiculeId);

    List<Reservation> findByStatut(ReservationStatus statut);

    List<Reservation> findByAgentId(Long agentId);

    List<Reservation> findByClientIdOrderByDateCreationDesc(Long clientId);

    @Query("SELECT r FROM Reservation r WHERE r.dateDebut >= :today ORDER BY r.dateDebut ASC")
    List<Reservation> findUpcomingReservations(@Param("today") LocalDate today);

    @Query("SELECT r FROM Reservation r WHERE r.dateDebut = :date OR r.dateFin = :date")
    List<Reservation> findByDate(@Param("date") LocalDate date);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.statut = :statut")
    long countByStatut(@Param("statut") ReservationStatus statut);

    @Query("""
        SELECT r FROM Reservation r WHERE r.vehicule.id = :vehiculeId
        AND r.statut NOT IN ('ANNULEE', 'TERMINEE')
        AND NOT (r.dateFin < :dateDebut OR r.dateDebut > :dateFin)
    """)
    List<Reservation> findConflictingReservations(
            @Param("vehiculeId") Long vehiculeId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);
}
