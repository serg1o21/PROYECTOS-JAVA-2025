package com.proyecto.hotelgema.dao.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categoria")
public class CategoriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nombre;

    private String descripcion;

    private Double precio;

    private String beneficios;

    private String aforo;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] imagen;

    public List<String> getBeneficiosLista() {
        if (beneficios == null || beneficios.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(beneficios.split(","))
                .map(String::trim)
                .toList();
    }
}
