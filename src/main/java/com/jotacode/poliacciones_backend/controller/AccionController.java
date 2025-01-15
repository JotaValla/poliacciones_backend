package com.jotacode.poliacciones_backend.controller;

import com.jotacode.poliacciones_backend.model.Accion;
import com.jotacode.poliacciones_backend.service.AccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/acciones")
public class AccionController {

    @Autowired
    private AccionService accionService;

    @PostMapping("/comprar")
    public Accion comprarAccion(@RequestBody Accion accion) {
        return accionService.registrarCompra(accion);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Accion> listarAcciones(@PathVariable Long usuarioId) {
        return accionService.obtenerAccionesPorUsuario(usuarioId);
    }

    @GetMapping("/buscar")
    public Map<String, Object> buscarAccion(
            @RequestParam String simbolo,
            @RequestParam String fecha
    ) {
        LocalDate fechaConsulta = LocalDate.parse(fecha);
        LocalDate fechaActual = LocalDate.now();

        if (fechaConsulta.isAfter(fechaActual)) {
            throw new IllegalArgumentException("La fecha no puede ser en el futuro.");
        }

        try {
            Double precio = accionService.obtenerPrecioPorSimboloYFecha(simbolo, fechaConsulta);

            return Map.of(
                    "simbolo", simbolo,
                    "precio", precio,
                    "fecha", fechaConsulta.toString()
            );
        } catch (IllegalArgumentException e) {
            System.out.println("Error específico: " + e.getMessage());
            return Map.of("message", e.getMessage());
        } catch (Exception e) {
            System.out.println("Error general: " + e.getMessage());
            return Map.of("message", "Error interno del servidor.");
        }
    }

    @GetMapping("/{accionId}")
    public Map<String, Object> obtenerAccionPorId(@PathVariable Long accionId) {
        Accion accion = accionService.obtenerAccionPorId(accionId);
        Double valorTotal = accion.getCantidad() * accion.getPrecio();
        return Map.of(
                "cantidad", accion.getCantidad(),
                "fecha", accion.getFecha(),
                "precio", accion.getPrecio(),
                "nombreAccion", accion.getNombreAccion(),
                "valorTotal", valorTotal
        );
    }


    @GetMapping("/ver-ganancia/{accionId}")
    public Map<String, Object> calcularGananciaPerdida(@PathVariable Long accionId) {
        try {
            return accionService.calcularGananciaPerdida(accionId);
        } catch (RuntimeException e) {
            System.out.println("Error al calcular ganancia/pérdida: " + e.getMessage());
            throw new RuntimeException("Error al calcular ganancia/pérdida: " + e.getMessage());
        }
    }
}
