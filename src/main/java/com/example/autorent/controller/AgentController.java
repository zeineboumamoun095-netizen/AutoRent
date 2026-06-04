package com.example.autorent.controller;


import com.example.autorent.service.ReservationService;
import com.example.autorent.service.UserService;
import com.example.autorent.service.VehiculeService;
import com.example.autorent.entity.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/agent")
@PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
@RequiredArgsConstructor
public class AgentController {

    private final ReservationService reservationService;
    private final VehiculeService vehiculeService;
    private final UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("reservations", reservationService.findAll());
        model.addAttribute("upcoming", reservationService.findUpcoming());
        model.addAttribute("totalVehicules", vehiculeService.countTotal());
        model.addAttribute("disponibles", vehiculeService.countDisponibles());
        return "agent/dashboard";
    }

    @GetMapping("/reservations")
    public String listeReservations(Model model) {
        model.addAttribute("reservations", reservationService.findAll());
        return "agent/reservations";
    }

    @GetMapping("/reservation/{id}")
    public String detailReservation(@PathVariable Long id, Model model) {
        model.addAttribute("reservation", reservationService.findById(id));
        return "agent/reservation-detail";
    }

    @PostMapping("/reservation/{id}/confirmer")
    public String confirmerReservation(@PathVariable Long id, Authentication auth,
                                       RedirectAttributes redirectAttributes) {
        try {
            reservationService.confirmer(id, auth.getName());
            redirectAttributes.addFlashAttribute("success", "Réservation confirmée");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/agent/reservations";
    }

    @PostMapping("/reservation/{id}/statut")
    public String changerStatut(@PathVariable Long id,
                                @RequestParam ReservationStatus statut,
                                RedirectAttributes redirectAttributes) {
        try {
            reservationService.changerStatut(id, statut);
            redirectAttributes.addFlashAttribute("success", "Statut mis à jour");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/agent/reservations";
    }

    @GetMapping("/agenda")
    public String agenda(Model model) {
        model.addAttribute("reservations", reservationService.findUpcoming());
        return "agent/agenda";
    }

    @GetMapping("/vehicules")
    public String vehicules(Model model) {
        model.addAttribute("vehicules", vehiculeService.findAll());
        return "agent/vehicules";
    }

    @GetMapping("/vehicule/{id}")
    public String ficheVehicule(@PathVariable Long id, Model model) {
        model.addAttribute("vehicule", vehiculeService.findById(id));
        model.addAttribute("reservations", reservationService.findByVehicule(id));
        return "agent/vehicule-fiche";
    }
}
