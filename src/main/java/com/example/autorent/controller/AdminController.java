package com.example.autorent.controller;



import com.example.autorent.dto.UserRegistrationDto;
import com.example.autorent.entity.*;
import com.example.autorent.entity.ReservationStatus;
import com.example.autorent.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final VehiculeService vehiculeService;
    private final CategorieService categorieService;
    private final ReservationService reservationService;

    // ====== Dashboard ======
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalClients", userService.countByRole(Role.CLIENT));
        model.addAttribute("totalAgents", userService.countByRole(Role.AGENT));
        model.addAttribute("totalVehicules", vehiculeService.countTotal());
        model.addAttribute("totalReservations", reservationService.countTotal());
        model.addAttribute("reservationsEnAttente",
                reservationService.countByStatut(ReservationStatus.EN_ATTENTE));
        model.addAttribute("reservations", reservationService.findAll());
        return "admin/dashboard";
    }

    // ====== Utilisateurs ======
    @GetMapping("/utilisateurs")
    public String listeUtilisateurs(Model model) {
        model.addAttribute("utilisateurs", userService.findAll());
        return "admin/utilisateurs";
    }

    @GetMapping("/utilisateur/nouveau")
    public String nouveauUtilisateur(Model model) {
        model.addAttribute("userDto", new UserRegistrationDto());
        return "admin/utilisateur-form";
    }

    @PostMapping("/utilisateur/nouveau")
    public String creerUtilisateur(@Valid @ModelAttribute("userDto") UserRegistrationDto dto,
                                   BindingResult result,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {
        if (result.hasErrors()) return "admin/utilisateur-form";
        try {
            userService.inscrire(dto);
            redirectAttributes.addFlashAttribute("success", "Utilisateur créé avec succès");
            return "redirect:/admin/utilisateurs";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "admin/utilisateur-form";
        }
    }

    @PostMapping("/utilisateur/{id}/toggle")
    public String toggleActif(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.toggleActif(id);
        redirectAttributes.addFlashAttribute("success", "Statut modifié");
        return "redirect:/admin/utilisateurs";
    }

    @PostMapping("/utilisateur/{id}/supprimer")
    public String supprimerUtilisateur(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Utilisateur supprimé");
        return "redirect:/admin/utilisateurs";
    }

    // ====== Véhicules ======
    @GetMapping("/vehicules")
    public String listeVehicules(Model model) {
        model.addAttribute("vehicules", vehiculeService.findAll());
        return "admin/vehicules";
    }

    @GetMapping("/vehicule/nouveau")
    public String nouveauVehicule(Model model) {
        model.addAttribute("vehicule", new Vehicule());
        model.addAttribute("categories", categorieService.findAll());
        return "admin/vehicule-form";
    }

    @PostMapping("/vehicule/nouveau")
    public String creerVehicule(@ModelAttribute Vehicule vehicule,
                                RedirectAttributes redirectAttributes) {
        try {
            vehiculeService.save(vehicule);
            redirectAttributes.addFlashAttribute("success", "Véhicule ajouté");
            return "redirect:/admin/vehicules";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/vehicule/nouveau";
        }
    }

    @GetMapping("/vehicule/{id}/modifier")
    public String modifierVehicule(@PathVariable Long id, Model model) {
        model.addAttribute("vehicule", vehiculeService.findById(id));
        model.addAttribute("categories", categorieService.findAll());
        return "admin/vehicule-form";
    }

    @PostMapping("/vehicule/{id}/modifier")
    public String mettreAJourVehicule(@PathVariable Long id,
                                      @ModelAttribute Vehicule vehicule,
                                      RedirectAttributes redirectAttributes) {
        try {
            vehiculeService.update(id, vehicule);
            redirectAttributes.addFlashAttribute("success", "Véhicule mis à jour");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/vehicules";
    }

    @PostMapping("/vehicule/{id}/supprimer")
    public String supprimerVehicule(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        vehiculeService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Véhicule supprimé");
        return "redirect:/admin/vehicules";
    }

    // ====== Catégories ======
    @GetMapping("/categories")
    public String listeCategories(Model model) {
        model.addAttribute("categories", categorieService.findAll());
        model.addAttribute("categorie", new Categorie());
        return "admin/categories";
    }

    @PostMapping("/categorie/nouveau")
    public String creerCategorie(@ModelAttribute Categorie categorie,
                                 RedirectAttributes redirectAttributes) {
        try {
            categorieService.save(categorie);
            redirectAttributes.addFlashAttribute("success", "Catégorie créée");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/categorie/{id}/supprimer")
    public String supprimerCategorie(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        categorieService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Catégorie supprimée");
        return "redirect:/admin/categories";
    }

    // ====== Réservations ======
    @GetMapping("/reservations")
    public String listeReservations(Model model) {
        model.addAttribute("reservations", reservationService.findAll());
        return "admin/reservations";
    }

    @PostMapping("/reservation/{id}/supprimer")
    public String supprimerReservation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        reservationService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Réservation supprimée");
        return "redirect:/admin/reservations";
    }

    @GetMapping("/acces-refuse")
    public String accesRefuse() {
        return "acces-refuse"; // يجب أن يطابق اسم الملف تماماً
    }
}
