package com.example.autorent.service;


import com.example.autorent.dto.UserRegistrationDto;
import com.example.autorent.entity.User;
import com.example.autorent.entity.Role;
import com.example.autorent.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User inscrire(UserRegistrationDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("Les mots de passe ne correspondent pas");
        }

        User user = User.builder()
                .nom(dto.getNom())
                .prenom(dto.getPrenom())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .telephone(dto.getTelephone())
                .adresse(dto.getAdresse())
                .role(dto.getRole() != null ? dto.getRole() : Role.CLIENT)
                .actif(true)
                .build();

        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public User update(Long id, User updatedUser) {
        User user = findById(id);
        user.setNom(updatedUser.getNom());
        user.setPrenom(updatedUser.getPrenom());
        user.setTelephone(updatedUser.getTelephone());
        user.setAdresse(updatedUser.getAdresse());
        return userRepository.save(user);
    }

    public void toggleActif(Long id) {
        User user = findById(id);
        user.setActif(!user.isActif());
        userRepository.save(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public long countByRole(Role role) {
        return userRepository.findByRole(role).size();
    }
}
