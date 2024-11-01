package com.armirene.empleados.infrastructure.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.armirene.empleados.infrastructure.entity.Pais;
import com.armirene.empleados.domain.PaisService;

@RestController
public class PaisController {

    @Autowired
    private PaisService paisService;

    @GetMapping("/obtenerPaises")
    public List<Pais> obtenerPaises() {
        return paisService.findAll();
    }

}
