package com.example.autorent.controller;

import com.example.autorent.config.SecurityConfig;
import com.example.autorent.entity.Categorie;
import com.example.autorent.entity.Vehicule;
import com.example.autorent.security.CustomUserDetailsService;
import com.example.autorent.service.CategorieService;
import com.example.autorent.service.VehiculeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(HomeController.class)
@Import(SecurityConfig.class)          // ← LA VRAIE SOLUTION
@ActiveProfiles("dev")
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Services mockés (requis par HomeController)
    @MockitoBean private VehiculeService          vehiculeService;
    @MockitoBean private CategorieService         categorieService;

    // Requis par SecurityConfig (il injecte CustomUserDetailsService)
    @MockitoBean private CustomUserDetailsService customUserDetailsService;

    // ── Test 1: Page d'accueil — SANS authentification ───────────
    // permitAll() → doit retourner 200 sans aucun @WithMockUser
    @Test
    void home_accessible_sans_authentification() throws Exception {
        when(vehiculeService.findDisponibles()).thenReturn(List.of());
        when(categorieService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())         // 200, pas 401
                .andExpect(view().name("home"));
    }

    // ── Test 2: /home sans authentification ──────────────────────
    @Test
    void home_url_accessible_sans_authentification() throws Exception {
        when(vehiculeService.findDisponibles()).thenReturn(List.of());
        when(categorieService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }

    // ── Test 3: /vehicules sans authentification ─────────────────
    @Test
    void vehicules_accessible_sans_authentification() throws Exception {
        Categorie cat = Categorie.builder().id(1L).nom("SUV").build();
        Vehicule v = Vehicule.builder()
                .id(1L).marque("Toyota").modele("RAV4")
                .prixParJour(new BigDecimal("90.00"))
                .disponible(true).categorie(cat).build();

        when(vehiculeService.findDisponibles()).thenReturn(List.of(v));
        when(categorieService.findAll()).thenReturn(List.of(cat));

        mockMvc.perform(get("/vehicules"))
                .andExpect(status().isOk())
                .andExpect(view().name("vehicules/liste"))
                .andExpect(model().attributeExists("vehicules"))
                .andExpect(model().attributeExists("categories"));
    }

    // ── Test 4: /vehicules/{id} sans authentification ────────────
    @Test
    void vehiculeDetail_accessible_sans_authentification() throws Exception {
        Categorie cat = Categorie.builder().id(1L).nom("Berline").build();
        Vehicule v = Vehicule.builder()
                .id(1L).marque("BMW").modele("Serie3")
                .prixParJour(new BigDecimal("95.00"))
                .disponible(true).categorie(cat).build();

        when(vehiculeService.findById(1L)).thenReturn(v);

        mockMvc.perform(get("/vehicules/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("vehicules/detail"))
                .andExpect(model().attributeExists("vehicule"));
    }

    // ── Test 5: /dashboard redirige selon rôle ───────────────────
    // Cette page requiert authentification → @WithMockUser obligatoire
    @Test
    @WithMockUser(username = "client@test.com", roles = {"CLIENT"})
    void dashboard_redirige_vers_espace_client() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/client/dashboard"));
    }

    // ── Test 6: /admin sans authentification → 302 vers /login ───
    // Vérifier que les pages protégées redirigent bien
    @Test
    void admin_sans_auth_redirige_vers_login() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().is3xxRedirection());
    }
}