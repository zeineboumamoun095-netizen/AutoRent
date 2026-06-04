

package com.example.autorent.controller;

import com.example.autorent.dto.UserRegistrationDto;
import com.example.autorent.entity.Role;
import com.example.autorent.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return redirectByRole(authentication);
        }
        return "auth/login";
    }

    @GetMapping("/inscription")
    public String inscriptionPage(Model model) {
        model.addAttribute("userDto", new UserRegistrationDto());
        return "auth/inscription";
    }

    @PostMapping("/inscription")
    public String inscrire(@Valid @ModelAttribute("userDto") UserRegistrationDto dto,
                           BindingResult result,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        if (result.hasErrors()) {
            return "auth/inscription";
        }

        try {
            dto.setRole(Role.CLIENT);
            userService.inscrire(dto);
            redirectAttributes.addFlashAttribute("success", "Inscription réussie ! Vous pouvez vous connecter.");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "auth/inscription";
        }
    }

    private String redirectByRole(Authentication auth) {
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "redirect:/admin/dashboard";
        } else if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_AGENT"))) {
            return "redirect:/agent/dashboard";
        }
        return "redirect:/client/dashboard";
    }
}