package com.proyecto.hotelgema.service;

import java.util.List;

import com.proyecto.hotelgema.dao.entity.CategoriaEntity;

public interface CategoriaService {

    public List<CategoriaEntity> listarCategorias();
    public List<CategoriaEntity> buscarCategoriasPorNombre(String nombre);
    public CategoriaEntity obtenerCategoriaPorId(int id);

    public CategoriaEntity crearCategoria(CategoriaEntity categoriaEntity);
    public CategoriaEntity actualizarCategoria(int id, CategoriaEntity categoriaEntity);
    public boolean eliminarCategoria(int id);

}
