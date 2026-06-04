package com.example.autorent.dto;


import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehiculeSearchDto {
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Long categorieId;
    private BigDecimal prixMax;
}
