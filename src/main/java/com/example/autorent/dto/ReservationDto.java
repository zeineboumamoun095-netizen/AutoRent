package com.example.autorent.dto;



import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class ReservationDto {

        @NotNull(message = "La date de début est obligatoire")
        private LocalDate dateDebut;

        @NotNull(message = "La date de fin est obligatoire")
        private LocalDate dateFin;

        @NotNull(message = "Le véhicule est obligatoire")
        private Long vehiculeId;

        private String remarques;
    }

