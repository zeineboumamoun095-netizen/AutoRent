package com.example.autorent.service;


import com.example.autorent.entity.Categorie;
import com.example.autorent.repository.CategorieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategorieService {

    private final CategorieRepository categorieRepository;

    public List<Categorie> findAll() {
        return categorieRepository.findAll();
    }

    public Categorie findById(Long id) {
        return categorieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));
    }

    public Categorie save(Categorie categorie) {
        if (categorieRepository.existsByNom(categorie.getNom())) {
            throw new RuntimeException("Cette catégorie existe déjà");
        }
        return categorieRepository.save(categorie);
    }

    public Categorie update(Long id, Categorie updated) {
        Categorie categorie = findById(id);
        categorie.setNom(updated.getNom());
        categorie.setDescription(updated.getDescription());
        return categorieRepository.save(categorie);
    }

    public void delete(Long id) {
        categorieRepository.deleteById(id);
    }
}
