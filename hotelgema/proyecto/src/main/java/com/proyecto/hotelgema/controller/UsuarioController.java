package com.proyecto.hotelgema.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.hotelgema.dao.entity.UsuarioEntity;
import com.proyecto.hotelgema.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    //inyectamos la dependencia
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> listarUsuarios() {
        Map<String, Object> response = new HashMap<>();
        List<UsuarioEntity> lista = usuarioService.listarUsuarios();
        response.put("success", true);
        response.put("data", lista);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerUsuario(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        UsuarioEntity usuario = usuarioService.buscarUsuarioPorDoc(id);
        if (usuario == null) {
            response.put("success", false);
            response.put("message", "Categoría no encontrada");
            return ResponseEntity.status(404).body(response);
        }
        response.put("success", true);
        response.put("data", usuario);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearUsuario(@RequestBody @Valid UsuarioEntity usuario) {
        Map<String, Object> response = new HashMap<>();
        usuarioService.crearUsuario(usuario);
        response.put("success", true);
        response.put("message", "Usuario registrado correctamente");
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{ndoc}")
    public ResponseEntity<Map<String, Object>> actualizarUsuario(@PathVariable String ndoc, @RequestBody UsuarioEntity usuario) {
        Map<String, Object> response = new HashMap<>();
        UsuarioEntity actualizada = usuarioService.actualizarUsuario(ndoc, usuario);
        response.put("success", true);
        response.put("data", actualizada);
        response.put("message", "Habitación actualizada correctamente");
        return ResponseEntity.ok(response);
    }
}
