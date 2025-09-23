package com.proyecto.hotelgema.service;

import java.time.LocalDate;
import java.util.List;

import com.proyecto.hotelgema.dao.entity.HabitacionEntity;

public interface HabitacionService {

    public List<HabitacionEntity> obtenerDisponibles(LocalDate checkIn, LocalDate checkOut, String categoria);

    public List<HabitacionEntity> listarHabitaciones();

    public HabitacionEntity obtenerHabitacionPorId(int id);

    public HabitacionEntity crearHabitacion(HabitacionEntity habitacionEntity);

    public HabitacionEntity actualizarHabitacion(int id, HabitacionEntity habitacionEntity);

    public boolean eliminarHabitacion(int id);
}
