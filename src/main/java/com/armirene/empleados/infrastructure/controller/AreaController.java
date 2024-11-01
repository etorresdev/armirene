package com.armirene.empleados.infrastructure.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.armirene.empleados.domain.AreaService;
import com.armirene.empleados.infrastructure.entity.Area;

@RestController
public class AreaController {

    @Autowired
    private AreaService areaService;

    @GetMapping("/obtenerAreas")
    public List<Area> obtenerPaises() {
        return areaService.findAll();
    }
}
