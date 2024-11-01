package com.armirene.empleados.infrastructure.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.armirene.empleados.infrastructure.entity.TipoIdentificacion;
import com.armirene.empleados.domain.TipoIdentificacionService;

@RestController
public class TipoIdentificacionController {

    @Autowired
    private TipoIdentificacionService tipoIdentificacionService;

    @GetMapping("/obtenerTiposIdentificacion")
    public List<TipoIdentificacion> obtenerTiposIdentificacion() {
        return tipoIdentificacionService.findAll();
    }
}
