package com.proyecto.hotelgema.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.proyecto.hotelgema.dao.entity.CategoriaEntity;
import com.proyecto.hotelgema.service.CategoriaService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/categoria")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> listarCategorias(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("message", "No autenticado");
            return ResponseEntity.status(401).body(response);
        }

        try {
            List<CategoriaEntity> lista = categoriaService.listarCategorias();
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
    public ResponseEntity<Map<String, Object>> obtenerCategoria(@PathVariable int id, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("message", "No autenticado");
            return ResponseEntity.status(401).body(response);
        }

        try {
            CategoriaEntity categoria = categoriaService.obtenerCategoriaPorId(id);
            if (categoria == null) {
                response.put("success", false);
                response.put("message", "Categoría no encontrada");
                return ResponseEntity.status(404).body(response);
            }
            response.put("success", true);
            response.put("data", categoria);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error interno del servidor");
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearCategoria(
            @RequestBody CategoriaEntity categoria,
            Authentication authentication) {

        Map<String, Object> response = new HashMap<>();
        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("message", "No autenticado");
            return ResponseEntity.status(401).body(response);
        }

        try {
            if (categoria.getNombre() == null || categoria.getNombre().isBlank()) {
                response.put("success", false);
                response.put("message", "El nombre es obligatorio");
                return ResponseEntity.badRequest().body(response);
            }
            if (categoria.getPrecio() == null || categoria.getPrecio() < 0) {
                response.put("success", false);
                response.put("message", "El precio debe ser mayor o igual a 0");
                return ResponseEntity.badRequest().body(response);
            }

            CategoriaEntity nuevaCategoria = categoriaService.crearCategoria(categoria);
            response.put("success", true);
            response.put("data", nuevaCategoria);
            response.put("message", "Categoría creada correctamente");
            return ResponseEntity.status(201).body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error interno del servidor");
            return ResponseEntity.status(500).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarCategoria(
            @PathVariable int id,
            @RequestBody CategoriaEntity categoria,
            Authentication authentication) {

        Map<String, Object> response = new HashMap<>();
        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("message", "No autenticado");
            return ResponseEntity.status(401).body(response);
        }

        try {
            CategoriaEntity actualizada = categoriaService.actualizarCategoria(id, categoria);
            if (actualizada == null) {
                response.put("success", false);
                response.put("message", "Categoría no encontrada");
                return ResponseEntity.status(404).body(response);
            }
            response.put("success", true);
            response.put("data", actualizada);
            response.put("message", "Categoría actualizada correctamente");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error interno del servidor");
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminarCategoria(
            @PathVariable int id,
            Authentication authentication) {

        Map<String, Object> response = new HashMap<>();
        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("message", "No autenticado");
            return ResponseEntity.status(401).body(response);
        }

        try {
            boolean eliminado = categoriaService.eliminarCategoria(id);
            if (!eliminado) {
                response.put("success", false);
                response.put("message", "Categoría no encontrada");
                return ResponseEntity.status(404).body(response);
            }
            response.put("success", true);
            response.put("message", "Categoría eliminada correctamente");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error interno del servidor");
            return ResponseEntity.status(500).body(response);
        }
    }
}
