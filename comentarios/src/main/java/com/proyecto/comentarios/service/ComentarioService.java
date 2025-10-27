package com.proyecto.comentarios.service;

import java.util.List;

import com.proyecto.comentarios.dao.entity.ComentarioEntity;

public interface ComentarioService {

    public List<ComentarioEntity> listarComentarios();
}
