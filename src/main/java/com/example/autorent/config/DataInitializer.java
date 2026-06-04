package com.example.autorent.config;



import com.example.autorent.entity.*;
import com.example.autorent.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

    @Component
    @RequiredArgsConstructor
    public class DataInitializer implements CommandLineRunner {

        private final UserRepository userRepository;
        private final CategorieRepository categorieRepository;
        private final VehiculeRepository vehiculeRepository;
        private final PasswordEncoder passwordEncoder;

        @Override
        public void run(String... args) {
            if (userRepository.count() == 0) {
                initUsers();
            }
            if (categorieRepository.count() == 0) {
                initCategories();
            }
            if (vehiculeRepository.count() == 0) {
                initVehicules();
            }
        }

        private void initUsers() {
            userRepository.save(User.builder()
                    .nom("Admin").prenom("Super")
                    .email("admin@autorent.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN).actif(true)
                    .telephone("0600000000").adresse("Siège Social")
                    .build());

            userRepository.save(User.builder()
                    .nom("Dupont").prenom("Pierre")
                    .email("agent@autorent.com")
                    .password(passwordEncoder.encode("agent123"))
                    .role(Role.AGENT).actif(true)
                    .telephone("0611111111").adresse("Agence Paris")
                    .build());

            userRepository.save(User.builder()
                    .nom("Martin").prenom("Marie")
                    .email("client@autorent.com")
                    .password(passwordEncoder.encode("client123"))
                    .role(Role.CLIENT).actif(true)
                    .telephone("0622222222").adresse("12 Rue de la Paix, Paris")
                    .build());

            System.out.println("✅ Utilisateurs initialisés");
        }

        private void initCategories() {
            categorieRepository.save(Categorie.builder().nom("Économique").description("Voitures compactes et économiques").build());
            categorieRepository.save(Categorie.builder().nom("Berline").description("Berlines confortables pour tous trajets").build());
            categorieRepository.save(Categorie.builder().nom("SUV").description("SUV spacieux pour les familles").build());
            categorieRepository.save(Categorie.builder().nom("Luxe").description("Véhicules haut de gamme").build());
            categorieRepository.save(Categorie.builder().nom("Utilitaire").description("Camionnettes et utilitaires").build());
            System.out.println("✅ Catégories initialisées");
        }

        private void initVehicules() {
            Categorie eco = categorieRepository.findByNom("Économique").orElseThrow();
            Categorie berline = categorieRepository.findByNom("Berline").orElseThrow();
            Categorie suv = categorieRepository.findByNom("SUV").orElseThrow();
            Categorie luxe = categorieRepository.findByNom("Luxe").orElseThrow();
            Categorie utilitaire = categorieRepository.findByNom("Utilitaire").orElseThrow();

            vehiculeRepository.save(Vehicule.builder().marque("Peugeot").modele("208").annee(2022)
                    .immatriculation("AB-123-CD").prixParJour(new BigDecimal("45.00"))
                    .couleur("Blanc").nombrePlaces(5).typeCarburant("Essence")
                    .transmission("Manuelle").disponible(true).kilometrage(15000)
                    .categorie(eco).description("Citadine parfaite pour la ville").build());

            vehiculeRepository.save(Vehicule.builder().marque("Renault").modele("Clio").annee(2023)
                    .immatriculation("EF-456-GH").prixParJour(new BigDecimal("42.00"))
                    .couleur("Rouge").nombrePlaces(5).typeCarburant("Essence")
                    .transmission("Automatique").disponible(true).kilometrage(8000)
                    .categorie(eco).description("Fiable et économique").build());

            vehiculeRepository.save(Vehicule.builder().marque("Toyota").modele("Corolla").annee(2022)
                    .immatriculation("IJ-789-KL").prixParJour(new BigDecimal("65.00"))
                    .couleur("Gris").nombrePlaces(5).typeCarburant("Hybride")
                    .transmission("Automatique").disponible(true).kilometrage(22000)
                    .categorie(berline).description("Berline hybride confortable").build());

            vehiculeRepository.save(Vehicule.builder().marque("BMW").modele("Série 3").annee(2023)
                    .immatriculation("MN-012-OP").prixParJour(new BigDecimal("95.00"))
                    .couleur("Noir").nombrePlaces(5).typeCarburant("Diesel")
                    .transmission("Automatique").disponible(true).kilometrage(5000)
                    .categorie(berline).description("Berline premium performante").build());

            vehiculeRepository.save(Vehicule.builder().marque("Volkswagen").modele("Tiguan").annee(2022)
                    .immatriculation("QR-345-ST").prixParJour(new BigDecimal("85.00"))
                    .couleur("Bleu").nombrePlaces(7).typeCarburant("Diesel")
                    .transmission("Automatique").disponible(true).kilometrage(18000)
                    .categorie(suv).description("SUV spacieux idéal famille").build());

            vehiculeRepository.save(Vehicule.builder().marque("Toyota").modele("RAV4").annee(2023)
                    .immatriculation("UV-678-WX").prixParJour(new BigDecimal("90.00"))
                    .couleur("Blanc").nombrePlaces(5).typeCarburant("Hybride")
                    .transmission("Automatique").disponible(true).kilometrage(3000)
                    .categorie(suv).description("SUV hybride tout terrain").build());

            vehiculeRepository.save(Vehicule.builder().marque("Mercedes").modele("Classe E").annee(2023)
                    .immatriculation("YZ-901-AB").prixParJour(new BigDecimal("150.00"))
                    .couleur("Argent").nombrePlaces(5).typeCarburant("Essence")
                    .transmission("Automatique").disponible(true).kilometrage(7000)
                    .categorie(luxe).description("Berline de luxe haut de gamme").build());

            vehiculeRepository.save(Vehicule.builder().marque("Audi").modele("Q7").annee(2022)
                    .immatriculation("CD-234-EF").prixParJour(new BigDecimal("180.00"))
                    .couleur("Noir").nombrePlaces(7).typeCarburant("Diesel")
                    .transmission("Automatique").disponible(true).kilometrage(12000)
                    .categorie(luxe).description("SUV de luxe premium").build());

            vehiculeRepository.save(Vehicule.builder().marque("Renault").modele("Master").annee(2021)
                    .immatriculation("GH-567-IJ").prixParJour(new BigDecimal("75.00"))
                    .couleur("Blanc").nombrePlaces(3).typeCarburant("Diesel")
                    .transmission("Manuelle").disponible(true).kilometrage(45000)
                    .categorie(utilitaire).description("Fourgon utilitaire spacieux").build());

            System.out.println("✅ Véhicules initialisés");
        }
    }

