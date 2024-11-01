package com.armirene.empleados.infrastructure.entity;

import java.io.Serializable;
import java.util.Date;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "empleado")
public class Empleado implements Serializable {

    private static final long serialVersionUID = -4315232028L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "primer_nombre", length = 20)
    private String primerNombre;

    @Column(name = "otros_nombres", length = 50)
    private String otrosNombres;

    @Column(name = "primer_apellido", length = 20)
    private String primerApellido;

    @Column(name = "segundo_apellido", length = 20)
    private String segundoApellido;

    @Column(length = 300)
    private String correo;

    @Column(name = "numero_identificacion", length = 20)
    private String numeroIdentificacion;

    private String estado;

    @Column(name = "fecha_ingreso")
    private Date fechaIngreso;

    @Column(name = "fecha_registro")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Timestamp fechaRegistro;

    @Column(name = "fecha_edicion")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Timestamp fechaEdicion;

    @Column(name = "foto", columnDefinition = "TEXT")
    @Lob
    private String foto;

    @ManyToOne
    @JoinColumn(name = "id_tipo_identificacion")
    private TipoIdentificacion tipoIdentificacion;

    @ManyToOne
    @JoinColumn(name = "id_pais")
    private Pais pais;

    @ManyToOne
    @JoinColumn(name = "id_area")
    private Area area;

}
