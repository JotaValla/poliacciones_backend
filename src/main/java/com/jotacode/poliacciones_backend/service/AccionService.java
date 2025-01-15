package com.jotacode.poliacciones_backend.service;

import com.jotacode.poliacciones_backend.model.Accion;
import com.jotacode.poliacciones_backend.model.Usuario;
import com.jotacode.poliacciones_backend.repository.AccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class AccionService {

    @Autowired
    private AccionRepository accionRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private TiingoService tiingoService;

    public Accion registrarCompra(Accion accion) {
        // Validar símbolo de acción
        if (!tiingoService.verificarSimbolo(accion.getNombreAccion())) {
            throw new IllegalArgumentException("El símbolo de la acción no es válido: " + accion.getNombreAccion());
        }

        // Validar usuario
        Usuario usuario = usuarioService.obtenerUsuarioPorId(accion.getUsuario().getIdUsuario());
        accion.setUsuario(usuario);

        // Validar fecha
        if (accion.getFecha() == null || accion.getFecha().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha no puede ser futura y debe ser válida.");
        }

        if (accion.getFecha().getDayOfWeek() == DayOfWeek.SATURDAY || accion.getFecha().getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new IllegalArgumentException("El mercado cierra los sábados y domingos.");
        }

        // Validar precio
        if (accion.getPrecio() == null || accion.getPrecio() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0 y no puede ser nulo.");
        }

        return accionRepository.save(accion);
    }

    public Accion obtenerAccionPorId(Long accionId) {
        return accionRepository.findById(accionId)
                .orElseThrow(() -> new IllegalArgumentException("Acción no encontrada con ID: " + accionId));
    }

    public List<Accion> obtenerAccionesPorUsuario(Long usuarioId) {
        return accionRepository.findByUsuarioIdUsuario(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontraron acciones para el usuario con ID: " + usuarioId));
    }

    public Double obtenerPrecioPorSimboloYFecha(String simbolo, LocalDate fecha) {
        return tiingoService.obtenerPrecioPorFecha(simbolo, fecha)
                .orElseThrow(() -> new IllegalArgumentException("No se pudo obtener el precio para el símbolo " + simbolo + " en la fecha " + fecha));
    }

    public Map<String, Object> calcularGananciaPerdida(Long accionId) {
        Accion accion = obtenerAccionPorId(accionId);
        Double precioActual = tiingoService.obtenerPrecioActual(accion.getNombreAccion())
                .orElseThrow(() -> new RuntimeException("No se pudo obtener el precio actual para la acción " + accion.getNombreAccion()));

        Double valorTotal = accion.getCantidad() * accion.getPrecio();
        Double valorActualTotal = accion.getCantidad() * precioActual;
        Double gananciaTotal = Math.max(valorActualTotal - valorTotal, 0);
        Double perdidaTotal = Math.max(valorTotal - valorActualTotal, 0);

        Double porcentajeGanancia = gananciaTotal > 0 ? (gananciaTotal / valorTotal) * 100 : 0;
        Double porcentajePerdida = perdidaTotal > 0 ? (perdidaTotal / valorTotal) * 100 : 0;

        return Map.of(
                "gananciaTotal", gananciaTotal,
                "perdidaTotal", perdidaTotal,
                "porcentajeGanancia", porcentajeGanancia,
                "porcentajePerdida", porcentajePerdida,
                "valorActual", precioActual,
                "fechaActual", LocalDate.now().toString()
        );
    }

}
