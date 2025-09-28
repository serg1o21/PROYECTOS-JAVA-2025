package com.proyecto.hotelgema.dao.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "reserva")
public class ReservaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Integer idReserva;

    @Column(name = "id_cliente", nullable = false, length = 20)
    private String idCliente; // Relación a Usuario (puedes hacer ManyToOne si tienes UsuarioEntity)

    @Column(name = "fecha_reserva", insertable = false, updatable = false)
    private LocalDateTime fechaReserva;

    @Column(name = "check_in", nullable = false)
    private LocalDate checkIn;

    @Column(name = "check_out", nullable = false)
    private LocalDate checkOut;

    @Column(name = "estado")
    private String estado;

    @Column(name = "fec_creacion", insertable = false, updatable = false)
    private LocalDateTime fecCreacion;

    @Column(name = "fec_actualizacion", insertable = false, updatable = false)
    private LocalDateTime fecActualizacion;

  
   @Column(name = "url_comprobante")
    private String urlComprobante;

    @JsonManagedReference
    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleReservaEntity> detalles;

}
