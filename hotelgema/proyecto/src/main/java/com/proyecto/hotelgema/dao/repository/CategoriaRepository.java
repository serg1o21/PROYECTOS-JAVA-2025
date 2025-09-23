package com.proyecto.hotelgema.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.hotelgema.dao.entity.CategoriaEntity;

public interface CategoriaRepository extends JpaRepository<CategoriaEntity, Integer>{

    public List<CategoriaEntity> findByNombre(String nombre);
    
}
