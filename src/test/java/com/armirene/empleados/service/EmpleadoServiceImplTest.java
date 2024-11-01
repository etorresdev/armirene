package com.armirene.empleados.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import com.armirene.empleados.application.EmpleadoServiceImpl;
import com.armirene.empleados.domain.EmpleadoService;
import com.armirene.empleados.infrastructure.dto.EmpleadoDto;
import com.armirene.empleados.infrastructure.entity.Area;
import com.armirene.empleados.infrastructure.entity.Empleado;
import com.armirene.empleados.infrastructure.entity.Pais;
import com.armirene.empleados.infrastructure.entity.TipoIdentificacion;
import com.armirene.empleados.infrastructure.exception.EmpleadoException;
import com.armirene.empleados.infrastructure.repository.AreaRepository;
import com.armirene.empleados.infrastructure.repository.EmpleadoRepository;
import com.armirene.empleados.infrastructure.repository.PaisRepository;
import com.armirene.empleados.infrastructure.repository.TipoIdentificacionRepository;

@ExtendWith(MockitoExtension.class)
class EmpleadoServiceImplTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @Mock
    private TipoIdentificacionRepository tipoIdentificacionRepository;

    @Mock
    private PaisRepository paisRepository;

    @Mock
    private AreaRepository areaRepository;

    @InjectMocks
    private EmpleadoService empleadoService = new EmpleadoServiceImpl();

    @Test
    void testSave_SuccessfulSave() {

        EmpleadoDto empleadoDto = new EmpleadoDto();
        empleadoDto.setPrimerNombre("JUAN");
        empleadoDto.setOtrosNombres("GADIEL");
        empleadoDto.setPrimerApellido("PEREZ");
        empleadoDto.setSegundoApellido("LOPEZ");
        empleadoDto.setNumeroIdentificacion("123456785");
        empleadoDto.setIdTipoIdentificacion(1);
        empleadoDto.setIdPais(2);
        empleadoDto.setIdArea(3);
        empleadoDto.setFechaIngreso(new Date());

        TipoIdentificacion tipoIdentificacion = new TipoIdentificacion();
        Pais pais = new Pais();
        pais.setNombre("COLOMBIA");
        Area area = new Area();

        when(tipoIdentificacionRepository.findById(1)).thenReturn(Optional.of(tipoIdentificacion));
        when(paisRepository.findById(2)).thenReturn(Optional.of(pais));
        when(areaRepository.findById(3)).thenReturn(Optional.of(area));
        when(empleadoRepository.save(any(Empleado.class))).thenReturn(new Empleado());

        Empleado empleadoGuardado = empleadoService.save(empleadoDto, null);

        assertNotNull(empleadoGuardado);
        verify(empleadoRepository).save(any(Empleado.class));
    }

    @Test
    void testSave_WithInvalidPrimerNombre_ShouldThrowException() {

        EmpleadoDto empleadoDto = new EmpleadoDto();
        empleadoDto.setPrimerNombre("JU@N");
        empleadoDto.setOtrosNombres("GADIEL");
        empleadoDto.setPrimerApellido("PEREZ");
        empleadoDto.setSegundoApellido("LOPEZ");
        empleadoDto.setNumeroIdentificacion("123456785");
        empleadoDto.setIdTipoIdentificacion(1);
        empleadoDto.setIdPais(2);
        empleadoDto.setIdArea(3);
        empleadoDto.setFechaIngreso(new Date());

        Exception exception = assertThrows(EmpleadoException.PrimerNombreException.class, () -> {
            empleadoService.save(empleadoDto, null);
        });

        String expectedMessage = "El primer nombre solo permite caracteres de la A a la Z, mayúscula, sin acentos ni Ñ y su longitud máxima es de 20 letras";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testSave_InvalidDate() {

        EmpleadoDto empleadoDto = new EmpleadoDto();
        empleadoDto.setPrimerNombre("JUAN");
        empleadoDto.setOtrosNombres("GADIEL");
        empleadoDto.setPrimerApellido("PEREZ");
        empleadoDto.setSegundoApellido("LOPEZ");
        empleadoDto.setNumeroIdentificacion("123456785");
        empleadoDto.setIdTipoIdentificacion(1);
        empleadoDto.setIdPais(2);
        empleadoDto.setIdArea(3);
        empleadoDto.setFechaIngreso(new Date(System.currentTimeMillis() + 86400000));

        when(tipoIdentificacionRepository.findById(1)).thenReturn(Optional.of(new TipoIdentificacion()));
        when(paisRepository.findById(2)).thenReturn(Optional.of(new Pais()));
        when(areaRepository.findById(3)).thenReturn(Optional.of(new Area()));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            empleadoService.save(empleadoDto, null);
        });
        assertEquals("La fecha de ingreso debe estar entre hace un mes y la fecha actual.", exception.getMessage());
    }

    @Test
    void testSave_MissingTipoIdentificacion() {

        EmpleadoDto empleadoDto = new EmpleadoDto();
        empleadoDto.setPrimerNombre("JUAN");
        empleadoDto.setOtrosNombres("GADIEL");
        empleadoDto.setPrimerApellido("PEREZ");
        empleadoDto.setSegundoApellido("LOPEZ");
        empleadoDto.setNumeroIdentificacion("123456785");
        empleadoDto.setIdTipoIdentificacion(null);
        empleadoDto.setIdPais(2);
        empleadoDto.setIdArea(3);
        empleadoDto.setFechaIngreso(new Date());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            empleadoService.save(empleadoDto, null);
        });

        String expectedMessage = "Tipo de identificación no encontrado.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testUpdate_SuccessfulUpdate() throws Exception {
        Long id = 1L;
        EmpleadoDto empleadoDto = new EmpleadoDto();
        empleadoDto.setPrimerNombre("JUAN");
        empleadoDto.setPrimerApellido("PEREZ");
        empleadoDto.setSegundoApellido("LOPEZ");
        empleadoDto.setNumeroIdentificacion("12345");
        empleadoDto.setIdTipoIdentificacion(1);
        empleadoDto.setIdPais(1);

        Empleado empleadoExistente = new Empleado();
        empleadoExistente.setId(id);
        empleadoExistente.setPrimerNombre("PEDRO");
        empleadoExistente.setPrimerApellido("GOMEZ");
        empleadoExistente.setSegundoApellido("LOPEZ");
        empleadoExistente.setCorreo("pedro.gomez@empresa.com");
        empleadoExistente.setTipoIdentificacion(new TipoIdentificacion());
        empleadoExistente.setPais(new Pais());

        when(empleadoRepository.findById(id)).thenReturn(Optional.of(empleadoExistente));
        when(empleadoRepository.save(any(Empleado.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Pais pais = new Pais();
        pais.setNombre("Colombia");
        empleadoExistente.setPais(pais);

        Optional<Empleado> resultado = empleadoService.update(id, empleadoDto);

        assertTrue(resultado.isPresent());
        Empleado empleadoActualizado = resultado.get();
        assertEquals("JUAN", empleadoActualizado.getPrimerNombre());
        assertEquals("PEREZ", empleadoActualizado.getPrimerApellido());
        assertEquals("12345", empleadoActualizado.getNumeroIdentificacion());
        assertEquals("juan.perez@tuarmi.com.co", empleadoActualizado.getCorreo());
        assertNotNull(empleadoActualizado.getFechaEdicion());

        verify(empleadoRepository).findById(id);
        verify(empleadoRepository).save(any(Empleado.class));
    }

    @Test
    void testDeleteById() {
        Long id = 1L;
        empleadoService.deleteById(id);
        verify(empleadoRepository, times(1)).deleteById(id);
    }
}
