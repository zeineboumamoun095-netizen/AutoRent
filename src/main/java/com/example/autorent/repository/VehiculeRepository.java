package com.example.autorent.repository;


import com.example.autorent.entity.Vehicule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {

    List<Vehicule> findByDisponibleTrue();

    List<Vehicule> findByCategorieId(Long categorieId);

    List<Vehicule> findByMarqueContainingIgnoreCase(String marque);

    List<Vehicule> findByPrixParJourBetween(BigDecimal min, BigDecimal max);

    @Query("""
        SELECT v FROM Vehicule v WHERE v.disponible = true
        AND v.id NOT IN (
            SELECT r.vehicule.id FROM Reservation r
            WHERE r.statut NOT IN ('ANNULEE', 'TERMINEE')
            AND NOT (r.dateFin < :dateDebut OR r.dateDebut > :dateFin)
        )
    """)
    List<Vehicule> findVehiculesDisponibles(
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);

    @Query("""
        SELECT v FROM Vehicule v WHERE v.disponible = true
        AND v.id NOT IN (
            SELECT r.vehicule.id FROM Reservation r
            WHERE r.statut NOT IN ('ANNULEE', 'TERMINEE')
            AND NOT (r.dateFin < :dateDebut OR r.dateDebut > :dateFin)
        )
        AND (:categorieId IS NULL OR v.categorie.id = :categorieId)
        AND (:prixMax IS NULL OR v.prixParJour <= :prixMax)
    """)
    List<Vehicule> findVehiculesDisponiblesFiltered(
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin,
            @Param("categorieId") Long categorieId,
            @Param("prixMax") BigDecimal prixMax);
}
