package com.proyecto.hotelgema.dao.repository;

import com.proyecto.hotelgema.dao.entity.ReservaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<ReservaEntity, Integer> {

    List<ReservaEntity> findByIdClienteAndCheckOutAfterAndCheckInBefore(
            String idCliente, LocalDate checkIn, LocalDate checkOut);
    List<ReservaEntity> findByIdCliente(String idcliente);
}
