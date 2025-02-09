package com.jotacode.poliacciones_backend.repository;


import com.jotacode.poliacciones_backend.model.Accion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccionRepository extends JpaRepository<Accion, Long> {
    Optional<List<Accion>> findByUsuarioCedula(String cedula);

    Page<Accion> findByUsuarioCedula(String usuarioId, Pageable pageable);

}
