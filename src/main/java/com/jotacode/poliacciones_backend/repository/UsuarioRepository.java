package com.jotacode.poliacciones_backend.repository;


import com.jotacode.poliacciones_backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    Optional<Object> findByUsername(String username);
}
