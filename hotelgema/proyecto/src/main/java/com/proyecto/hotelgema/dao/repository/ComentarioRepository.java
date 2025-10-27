package com.proyecto.hotelgema.dao.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.proyecto.hotelgema.dto.ComentarioDTO;

@Repository
public class ComentarioRepository {

    private final RestTemplate restTemplate;

    @Value("${hotel.gema.comentario.api.url}")
    private String apiUrl;

    public ComentarioRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<ComentarioDTO> obtenerComentariosExternos() {
        ResponseEntity<List<ComentarioDTO>> response = restTemplate.exchange(
                apiUrl + "comentario",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ComentarioDTO>>() {
        }
        );
        return response.getBody();
    }
}
