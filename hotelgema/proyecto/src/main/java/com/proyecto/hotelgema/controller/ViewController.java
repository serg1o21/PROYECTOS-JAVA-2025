package com.proyecto.hotelgema.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

@Controller
public class ViewController {
    @Autowired
    private CategoriaService categoriaService;

    @GetMapping("/")
    public String mostrarInicio() {
        return "views/index";
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
}
