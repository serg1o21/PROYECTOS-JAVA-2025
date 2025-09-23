package com.proyecto.hotelgema.dao.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "detalle_reserva")
public class DetalleReservaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer idDetalle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reserva", nullable = false)
    @JsonBackReference // evita serializar la reserva para romper el ciclo - hijo
    private ReservaEntity reserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_habitacion", nullable = false)
    private HabitacionEntity habitacion;

    @Column(name = "precio_noche", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioNoche;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "fec_creacion", insertable = false, updatable = false)
    private LocalDateTime fecCreacion;

    @Column(name = "fec_actualizacion", insertable = false, updatable = false)
    private LocalDateTime fecActualizacion;
}
