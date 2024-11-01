package com.armirene.empleados.infrastructure.controller;

import org.springframework.web.bind.annotation.RestController;

import com.armirene.empleados.infrastructure.dto.EmpleadoDto;
import com.armirene.empleados.infrastructure.entity.Empleado;
import com.armirene.empleados.domain.EmpleadoService;

import jakarta.validation.Valid;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
public class EmpleadoController {

    @Autowired
    private EmpleadoService empleadoService;

    @GetMapping("/empleados")
    public Page<Empleado> empleados(
            @RequestParam(required = false) String primerNombre,
            @RequestParam(required = false) String otrosNombres,
            @RequestParam(required = false) String primerApellido,
            @RequestParam(required = false) String segundoApellido,
            @RequestParam(required = false) Integer idTipoIdentificacion,
            @RequestParam(required = false) String numeroIdentificacion,
            @RequestParam(required = false) Integer idPais,
            @RequestParam(required = false) String correo,
            @RequestParam(required = false) String estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        EmpleadoDto filter = new EmpleadoDto();
        filter.setPrimerNombre(primerNombre);
        filter.setOtrosNombres(otrosNombres);
        filter.setPrimerApellido(primerApellido);
        filter.setSegundoApellido(segundoApellido);
        filter.setIdTipoIdentificacion(idTipoIdentificacion);
        filter.setNumeroIdentificacion(numeroIdentificacion);
        filter.setIdPais(idPais);
        filter.setCorreo(correo);
        filter.setEstado(estado);
        return empleadoService.findAll(filter, page, size);
    }

    @PostMapping("/crearEmpleado")
    public ResponseEntity<Empleado> crearEmpleado(@Valid @RequestBody EmpleadoDto empleadoDto, byte[] imagenBytes) {
        return ResponseEntity.status(HttpStatus.CREATED).body(empleadoService.save(empleadoDto, imagenBytes));
    }

    @PutMapping("/editarEmpleado/{id}")
    public ResponseEntity<Optional<Empleado>> editarEmpleado(@PathVariable Long id,
            @Valid @RequestBody EmpleadoDto empleadoDto) throws Exception {
        Optional<Empleado> empleadoActualizado = empleadoService.update(id, empleadoDto);
        return empleadoActualizado.isPresent()
                ? ResponseEntity.ok(empleadoActualizado)
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/eliminarEmpleado/{id}")
    public ResponseEntity<?> eliminarEmpleado(@PathVariable Long id,
            @RequestParam(required = false, defaultValue = "false") boolean confirmar) {
        Optional<Empleado> empleadoOptional = empleadoService.findById(id);
        if (empleadoOptional.isPresent()) {
            if (!confirmar) {
                return ResponseEntity.badRequest().body(
                        "¿Está seguro de que desea eliminar el empleado? Por favor confirmar la eliminación.");
            }
            empleadoService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Empleado con ID " + id + " no encontrado.");
        }
    }

}
