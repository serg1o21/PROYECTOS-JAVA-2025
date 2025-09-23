package com.proyecto.hotelgema.service;

import java.util.List;

import com.proyecto.hotelgema.dao.entity.ReservaEntity;

public interface ReservaService {
    ReservaEntity crearReserva(ReservaEntity reserva, String username);


    List<ReservaEntity> listarReservas(String username);
}
