package com.example.autorent.controller;



import com.example.autorent.dto.VehiculeSearchDto;
import com.example.autorent.entity.Vehicule;
import com.example.autorent.service.CategorieService;
import com.example.autorent.service.VehiculeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final VehiculeService vehiculeService;
    private final CategorieService categorieService;

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("vehicules", vehiculeService.findDisponibles());
        model.addAttribute("categories", categorieService.findAll());
        model.addAttribute("searchDto", new VehiculeSearchDto());
        return "home";
    }

    @GetMapping("/vehicules")
    public String vehicules(Model model) {
        model.addAttribute("vehicules", vehiculeService.findDisponibles());
        model.addAttribute("categories", categorieService.findAll());
        model.addAttribute("searchDto", new VehiculeSearchDto());
        return "vehicules/liste";
    }

    @PostMapping("/vehicules/rechercher")
    public String rechercherVehicules(@ModelAttribute VehiculeSearchDto dto, Model model) {
        List<Vehicule> resultats;
        if (dto.getDateDebut() != null && dto.getDateFin() != null) {
            resultats = vehiculeService.rechercherDisponiblesFiltered(
                    dto.getDateDebut(), dto.getDateFin(),
                    dto.getCategorieId(), dto.getPrixMax());
        } else {
            resultats = vehiculeService.findDisponibles();
        }
        model.addAttribute("vehicules", resultats);
        model.addAttribute("categories", categorieService.findAll());
        model.addAttribute("searchDto", dto);
        return "vehicules/liste";
    }

    @GetMapping("/vehicules/{id}")
    public String detailVehicule(@PathVariable Long id, Model model) {
        model.addAttribute("vehicule", vehiculeService.findById(id));
        return "vehicules/detail";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "redirect:/client/dashboard";
    }

    @GetMapping("/acces-refuse")
    public String accesRefuse() {
        return "acces-refuse";
    }
}
