package com.proyecto.hotelgema.dao.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Table(name = "usuario")
@Data
public class UsuarioEntity {

    @Id
    @Column(name = "ndoc")
    @NotBlank(message = "El número de documento es obligatorio")
    private String ndoc;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede tener más de 100 caracteres")
    private String apellido;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    private java.time.LocalDate fecnacimiento;

    @NotNull(message = "El género es obligatorio")
    @Pattern(regexp = "M|F|O", message = "El género debe ser M, F u O")
    private String genero;

    @NotBlank(message = "El tipo de documento es obligatorio")
    private String tipodoc;

    private String rol;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe tener un formato válido")
    private String correo;

    @NotBlank(message = "El usuario es obligatorio")
    private String usuario;

    @NotBlank(message = "La clave es obligatoria")
    private String clave;

    private Boolean estado;
}
