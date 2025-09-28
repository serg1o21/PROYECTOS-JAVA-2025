package com.proyecto.hotelgema.dao.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.hotelgema.dao.entity.UsuarioEntity;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, String> {
    boolean existsByUsuario(String usuario);
    boolean existsByCorreo(String correo);
    boolean existsByNdoc(String ndoc);
    Optional<UsuarioEntity> findByNdoc(String ndoc);
    Optional<UsuarioEntity> findByUsuario(String usuario);
}
