package com.armirene.empleados.application;

import java.util.List;

import com.armirene.empleados.domain.PaisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.armirene.empleados.infrastructure.entity.Area;
import com.armirene.empleados.infrastructure.entity.Pais;
import com.armirene.empleados.infrastructure.repository.PaisRepository;

/**
 * Implementación del servicio para gestionar paises.
 * Esta clase proporciona métodos para realizar operaciones relacionadas con el
 * manejo de paises.
 */
@Service
public class PaisServiceImpl implements PaisService {

    @Autowired
    PaisRepository paisRepository;

    /**
     * Obtiene una lista de todas los paises disponibles.
     * Este método es de solo lectura y no realiza modificaciones en la base de
     * datos.
     *
     * @return una lista de objetos {@link Pais} que representan todos las paises en
     *         la base de datos.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Pais> findAll() {
        return paisRepository.findAll();
    }

}
