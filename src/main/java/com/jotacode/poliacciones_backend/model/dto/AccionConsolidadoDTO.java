package com.jotacode.poliacciones_backend.model.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccionConsolidadoDTO {
    private String accion;
    private Integer cantidadTotal;
    private Double valorUSD;
    private Double precioCosto;
    private Double porcentajeGananciaPerdida;
    private Double gananciaPerdidaUSD;
}