package com.proyecto.hotelgema.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proyecto.hotelgema.dao.entity.CategoriaEntity;
import com.proyecto.hotelgema.service.CategoriaService;
import com.proyecto.hotelgema.service.ComentarioService;

@Controller
public class ViewController {

    @Autowired
    private CategoriaService categoriaService;
    @Autowired
    private ComentarioService comentarioService;

    @GetMapping("/")
    public String mostrarInicio() {
        return "views/index";
    }

    @GetMapping("/comentarios")
    public String mostrarComentarios(Model model) {
        model.addAttribute("listaComentarios", comentarioService.obtenerComentarios());
        return "views/client/comentarios";
    }

    @GetMapping("/habitaciones")
    public String mostrarCategoria(@RequestParam(required = false) String nombre, Model model) {

        if (nombre != null && nombre != "") {
            List<CategoriaEntity> list = categoriaService.buscarCategoriasPorNombre(nombre);
            model.addAttribute("listaCategoria", list);
            model.addAttribute("nombre", nombre);
        } else {
            List<CategoriaEntity> list = categoriaService.listarCategorias();
            model.addAttribute("listaCategoria", list);
        }

        return "views/client/categoria";
    }

    @GetMapping("/imagen/{nombre}")
    @ResponseBody
    public ResponseEntity<byte[]> obtenerImagenPorNombre(@PathVariable String nombre) {
        List<CategoriaEntity> lista = categoriaService.buscarCategoriasPorNombre(nombre);

        if (lista.isEmpty() || lista.get(0).getImagen() == null) {
            return ResponseEntity.notFound().build();
        }

        CategoriaEntity categoria = lista.get(0);

        return ResponseEntity.ok()
                .header("Content-Type", "image/png")
                .body(categoria.getImagen());
    }

    @GetMapping("/api/archivos/comprobantes/{nombreArchivo}")
    public ResponseEntity<Resource> servirComprobante(
            @PathVariable String nombreArchivo,
            Authentication authentication) throws IOException {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Path archivoPath = Paths.get("uploads/clientes/comprobantes").resolve(nombreArchivo);

        if (!Files.exists(archivoPath)) {
            return ResponseEntity.notFound().build();
        }

        Resource recurso = new UrlResource(archivoPath.toUri());
        String contentType = Files.probeContentType(archivoPath);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
                .body(recurso);
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "views/security/login";
    }

    @GetMapping("/cliente/dashboard")
    public String clientDashboard() {
        return "views/client/dashboard";
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        return "views/admin/dashboard";
    }

    @GetMapping("/admin/mantenimiento/habitacion")
    public String mantenimientoHabitacion(Model model, Authentication authentication) {
        return "views/admin/habitacion";
    }

    @GetMapping("/admin/mantenimiento/categoria")
    public String mantenimientoCategoria(Model model, Authentication authentication) {
        return "views/admin/categoria";
    }

    @GetMapping("/admin/mantenimiento/usuario")
    public String mantenimientoUsuario(Model model, Authentication authentication) {
        return "views/admin/usuario";
    }

    @GetMapping("/admin/mantenimiento/reserva")
    public String mantenimientoReserva(Model model, Authentication authentication) {
        return "views/admin/reserva";
    }
}
