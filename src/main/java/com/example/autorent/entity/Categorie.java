package com.example.autorent.entity;



import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "categories")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categorie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String nom;

    private String description;

    @OneToMany(mappedBy = "categorie", cascade = CascadeType.ALL)
    private List<Vehicule> vehicules;
}
