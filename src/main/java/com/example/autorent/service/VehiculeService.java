package com.example.autorent.service;


import com.example.autorent.entity.Vehicule;
import com.example.autorent.repository.VehiculeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VehiculeService {

    private final VehiculeRepository vehiculeRepository;

    public List<Vehicule> findAll() {
        return vehiculeRepository.findAll();
    }

    public List<Vehicule> findDisponibles() {
        return vehiculeRepository.findByDisponibleTrue();
    }

    public Vehicule findById(Long id) {
        return vehiculeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Véhicule introuvable"));
    }

    public Vehicule save(Vehicule vehicule) {
        return vehiculeRepository.save(vehicule);
    }

    public Vehicule update(Long id, Vehicule updated) {
        Vehicule v = findById(id);
        v.setMarque(updated.getMarque());
        v.setModele(updated.getModele());
        v.setAnnee(updated.getAnnee());
        v.setCouleur(updated.getCouleur());
        v.setPrixParJour(updated.getPrixParJour());
        v.setNombrePlaces(updated.getNombrePlaces());
        v.setTypeCarburant(updated.getTypeCarburant());
        v.setTransmission(updated.getTransmission());
        v.setDescription(updated.getDescription());
        v.setDisponible(updated.isDisponible());
        v.setKilometrage(updated.getKilometrage());
        v.setCategorie(updated.getCategorie());
        return vehiculeRepository.save(v);
    }

    public void delete(Long id) {
        vehiculeRepository.deleteById(id);
    }

    public List<Vehicule> rechercherDisponibles(LocalDate dateDebut, LocalDate dateFin) {
        return vehiculeRepository.findVehiculesDisponibles(dateDebut, dateFin);
    }

    public List<Vehicule> rechercherDisponiblesFiltered(LocalDate dateDebut, LocalDate dateFin,
                                                        Long categorieId, BigDecimal prixMax) {
        return vehiculeRepository.findVehiculesDisponiblesFiltered(dateDebut, dateFin, categorieId, prixMax);
    }

    public void toggleDisponible(Long id) {
        Vehicule v = findById(id);
        v.setDisponible(!v.isDisponible());
        vehiculeRepository.save(v);
    }

    public long countTotal() {
        return vehiculeRepository.count();
    }

    public long countDisponibles() {
        return vehiculeRepository.findByDisponibleTrue().size();
    }
}
