package com.jotacode.poliacciones_backend.controller;

import com.jotacode.poliacciones_backend.model.Usuario;
import com.jotacode.poliacciones_backend.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public Usuario crearUsuario(@Valid @RequestBody Usuario usuario) {
        return usuarioService.crearUsuario(usuario);
    }

    @GetMapping
    public List<Usuario> obtenerUsuarios() {
        return usuarioService.obtenerUsuarios();
    }

    @GetMapping("/{idUsuario}")
    public Usuario obtenerUsuarioPorId(@PathVariable Long idUsuario) {
        return usuarioService.obtenerUsuarioPorId(idUsuario);
    }
}
