package com.proyecto.hotelgema.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.hotelgema.dao.repository.ComentarioRepository;
import com.proyecto.hotelgema.dto.ComentarioDTO;
import com.proyecto.hotelgema.service.ComentarioService;

@Service
public class ComentarioServiceImpl implements ComentarioService{

    @Autowired
    private ComentarioRepository comentarioRepository;
    @Override
    public List<ComentarioDTO> obtenerComentarios() {
        return comentarioRepository.obtenerComentariosExternos();
    }

}
