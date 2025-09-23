package com.proyecto.hotelgema.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.proyecto.hotelgema.dao.entity.UsuarioEntity;

public interface UsuarioService {

    public UsuarioEntity buscarUsuario(String usuario);

    public void crearUsuario(UsuarioEntity UsuarioEntity);

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

}
