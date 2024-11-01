package com.armirene.empleados.application;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.armirene.empleados.domain.AreaService;
import com.armirene.empleados.infrastructure.entity.Area;
import com.armirene.empleados.infrastructure.repository.AreaRepository;

/**
 * Implementación del servicio para gestionar áreas.
 * Esta clase proporciona métodos para realizar operaciones relacionadas con el
 * manejo de áreas.
 */
@Service
public class AreaServiceImpl implements AreaService {

    @Autowired
    private AreaRepository areaRepository;

    /**
     * Obtiene una lista de todas las áreas disponibles.
     * Este método es de solo lectura y no realiza modificaciones en la base de
     * datos.
     *
     * @return una lista de objetos {@link Area} que representan todas las áreas en
     *         la base de datos.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Area> findAll() {
        return areaRepository.findAll();
    }
}
