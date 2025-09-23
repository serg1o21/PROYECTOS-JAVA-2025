package com.proyecto.hotelgema.dao.repository;

import com.proyecto.hotelgema.dao.entity.DetalleReservaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleReservaRepository extends JpaRepository<DetalleReservaEntity, Integer> {
}
