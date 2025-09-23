package com.proyecto.hotelgema.service.impl;

import com.proyecto.hotelgema.dao.entity.ReservaEntity;
import com.proyecto.hotelgema.dao.entity.DetalleReservaEntity;
import com.proyecto.hotelgema.dao.entity.HabitacionEntity;
import com.proyecto.hotelgema.dao.entity.UsuarioEntity;
import com.proyecto.hotelgema.dao.repository.ReservaRepository;
import com.proyecto.hotelgema.dao.repository.HabitacionRepository;
import com.proyecto.hotelgema.dao.repository.UsuarioRepository;
import com.proyecto.hotelgema.service.ReservaService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final HabitacionRepository habitacionRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    @Override
    public ReservaEntity crearReserva(ReservaEntity reserva, String username) {
        // validamos al usuario autenticado y lo buscamos en nuestro repositorio de
        // usuario
        UsuarioEntity usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + username));
        // en la entidad de la reserva vamos a setear su valor por el numero de
        // identidad del usuario logeado
        reserva.setIdCliente(usuario.getNdoc());
        // estara como pendiente en un inicio, porque falta validar el pago por la
        // captura enviada
        reserva.setEstado("PENDIENTE");

        // validamos las fechas checkin y checkout
        if (reserva.getCheckIn() == null || reserva.getCheckOut() == null) {
            throw new IllegalArgumentException("Debe indicar fecha de check-in y check-out");
        }
        if (!reserva.getCheckOut().isAfter(reserva.getCheckIn())) {
            throw new IllegalArgumentException("La fecha de check-out debe ser posterior al check-in");
        }

        // calculamos las noches por el precio segun tipo de habitación
        long noches = ChronoUnit.DAYS.between(reserva.getCheckIn(), reserva.getCheckOut());
        if (noches <= 0)
            noches = 1;

        // verificamos que nuestro detalle de la reserva no este como nulo
        if (reserva.getDetalles() == null || reserva.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos una habitación");
        }
        // de nuestra entidad detalle reserva, hacemos un recorrido para guardar las
        // habitaciones, su subtotal, etc
        for (DetalleReservaEntity detalleReservaEntity : reserva.getDetalles()) {
            // guardamos los ids enviados desde el front
            Integer idHabitacion = detalleReservaEntity.getHabitacion().getIdHabitacion();
            // buscamos uno x uno las habitaciones
            HabitacionEntity habitacion = habitacionRepository.findById(idHabitacion)
                    .orElseThrow(
                            () -> new IllegalArgumentException("La habitación con ID " + idHabitacion + " no existe"));

            detalleReservaEntity.setReserva(reserva);
            detalleReservaEntity.setHabitacion(habitacion);

            // reasignamos los precioxnoche y subtotal
            BigDecimal precioNoche = habitacion.getPrecioNoche();
            detalleReservaEntity.setPrecioNoche(precioNoche);
            BigDecimal subtotal = precioNoche.multiply(BigDecimal.valueOf(noches));
            detalleReservaEntity.setSubtotal(subtotal);
        }

        // luego de cumplir con todo, guardamos la reserva
        return reservaRepository.save(reserva);
    }

    @Override
    public List<ReservaEntity> listarReservas(String username) {
        UsuarioEntity usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + username));
        String usuarioEncontrado = usuario.getNdoc();
        return reservaRepository.findByIdCliente(usuarioEncontrado);
    }

}
