package com.armirene.empleados.infrastructure.dto;

import java.sql.Timestamp;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class EmpleadoDto {

    @NotBlank(message = "El campo primerNombre es obligatorio")
    @Pattern(regexp = "^[A-Z ]+$", message = "El campo solo debe contener letras mayúsculas sin acentos ni Ñ")
    private String primerNombre;

    @NotBlank(message = "El campo otrosNombres es obligatorio")
    @Pattern(regexp = "^[A-Z ]+$", message = "El campo solo debe contener letras mayúsculas sin acentos ni Ñ")
    private String otrosNombres;

    @NotBlank(message = "El campo primerApellido es obligatorio")
    @Pattern(regexp = "^[A-Z ]+$", message = "El campo solo debe contener letras mayúsculas sin acentos ni Ñ")
    private String primerApellido;

    @NotBlank(message = "El campo segundoApellido es obligatorio")
    @Pattern(regexp = "^[A-Z ]+$", message = "El campo solo debe contener letras mayúsculas sin acentos ni Ñ")
    private String segundoApellido;

    @Column(unique = true)
    private String correo;

    @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "No se permiten caracteres especiales.")
    @Column(unique = true)
    private String numeroIdentificacion;

    private String estado;

    private Date fechaIngreso;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Timestamp fechaRegistro;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Timestamp fechaEdicion;

    private String foto;

    private Integer idTipoIdentificacion;

    private Integer idPais;

    private Integer idArea;
}
