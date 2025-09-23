package com.proyecto.hotelgema.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.proyecto.hotelgema.dao.entity.HabitacionEntity;
import com.proyecto.hotelgema.dao.repository.HabitacionRepository;
import com.proyecto.hotelgema.service.HabitacionService;

@Service
public class HabitacionServiceImpl implements HabitacionService {



    // inyectamos la dependencia del repositorio
    @Autowired
    private HabitacionRepository habitacionRepository;


    public List<HabitacionEntity> obtenerDisponibles(LocalDate checkIn, LocalDate checkOut, String categoria) {
        if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Fechas inválidas");
        }
        return habitacionRepository.buscarHabitacionesDisponibles(checkIn, checkOut, categoria);
    }

    @Override
    public List<HabitacionEntity> listarHabitaciones() {
        return habitacionRepository.findAll();
    }

    @Override
    public HabitacionEntity crearHabitacion(HabitacionEntity habitacionEntity) {
        return habitacionRepository.save(habitacionEntity);
    }

    @Override
    public HabitacionEntity actualizarHabitacion(int id, HabitacionEntity habitacionEntity) {
        habitacionEntity.setIdHabitacion(id);
        return habitacionRepository.save(habitacionEntity);
    }

    @Override
    public boolean eliminarHabitacion(int id) {
        if (!habitacionRepository.existsById(id)) {
            return false;
        }
        habitacionRepository.deleteById(id);
        return true;
    }

    @Override
    public HabitacionEntity obtenerHabitacionPorId(int id) {
        return habitacionRepository.findById(id).orElse(null);
    }

}
