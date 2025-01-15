package com.jotacode.poliacciones_backend.service;


import com.jotacode.poliacciones_backend.model.Usuario;
import com.jotacode.poliacciones_backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario crearUsuario(Usuario usuario){
        validarUsuarioACrear(usuario);
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> obtenerUsuarios(){
        return usuarioRepository.findAll();
    }

    public Usuario obtenerUsuarioPorId(Long id){
        return usuarioRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id));
    }

    public void validarUsuarioACrear(Usuario usuario){
        if (usuario.getNombre() == null || usuario.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre del usuario es requerido.");
        }

    }

}
