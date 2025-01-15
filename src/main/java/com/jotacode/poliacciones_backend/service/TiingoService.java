package com.jotacode.poliacciones_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TiingoService {

    private final RestTemplate clienteHttp;

    @Value("${tiingo.api.token}")
    private String apiToken;

    private static final String BASE_URL = "https://api.tiingo.com/tiingo";

    public TiingoService(RestTemplate clienteHttp) {
        this.clienteHttp = clienteHttp;
    }

    public Optional<Double> obtenerPrecioActual(String simbolo) {
        String url = BASE_URL + "/daily/" + simbolo + "/prices?token=" + apiToken;

        try {
            ResponseEntity<List<Map<String, Object>>> response = clienteHttp.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

            List<Map<String, Object>> precios = response.getBody();
            if (precios != null && !precios.isEmpty()) {
                return Optional.of(Double.parseDouble(precios.get(0).get("close").toString()));
            }
        } catch (Exception e) {
            System.out.println("Error al obtener precio actual: " + e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Double> obtenerPrecioPorFecha(String simbolo, LocalDate fecha) {
        String url = BASE_URL + "/daily/" + simbolo + "/prices?startDate=" + fecha + "&endDate=" + fecha + "&token=" + apiToken;

        try {
            Map<String, Object>[] respuesta = clienteHttp.getForObject(url, Map[].class);
            if (respuesta != null && respuesta.length > 0) {
                return Optional.of(Double.parseDouble(respuesta[0].get("close").toString()));
            }
        } catch (Exception e) {
            System.out.println("Error al obtener precio por fecha: " + e.getMessage());
        }
        return Optional.empty();
    }

    public boolean verificarSimbolo(String simbolo) {
        String url = BASE_URL + "/daily/" + simbolo + "?token=" + apiToken;
        try {
            Map<String, Object> respuesta = clienteHttp.getForObject(url, Map.class);
            return respuesta != null && respuesta.containsKey("ticker") && respuesta.get("ticker").equals(simbolo);
        } catch (Exception e) {
            System.out.println("Error al verificar el símbolo: " + e.getMessage());
            return false;
        }
    }
}
