package com.proyecto.hotelgema.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.hotelgema.dao.entity.CategoriaEntity;
import com.proyecto.hotelgema.dao.entity.HabitacionEntity;
import com.proyecto.hotelgema.service.CategoriaService;
import com.proyecto.hotelgema.service.HabitacionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/habitacion")
public class HabitacionController {

    @Autowired
    private HabitacionService habitacionService;
    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> listarHabitaciones() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<HabitacionEntity> lista = habitacionService.listarHabitaciones();
            response.put("success", true);
            response.put("data", lista);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error interno del servidor");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerHabitacion(@PathVariable int id, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("message", "No autenticado");
            return ResponseEntity.status(401).body(response);
        }

        try {
            HabitacionEntity habitacion = habitacionService.obtenerHabitacionPorId(id);

            if (habitacion == null) {
                response.put("success", false);
                response.put("message", "Habitación no encontrada");
                return ResponseEntity.status(404).body(response);
            }
            response.put("success", true);
            response.put("data", habitacion);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error interno del servidor");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/disponibles")
    public List<HabitacionEntity> listarHabitacionesDisponible(
            @RequestParam String checkin,
            @RequestParam String checkout,
            @RequestParam(required = false) String categoria) {

        LocalDate fechaCheckin = LocalDate.parse(checkin);
        LocalDate fechaCheckout = LocalDate.parse(checkout);

        return habitacionService.obtenerDisponibles(
                fechaCheckin,
                fechaCheckout,
                categoria != null && !categoria.isEmpty() ? categoria : null);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearHabitacion(
            @RequestBody HabitacionEntity habitacion,
            Authentication authentication) {
        Map<String, Object> response = new HashMap<>();

        // 1. Validar autenticación
        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("success", false);
            response.put("message", "No autenticado");
            return ResponseEntity.status(401).body(response);
        }

        try {
            // 2. Validaciones de datos
            if (habitacion.getNumero() == null || habitacion.getNumero().isBlank()) {
                response.put("success", false);
                response.put("message", "El número de habitación es obligatorio");
                return ResponseEntity.badRequest().body(response);
            }

            if (habitacion.getCapacidad() == null || habitacion.getCapacidad() <= 0) {
                response.put("success", false);
                response.put("message", "La capacidad debe ser mayor que 0");
                return ResponseEntity.badRequest().body(response);
            }

            if (habitacion.getPrecioNoche() == null || habitacion.getPrecioNoche().compareTo(BigDecimal.ZERO) < 0) {
                response.put("success", false);
                response.put("message", "El precio debe ser mayor o igual a 0");
                return ResponseEntity.badRequest().body(response);
            }

            // 3. Validar que la categoría exista
            if (habitacion.getCategoria() == null || habitacion.getCategoria().getId() == 0) {
                response.put("success", false);
                response.put("message", "Debe seleccionar una categoría");
                return ResponseEntity.badRequest().body(response);
            }

            CategoriaEntity categoriaEntity = categoriaService.obtenerCategoriaPorId(habitacion.getCategoria().getId());
            if (categoriaEntity == null) {
                response.put("success", false);
                response.put("message", "La categoría seleccionada no existe");
                return ResponseEntity.badRequest().body(response);
            }

            // 4. Reemplazar la categoría parcial por la entidad administrada
            habitacion.setCategoria(categoriaEntity);

            // 5. Guardar habitación
            HabitacionEntity nuevaHabitacion = habitacionService.crearHabitacion(habitacion);

            response.put("success", true);
            response.put("data", nuevaHabitacion);
            response.put("message", "Habitación creada correctamente");
            return ResponseEntity.status(201).body(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error interno del servidor");
            return ResponseEntity.status(500).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarHabitacion(@PathVariable int id,
            @RequestBody HabitacionEntity habitacionEntity, Authentication authentication) {

        Map<String, Object> response = new HashMap<>();
        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("message", "No autenticado");
            return ResponseEntity.status(401).body(response);
        }
        try {
            HabitacionEntity actualizada = habitacionService.actualizarHabitacion(id, habitacionEntity);
            if (actualizada == null) {
                response.put("success", false);
                response.put("message", "Habitación no encontrada");
                return ResponseEntity.status(404).body(response);
            }
            response.put("success", true);
            response.put("data", actualizada);
            response.put("message", "Habitación actualizada correctamente");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error interno del servidor");
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminarHabitacion(
            @PathVariable int id,
            Authentication authentication) {

        Map<String, Object> response = new HashMap<>();
        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("message", "No autenticado");
            return ResponseEntity.status(401).body(response);
        }

        try {
            boolean eliminado = habitacionService.eliminarHabitacion(id);
            if (!eliminado) {
                response.put("success", false);
                response.put("message", "Habitación no encontrada");
                return ResponseEntity.status(404).body(response);
            }
            response.put("success", true);
            response.put("message", "Habitación eliminada correctamente");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error interno del servidor");
            return ResponseEntity.status(500).body(response);
        }
    }

}
