package com.example.autorent.entity;



import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "vehicules")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String marque;

    @NotBlank
    private String modele;

    @NotNull
    private Integer annee;

    @NotBlank
    @Column(unique = true)
    private String immatriculation;

    @NotNull
    @Positive
    private BigDecimal prixParJour;

    private String couleur;

    private Integer nombrePlaces;

    private String typeCarburant;

    private String transmission;

    private String description;

    private String imageUrl;

    private boolean disponible = true;

    private Integer kilometrage;

    @ManyToOne
    @JoinColumn(name = "categorie_id")
    private Categorie categorie;

    @OneToMany(mappedBy = "vehicule", cascade = CascadeType.ALL)
    private List<Reservation> reservations;
}
