package com.armirene.empleados.application;

import java.util.List;

import com.armirene.empleados.domain.TipoIdentificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.armirene.empleados.infrastructure.entity.TipoIdentificacion;
import com.armirene.empleados.infrastructure.repository.TipoIdentificacionRepository;

/**
 * Implementación del servicio para gestionar tipos de identificacion.
 * Esta clase proporciona métodos para realizar operaciones relacionadas con el
 * manejo de tipos de identificacion.
 */
@Service
public class TipoIdentificacionServiceImpl implements TipoIdentificacionService {

    @Autowired
    TipoIdentificacionRepository tipoIdentificacionRepository;

    /**
     * Obtiene una lista de todas las tipos de identificacion disponibles.
     * Este método es de solo lectura y no realiza modificaciones en la base de
     * datos.
     *
     * @return una lista de objetos {@link TipoIdentificacion} que representan todas
     *         las tipos de identificacion en la base de datos.
     */
    @Override
    @Transactional(readOnly = true)
    public List<TipoIdentificacion> findAll() {
        return tipoIdentificacionRepository.findAll();
    }

}
