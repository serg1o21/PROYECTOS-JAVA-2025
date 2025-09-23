package com.proyecto.hotelgema.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.hotelgema.dao.entity.CategoriaEntity;
import com.proyecto.hotelgema.dao.repository.CategoriaRepository;
import com.proyecto.hotelgema.service.CategoriaService;

@Service
public class CategoriaServiceImpl implements CategoriaService {
    // inyectamos la dependencia del repository
    @Autowired
    private CategoriaRepository categoriaRepository;

    @Override
    public List<CategoriaEntity> listarCategorias() {

        return categoriaRepository.findAll();

    }

    @Override
    public List<CategoriaEntity> buscarCategoriasPorNombre(String nombre) {
        return categoriaRepository.findByNombre(nombre);
    }

    @Override
    public CategoriaEntity obtenerCategoriaPorId(int id) {
        return categoriaRepository.findById(id).orElse(null);
    }

    @Override
    public CategoriaEntity crearCategoria(CategoriaEntity categoriaEntity) {
        return categoriaRepository.save(categoriaEntity);
    }

    @Override
    public CategoriaEntity actualizarCategoria(int id, CategoriaEntity categoriaEntity) {
        categoriaEntity.setId(id);
        return categoriaRepository.save(categoriaEntity);
    }

    @Override
    public boolean eliminarCategoria(int id) {
        if (!categoriaRepository.existsById(id)) {
            return false;
        }
        categoriaRepository.deleteById(id);
        return true; 
    }

}
