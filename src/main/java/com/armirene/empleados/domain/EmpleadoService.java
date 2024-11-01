package com.armirene.empleados.domain;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;

import com.armirene.empleados.infrastructure.dto.EmpleadoDto;
import com.armirene.empleados.infrastructure.entity.Empleado;

public interface EmpleadoService {

    public Page<Empleado> findAll(EmpleadoDto filter, int page, int size);

    public Empleado save(EmpleadoDto empleadoDto, byte[] imagenBytes);

    public Optional<Empleado> findById(@NonNull Long id);

    public Optional<Empleado> update(Long id, EmpleadoDto empleadoDto) throws Exception;

    public void deleteById(Long id);

}
