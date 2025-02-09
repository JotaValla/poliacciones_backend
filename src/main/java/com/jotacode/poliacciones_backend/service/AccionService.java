package com.jotacode.poliacciones_backend.service;

import com.jotacode.poliacciones_backend.model.Accion;
import com.jotacode.poliacciones_backend.model.Usuario;
import com.jotacode.poliacciones_backend.model.dto.AccionConsolidadoDTO;
import com.jotacode.poliacciones_backend.repository.AccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
        Usuario usuario = usuarioService.obtenerUsuarioPorId(accion.getUsuario().getCedula());
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

    public Page<Accion> obtenerAccionesPorUsuario(String idUsuario, Pageable pageable) {
        return accionRepository.findByUsuarioCedula(idUsuario, pageable);
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

    public List<AccionConsolidadoDTO> obtenerConsolidadoAcciones(String usuarioId) {
        List<Accion> acciones = accionRepository.findByUsuarioCedula(usuarioId)
                .orElse(Collections.emptyList());

        // Agrupar acciones por símbolo
        Map<String, List<Accion>> accionesPorSimbolo = acciones.stream()
                .collect(Collectors.groupingBy(Accion::getNombreAccion));

        return accionesPorSimbolo.entrySet().stream()
                .map(entry -> {
                    String simbolo = entry.getKey();
                    List<Accion> accionesDelSimbolo = entry.getValue();

                    // Calcular totales
                    int cantidadTotal = accionesDelSimbolo.stream()
                            .mapToInt(Accion::getCantidad)
                            .sum();

                    double valorTotalUSD = accionesDelSimbolo.stream()
                            .mapToDouble(a -> a.getPrecio() * a.getCantidad())
                            .sum();

                    // Calcular precio de costo promedio
                    double precioCosto = valorTotalUSD / cantidadTotal;

                    // Obtener precio actual y calcular ganancia/pérdida
                    Double precioActual = tiingoService.obtenerPrecioActual(simbolo)
                            .orElseThrow(() -> new RuntimeException("No se pudo obtener el precio actual para " + simbolo));

                    double valorActualTotal = cantidadTotal * precioActual;
                    double gananciaPerdidaUSD = valorActualTotal - valorTotalUSD;
                    double porcentajeGananciaPerdida = (gananciaPerdidaUSD / valorTotalUSD) * 100;

                    return new AccionConsolidadoDTO(
                            simbolo,
                            cantidadTotal,
                            valorTotalUSD,
                            precioCosto,
                            porcentajeGananciaPerdida,
                            gananciaPerdidaUSD
                    );
                })
                .collect(Collectors.toList());
    }


}
