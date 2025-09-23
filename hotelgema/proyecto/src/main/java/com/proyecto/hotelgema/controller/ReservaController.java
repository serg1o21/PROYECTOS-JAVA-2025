package com.proyecto.hotelgema.controller;

import com.proyecto.hotelgema.dao.entity.ReservaEntity;
import com.proyecto.hotelgema.service.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reserva")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearReserva(
            @RequestBody ReservaEntity reserva,
            Authentication authentication) {
        Map<String, Object> response = new HashMap<>();

        if (authentication == null || !authentication.isAuthenticated()) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "No autenticado");
            return ResponseEntity.status(401).body(error);
        }

        try {
            reservaService.crearReserva(reserva, authentication.getName());
            response.put("success", true);
            response.put("message", "Reserva creada correctamente");
            return ResponseEntity.status(201).body(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error interno del servidor");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> listarReserva(Authentication authentication) {
        
        Map<String, Object> response = new HashMap<>();
        if (authentication == null || !authentication.isAuthenticated()) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "No autenticado");
            return ResponseEntity.status(401).body(error);
        }

        try {
            List<ReservaEntity> lista =reservaService.listarReservas(authentication.getName());
            response.put("success", true);
            response.put("data", lista);
            return ResponseEntity.status(201).body(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error interno del servidor");
            return ResponseEntity.status(500).body(response);
        }
     
    }

}
