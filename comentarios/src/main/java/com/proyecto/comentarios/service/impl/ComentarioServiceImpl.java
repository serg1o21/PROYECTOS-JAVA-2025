package com.proyecto.comentarios.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.comentarios.dao.entity.ComentarioEntity;
import com.proyecto.comentarios.dao.repository.ComentarioRepository;
import com.proyecto.comentarios.service.ComentarioService;

@Service
public class ComentarioServiceImpl implements ComentarioService{

    @Autowired
    private ComentarioRepository comentarioRepository;
    @Override
    public List<ComentarioEntity> listarComentarios() {
        return comentarioRepository.findAll();
    }

}
