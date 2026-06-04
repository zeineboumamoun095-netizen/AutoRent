package com.example.autorent.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nom;

    @NotBlank
    private String prenom;

    @Email
    @NotBlank
    @Column(unique = true)
    private String email;

    @NotBlank
    private String password;

    private String telephone;

    private String adresse;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean actif = true;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Reservation> reservations;

    public String getFullName() {
        return prenom + " " + nom;
    }
}
