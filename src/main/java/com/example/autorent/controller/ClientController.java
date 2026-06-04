package com.example.autorent.controller;

import com.example.autorent.dto.ReservationDto;
import com.example.autorent.entity.User;
import com.example.autorent.service.CategorieService;
import com.example.autorent.service.ReservationService;
import com.example.autorent.service.UserService;
import com.example.autorent.service.VehiculeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/client")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class ClientController {

    private final ReservationService reservationService;
    private final VehiculeService vehiculeService;
    private final CategorieService categorieService;
    private final UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        User user = userService.findByEmail(auth.getName());
        model.addAttribute("user", user);
        model.addAttribute("reservations", reservationService.findByClient(auth.getName()));
        return "client/dashboard";
    }

    @GetMapping("/reserver/{vehiculeId}")
    public String formulaireReservation(@PathVariable Long vehiculeId, Model model) {
        model.addAttribute("vehicule", vehiculeService.findById(vehiculeId));
        model.addAttribute("reservationDto", new ReservationDto());
        return "client/reservation-form";
    }

    @PostMapping("/reserver")
    public String effectuerReservation(@Valid @ModelAttribute ReservationDto dto,
                                       BindingResult result,
                                       Authentication auth,
                                       RedirectAttributes redirectAttributes,
                                       Model model) {
        if (result.hasErrors()) {
            model.addAttribute("vehicule", vehiculeService.findById(dto.getVehiculeId()));
            return "client/reservation-form";
        }

        try {
            reservationService.creerReservation(dto, auth.getName());
            redirectAttributes.addFlashAttribute("success", "Réservation effectuée avec succès !");
            return "redirect:/client/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("vehicule", vehiculeService.findById(dto.getVehiculeId()));
            return "client/reservation-form";
        }
    }

    @PostMapping("/reservation/{id}/annuler")
    public String annulerReservation(@PathVariable Long id,
                                     Authentication auth,
                                     RedirectAttributes redirectAttributes) {
        try {
            var reservation = reservationService.findById(id);
            if (!reservation.getClient().getEmail().equals(auth.getName())) {
                redirectAttributes.addFlashAttribute("error", "Action non autorisée");
                return "redirect:/client/dashboard";
            }
            reservationService.annuler(id);
            redirectAttributes.addFlashAttribute("success", "Réservation annulée");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/client/dashboard";
    }

    @GetMapping("/profil")
    public String profil(Authentication auth, Model model) {
        model.addAttribute("user", userService.findByEmail(auth.getName()));
        return "client/profil";
    }
}