package com.proyecto.hotelgema.service.impl;

import com.proyecto.hotelgema.dao.entity.UsuarioEntity;
import com.proyecto.hotelgema.dao.repository.UsuarioRepository;
import com.proyecto.hotelgema.service.UsuarioService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService, UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
            BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void crearUsuario(UsuarioEntity usuarioEntity) {
        if (usuarioRepository.existsByNdoc(usuarioEntity.getNdoc()))
            throw new IllegalArgumentException("El número de documento ya está registrado");
        if (usuarioRepository.existsByUsuario(usuarioEntity.getUsuario()))
            throw new IllegalArgumentException("El usuario ya existe");
        if (usuarioRepository.existsByCorreo(usuarioEntity.getCorreo()))
            throw new IllegalArgumentException("El correo ya está registrado");

        usuarioEntity.setClave(passwordEncoder.encode(usuarioEntity.getClave()));
        // Si no se envía un rol explícito, asigna uno por defecto
        usuarioEntity.setRol("CLIENTE");
        usuarioEntity.setEstado(true);
        usuarioRepository.save(usuarioEntity);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsuarioEntity usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
                
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toUpperCase());

        return new org.springframework.security.core.userdetails.User(
                usuario.getUsuario(),
                usuario.getClave(),
                usuario.getEstado() != null && usuario.getEstado() ? List.of(authority) : List.of());
    }

    @Override
    public UsuarioEntity buscarUsuario(String usuario) {
        return usuarioRepository.findByUsuario(usuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

}
