package com.example.autorent.dto;

import com.example.autorent.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDto {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    @Email(message = "Email invalide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Minimum 6 caractères")
    private String password;

    @NotBlank(message = "Veuillez confirmer le mot de passe")
    private String confirmPassword;

    private String telephone;

    private String adresse;

    private Role role = Role.CLIENT;
}
