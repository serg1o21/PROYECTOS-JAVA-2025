package com.proyecto.hotelgema.dao.entity;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "precio")
    private Double precio;
    @Column(name = "beneficios")
    private String beneficios;
    @Column(name = "aforo")
    private int aforo;
    @Column(name = "imagen_url")
    private String imagenUrl;

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
