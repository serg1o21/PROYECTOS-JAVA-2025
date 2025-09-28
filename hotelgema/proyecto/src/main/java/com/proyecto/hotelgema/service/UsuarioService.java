package com.proyecto.hotelgema.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.proyecto.hotelgema.dao.entity.UsuarioEntity;

public interface UsuarioService {

    public List<UsuarioEntity> listarUsuarios();

    public UsuarioEntity buscarUsuario(String usuario);

    public UsuarioEntity crearUsuario(UsuarioEntity UsuarioEntity);

    public UsuarioEntity actualizarUsuario(String ndoc, UsuarioEntity usuarioEntity);

    public UsuarioEntity buscarUsuarioPorDoc(String ndoc);

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

}
