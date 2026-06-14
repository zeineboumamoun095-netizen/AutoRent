package com.example.autorent.entity;



import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "reservations")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDate dateDebut;

    @NotNull
    private LocalDate dateFin;

    private BigDecimal coutTotal;
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private ReservationStatus statut = ReservationStatus.EN_ATTENTE;

    private String remarques;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private User client;

    @ManyToOne
    @JoinColumn(name = "vehicule_id")
    private Vehicule vehicule;

    @ManyToOne
    @JoinColumn(name = "agent_id")
    private User agent;

    private LocalDate dateCreation = LocalDate.now();

    public long getNombreJours() {
        return ChronoUnit.DAYS.between(dateDebut, dateFin);
    }

    public void calculerCout() {
        if (vehicule != null && dateDebut != null && dateFin != null) {
            long jours = getNombreJours();
            if (jours > 0) {
                this.coutTotal = vehicule.getPrixParJour()
                        .multiply(BigDecimal.valueOf(jours));
            }
        }
    }
}
