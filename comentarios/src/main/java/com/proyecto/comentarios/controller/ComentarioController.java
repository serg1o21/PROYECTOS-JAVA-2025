package com.proyecto.comentarios.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.comentarios.dao.entity.ComentarioEntity;
import com.proyecto.comentarios.service.ComentarioService;

@RestController
@RequestMapping("/api/comentario")
public class ComentarioController {

    @Autowired
    private ComentarioService comentarioService;

    @GetMapping
    public List<ComentarioEntity> listarComentarios() {
        return comentarioService.listarComentarios();
    }

}
