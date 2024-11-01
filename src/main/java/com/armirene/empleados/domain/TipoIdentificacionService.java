package com.armirene.empleados.domain;

import java.util.List;

import com.armirene.empleados.infrastructure.entity.TipoIdentificacion;

public interface TipoIdentificacionService {

    public List<TipoIdentificacion> findAll();

}
