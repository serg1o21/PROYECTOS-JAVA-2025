package com.proyecto.hotelgema.aspect;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Before("execution(* com.proyecto.hotelgema.service.*.*(..))")
    public void antesDeEjecutar(JoinPoint punto) {
        String fechaHora = LocalDateTime.now().format(formatoFecha);
        System.out.println("[" + fechaHora + "] Entrando al método: " + punto.getSignature().getName());
    }

    @AfterReturning(pointcut = "execution(* com.proyecto.hotelgema.service.*.*(..))", returning = "resultado")
    public void despuesDeEjecutar(JoinPoint punto, Object resultado) {
        String fechaHora = LocalDateTime.now().format(formatoFecha);
        System.out.println("[" + fechaHora + "] Método terminado: " + punto.getSignature().getName()
                + " | Resultado: " + resumen(resultado));
    }

    private String resumen(Object resultado) {
        if (resultado == null) return "null";
        if (resultado instanceof List<?> lista) {
            return "Lista con " + lista.size() + " elementos.";
        }
        return resultado.getClass().getSimpleName();
    }
}
