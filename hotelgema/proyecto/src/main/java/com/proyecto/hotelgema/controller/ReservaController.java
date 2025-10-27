package com.proyecto.hotelgema.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.hotelgema.dao.entity.ReservaEntity;
import com.proyecto.hotelgema.service.ReservaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reserva")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Map<String, Object>> crearReserva(
            @RequestPart("reserva") String reservaJson,
            @RequestPart(value = "comprobante", required = false) MultipartFile comprobante,
            Authentication authentication) {

        Map<String, Object> response = new HashMap<>();

        System.out.println("📌 Entramos al endpoint crearReserva");

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "No autenticado"));
        }

        try {
            // Mostrar JSON recibido
            System.out.println("📥 JSON recibido: " + reservaJson);

            ReservaEntity reserva = objectMapper.readValue(reservaJson, ReservaEntity.class);

            if (comprobante != null && !comprobante.isEmpty()) {
                System.out.println("📎 Archivo recibido: " + comprobante.getOriginalFilename()
                        + ", tipo: " + comprobante.getContentType()
                        + ", tamaño: " + comprobante.getSize());

                if (!comprobante.getContentType().startsWith("image/")) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("success", false, "message", "El archivo debe ser una imagen válida."));
                }

                // Carpeta absoluta para guardar comprobantes
                Path carpeta = Paths.get(System.getProperty("user.dir"), "uploads", "clientes", "comprobantes");
                if (!Files.exists(carpeta)) {
                    Files.createDirectories(carpeta);
                }

                String extension = getExtension(comprobante.getOriginalFilename());
                String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
                String usuario = authentication.getName();
                String nombreArchivo = "comprobante-" + fechaActual + "-" + usuario
                        + (extension.isEmpty() ? "" : "." + extension);

                Path destino = carpeta.resolve(nombreArchivo);
                Files.copy(comprobante.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

                // Guardar URL en la reserva
                reserva.setUrlComprobante("/api/archivos/comprobantes/" + nombreArchivo);

                // Devolver nombre de archivo para debug
                response.put("archivoGuardado", nombreArchivo);
            } else {
                System.out.println("📎 No se envió comprobante");
            }

            // Guardar reserva
            reservaService.crearReserva(reserva, authentication.getName());

            response.put("success", true);
            response.put("message", "Reserva creada correctamente");
            response.put("reservaRecibida", reserva); // Devuelve el objeto parseado para debug

            return ResponseEntity.status(201).body(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage(), "reservaJson", reservaJson));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("success", false, "message", "Error interno del servidor", "reservaJson", reservaJson));
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
            // ✅ Obtener solo un rol
            String rol = authentication.getAuthorities().stream()
                    .findFirst()
                    .map(auth -> auth.getAuthority())
                    .orElse("ROLE_INVALID");

            List<ReservaEntity> lista;
            if (rol.equals("ROLE_ADMIN")) {
                lista = reservaService.listarReservas(rol);
            } else {
                lista = reservaService.listarReservas(authentication.getName());
            }

            response.put("success", true);
            response.put("data", lista);
            return ResponseEntity.ok(response);

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

    private String getExtension(String nombreArchivo) {
        if (nombreArchivo == null || !nombreArchivo.contains(".")) {
            return "";
        }
        return nombreArchivo.substring(nombreArchivo.lastIndexOf('.') + 1);
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Map<String, Object>> actualizarEstado(
            @PathVariable int id,
            @RequestBody Map<String, String> body) {

        Map<String, Object> response = new HashMap<>();
        try {
            String nuevoEstado = body.get("estado");
            List<String> estadosPermitidos = List.of("PENDIENTE", "CANCELADA", "CONFIRMADA");
            if (nuevoEstado == null || !estadosPermitidos.contains(nuevoEstado.toUpperCase())) {
                response.put("success", false);
                response.put("message", "Estado inválido. Solo se permite: PENDIENTE, CANCELADA o CONFIRMADA");
                return ResponseEntity.badRequest().body(response);
            }
            reservaService.actualizarEstado(id, nuevoEstado.toUpperCase());
            response.put("success", true);
            response.put("message", "Estado actualizado");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al actualizar estado");
            return ResponseEntity.status(500).body(response);
        }
    }

}
