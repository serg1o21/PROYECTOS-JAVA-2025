package com.proyecto.hotelgema.dao.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.proyecto.hotelgema.dao.entity.HabitacionEntity;

public interface HabitacionRepository extends JpaRepository<HabitacionEntity, Integer> {

    @Query("""
            SELECT h
            FROM HabitacionEntity h
            JOIN h.categoria c
            WHERE h.estado = 'disponible'
            AND c.nombre = :categoriaNombre
            AND h.idHabitacion NOT IN (
                SELECT dr.habitacion.idHabitacion
                FROM DetalleReservaEntity dr
                JOIN dr.reserva r
                WHERE r.estado = 'confirmada'
                AND :checkIn < r.checkOut
                AND :checkOut > r.checkIn
            )
            """)
    List<HabitacionEntity> buscarHabitacionesDisponibles(
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut,
            @Param("categoriaNombre") String nombre);

}
