package com.armirene.empleados.application;

import java.sql.Timestamp;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

import com.armirene.empleados.domain.EmpleadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.armirene.empleados.infrastructure.dto.EmpleadoDto;
import com.armirene.empleados.infrastructure.entity.Empleado;
import com.armirene.empleados.infrastructure.exception.EmpleadoException.OtrosNombresException;
import com.armirene.empleados.infrastructure.exception.EmpleadoException.PrimerApellidoException;
import com.armirene.empleados.infrastructure.exception.EmpleadoException.PrimerNombreException;
import com.armirene.empleados.infrastructure.exception.EmpleadoException.SegundoApellidoException;
import com.armirene.empleados.infrastructure.repository.AreaRepository;
import com.armirene.empleados.infrastructure.repository.EmpleadoRepository;
import com.armirene.empleados.infrastructure.repository.PaisRepository;
import com.armirene.empleados.infrastructure.repository.TipoIdentificacionRepository;
import com.armirene.empleados.infrastructure.util.CorreoUtil;
import com.armirene.empleados.infrastructure.util.FechaUtils;
import com.armirene.empleados.infrastructure.util.enums.Dominio;

/**
 * Implementación del servicio de empleados.
 * Proporciona métodos para gestionar la información de los empleados,
 * incluyendo
 * búsqueda, creación, actualización y eliminación de empleados.
 */
@Service
public class EmpleadoServiceImpl implements EmpleadoService {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private TipoIdentificacionRepository tipoIdentificacionRepository;

    @Autowired
    private PaisRepository paisRepository;

    @Autowired
    private AreaRepository areaRepository;

    /**
     * Busca un empleado por su ID.
     *
     * @param id el ID del empleado a buscar.
     * @return un {@link Optional} que contiene el empleado encontrado, o vacío si
     *         no se encontró.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Empleado> findById(@NonNull Long id) {
        return empleadoRepository.findById(id);
    }

    /**
     * Obtiene una página de empleados según los filtros proporcionados.
     *
     * @param filter objeto {@link EmpleadoDto} que contiene los criterios de filtro
     *               para la búsqueda de empleados.
     * @param page   número de la página a obtener (iniciado en 0).
     * @param size   número de empleados por página.
     * @return un objeto {@link Page} que contiene una lista de empleados que
     *         cumplen con los filtros especificados.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Empleado> findAll(EmpleadoDto filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return empleadoRepository.findAll(createSpecification(filter), pageable);
    }

    /**
     * Guarda un nuevo empleado en el sistema.
     *
     * @param empleadoDto objeto {@link EmpleadoDto} que contiene la información del
     *                    empleado a guardar.
     * @param imagenBytes un array de bytes que representa la imagen del empleado.
     * @return el objeto {@link Empleado} que fue guardado.
     * @throws IllegalArgumentException si los datos del empleado no son válidos.
     */
    @Override
    @Transactional
    public Empleado save(EmpleadoDto empleadoDto, byte[] imagenBytes) {

        validarEmpleado(empleadoDto);

        Empleado empleado = new Empleado();
        empleado.setPrimerNombre(empleadoDto.getPrimerNombre());
        empleado.setOtrosNombres(empleadoDto.getOtrosNombres());
        empleado.setPrimerApellido(empleadoDto.getPrimerApellido());
        empleado.setSegundoApellido(empleadoDto.getSegundoApellido());
        empleado.setNumeroIdentificacion(empleadoDto.getNumeroIdentificacion());
        empleado.setTipoIdentificacion(tipoIdentificacionRepository.findById(empleadoDto.getIdTipoIdentificacion())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de identificación no encontrado.")));
        empleado.setPais(paisRepository.findById(empleadoDto.getIdPais())
                .orElseThrow(() -> new IllegalArgumentException("País no encontrado.")));
        empleado.setArea(areaRepository.findById(empleadoDto.getIdArea())
                .orElseThrow(() -> new IllegalArgumentException("Área no encontrada.")));
        Date fechaUnMesAntes = FechaUtils.obtenerFechaUnMesAntes(new Date());
        if (empleadoDto.getFechaIngreso().after(new Date()) || empleadoDto.getFechaIngreso().before(fechaUnMesAntes)) {
            throw new IllegalArgumentException("La fecha de ingreso debe estar entre hace un mes y la fecha actual.");
        }
        empleado.setFechaIngreso(empleadoDto.getFechaIngreso());
        empleado.setFechaRegistro(new Timestamp(System.currentTimeMillis()));
        String correo = crearCorreo(empleado);
        empleado.setCorreo(correo);
        empleado.setEstado("ACTIVO");
        imagenBytes = obtenerImagenBytes(empleadoDto.getFoto());
        if (imagenBytes != null && imagenBytes.length > 0) {
            guardarFotoEmpleado(empleado, imagenBytes);
        }
        return empleadoRepository.save(empleado);
    }

    /**
     * Actualiza la información de un empleado existente.
     *
     * @param id          el ID del empleado a actualizar.
     * @param empleadoDto objeto {@link EmpleadoDto} que contiene la nueva
     *                    información del empleado.
     * @return un {@link Optional} que contiene el empleado actualizado, o vacío si
     *         no se encontró el empleado.
     * @throws Exception si ocurre un error durante el proceso de actualización.
     */
    @Override
    public Optional<Empleado> update(Long id, EmpleadoDto empleadoDto) throws Exception {

        validarEmpleado(empleadoDto);

        Optional<Empleado> empleadoExistente = empleadoRepository.findById(id);
        String correoAnterior = empleadoExistente.get().getCorreo();

        if (empleadoExistente.isPresent()) {
            Empleado empleado = empleadoExistente.get();

            empleado.setPrimerNombre(empleadoDto.getPrimerNombre());
            empleado.setOtrosNombres(empleadoDto.getOtrosNombres());
            empleado.setPrimerApellido(empleadoDto.getPrimerApellido());
            empleado.setSegundoApellido(empleadoDto.getSegundoApellido());
            empleado.setNumeroIdentificacion(empleadoDto.getNumeroIdentificacion());
            empleado.getTipoIdentificacion().setId(empleadoDto.getIdTipoIdentificacion());
            empleado.getPais().setId(empleadoDto.getIdPais());
            String correo = crearCorreo(empleado);
            if (CorreoUtil.nombresHanCambiado(correoAnterior, empleado.getPrimerNombre(),
                    empleado.getPrimerApellido())) {
                empleado.setCorreo(correo);
            }
            empleado.setFechaEdicion(new Timestamp(System.currentTimeMillis()));

            return Optional.of(empleadoRepository.save(empleado));
        }
        return Optional.empty();
    }

    /**
     * Elimina un empleado por su ID.
     *
     * @param id el ID del empleado que se desea eliminar.
     */
    @Override
    @Transactional
    public void deleteById(Long id) {
        empleadoRepository.deleteById(id);
    }

    /**
     * Crea un correo electrónico basado en el nombre y apellido del empleado y su
     * país.
     *
     * @param empleado el objeto {@link Empleado} del cual se generará el correo.
     * @return el correo electrónico generado.
     */
    private String crearCorreo(Empleado empleado) {
        String dominio = Dominio.obtenerDominio(empleado.getPais().getNombre().toUpperCase());
        String correoBase = generarCorreoBase(empleado);
        String correo = correoBase + "@" + dominio;
        int contador = 1;

        while (empleadoRepository.existsByCorreo(correo)) {
            correo = correoBase + "." + contador + "@" + dominio;
            contador++;
        }
        return correo;
    }

    /**
     * Genera la parte base del correo electrónico utilizando el primer nombre y
     * apellido del empleado.
     *
     * @param empleado el objeto {@link Empleado} del cual se generará la base del
     *                 correo.
     * @return la parte base del correo electrónico.
     */
    private String generarCorreoBase(Empleado empleado) {
        String primerNombre = empleado.getPrimerNombre().split(" ")[0].toLowerCase();
        String primerApellido = formatearApellido(empleado.getPrimerApellido());
        return primerNombre + "." + primerApellido;
    }

    /**
     * Formatea el apellido eliminando espacios y guiones, y convirtiéndolo a
     * minúsculas.
     *
     * @param apellido el apellido a formatear.
     * @return el apellido formateado.
     */
    private String formatearApellido(String apellido) {
        return apellido.toLowerCase().replaceAll("\\s|-", "");
    }

    /**
     * Convierte una imagen de bytes a una cadena en formato Base64.
     *
     * @param imagenBytes el array de bytes que representa la imagen.
     * @return la representación en Base64 de la imagen.
     */
    public String convertirImagenABase64(byte[] imagenBytes) {
        String base64Image = Base64.getEncoder().encodeToString(imagenBytes);
        return "data:image/png;base64," + base64Image;
    }

    /**
     * Obtiene los bytes de una imagen desde su representación en Base64.
     *
     * @param fotoBase64 la cadena en Base64 que representa la imagen.
     * @return un array de bytes que representa la imagen, o null si la entrada está
     *         vacía.
     */
    public byte[] obtenerImagenBytes(String fotoBase64) {
        if (fotoBase64 != null && !fotoBase64.isEmpty()) {
            return Base64.getDecoder().decode(fotoBase64);
        }
        return null;
    }

    /**
     * Guarda la foto del empleado en su objeto utilizando la representación en
     * Base64.
     *
     * @param empleado    el objeto {@link Empleado} al cual se le asignará la foto.
     * @param imagenBytes el array de bytes que representa la imagen.
     */
    public void guardarFotoEmpleado(Empleado empleado, byte[] imagenBytes) {
        String fotoBase64 = convertirImagenABase64(imagenBytes);
        empleado.setFoto(fotoBase64);
    }

    /**
     * Crea una especificación de búsqueda dinámica basada en los filtros
     * proporcionados.
     * Genera predicados de búsqueda para los atributos del empleado definidos en el
     * objeto {@link EmpleadoDto}.
     *
     * @param filter objeto {@link EmpleadoDto} que contiene los criterios de filtro
     *               para la búsqueda.
     * @return un objeto {@link Specification} que define los filtros aplicables a
     *         la consulta.
     */
    private Specification<Empleado> createSpecification(EmpleadoDto filter) {
        return (root, query, criteriaBuilder) -> {
            var predicates = criteriaBuilder.conjunction();

            if (filter.getPrimerNombre() != null && !filter.getPrimerNombre().isEmpty()) {
                predicates = criteriaBuilder.and(predicates,
                        criteriaBuilder.equal(root.get("primerNombre"), filter.getPrimerNombre()));
            }
            if (filter.getOtrosNombres() != null && !filter.getOtrosNombres().isEmpty()) {
                predicates = criteriaBuilder.and(predicates,
                        criteriaBuilder.equal(root.get("otrosNombres"), filter.getOtrosNombres()));
            }
            if (filter.getPrimerApellido() != null && !filter.getPrimerApellido().isEmpty()) {
                predicates = criteriaBuilder.and(predicates,
                        criteriaBuilder.equal(root.get("primerApellido"), filter.getPrimerApellido()));
            }
            if (filter.getSegundoApellido() != null && !filter.getSegundoApellido().isEmpty()) {
                predicates = criteriaBuilder.and(predicates,
                        criteriaBuilder.equal(root.get("segundoApellido"), filter.getSegundoApellido()));
            }
            if (filter.getIdTipoIdentificacion() != null) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder
                        .equal(root.join("tipoIdentificacion").get("id"), filter.getIdTipoIdentificacion()));
            }
            if (filter.getNumeroIdentificacion() != null && !filter.getNumeroIdentificacion().isEmpty()) {
                predicates = criteriaBuilder.and(predicates,
                        criteriaBuilder.equal(root.get("numeroIdentificacion"), filter.getNumeroIdentificacion()));
            }
            if (filter.getIdPais() != null) {
                predicates = criteriaBuilder.and(predicates,
                        criteriaBuilder.equal(root.join("pais").get("id"), filter.getIdPais()));
            }
            if (filter.getCorreo() != null && !filter.getCorreo().isEmpty()) {
                predicates = criteriaBuilder.and(predicates,
                        criteriaBuilder.equal(root.get("correo"), filter.getCorreo()));
            }
            if (filter.getEstado() != null && !filter.getEstado().isEmpty()) {
                predicates = criteriaBuilder.and(predicates,
                        criteriaBuilder.equal(root.get("estado"), filter.getEstado()));
            }

            return predicates;
        };
    }

    /**
     * Valida los datos del empleado antes de su creación o actualización.
     * Verifica los valores de los atributos del objeto {@link EmpleadoDto} y arroja
     * excepciones personalizadas en caso de que alguno de los valores no sea
     * válido.
     *
     * @param empleadoDto el objeto {@link EmpleadoDto} que contiene los datos a
     *                    validar.
     * @throws PrimerNombreException    si el primer nombre es nulo, vacío o no
     *                                  cumple con el formato.
     * @throws OtrosNombresException    si los otros nombres no cumplen con el
     *                                  formato o superan el límite de caracteres.
     * @throws PrimerApellidoException  si el primer apellido es nulo, vacío o no
     *                                  cumple con el formato.
     * @throws SegundoApellidoException si el segundo apellido es nulo, vacío o no
     *                                  cumple con el formato.
     */
    private void validarEmpleado(EmpleadoDto empleadoDto) {

        if (empleadoDto.getPrimerNombre() == null || empleadoDto.getPrimerNombre().isEmpty()) {
            throw new PrimerNombreException("El primer nombre es requerido.");
        }
        if (!empleadoDto.getPrimerNombre().matches("^[A-Z]{1,20}$")) {
            throw new PrimerNombreException(
                    "El primer nombre solo permite caracteres de la A a la Z, mayúscula, sin acentos ni Ñ y su longitud máxima es de 20 letras.");
        }

        if (empleadoDto.getOtrosNombres() != null && empleadoDto.getOtrosNombres().length() > 50) {
            throw new OtrosNombresException("Los otros nombres deben tener una longitud máxima de 50 letras.");
        }
        if (empleadoDto.getOtrosNombres() != null && !empleadoDto.getOtrosNombres().matches("^[A-Z ]{0,50}$")) {
            throw new OtrosNombresException(
                    "Los otros nombres solo permiten caracteres de la A a la Z, mayúscula, sin acentos ni Ñ, y el carácter espacio entre nombres.");
        }

        if (empleadoDto.getPrimerApellido() == null || empleadoDto.getPrimerApellido().isEmpty()) {
            throw new PrimerApellidoException("El primer apellido es requerido.");
        }
        if (!empleadoDto.getPrimerApellido().matches("^[A-Z]{1,20}$")) {
            throw new PrimerApellidoException(
                    "El primer apellido solo permite caracteres de la A a la Z, mayúscula, sin acentos ni Ñ y su longitud máxima es de 20 letras.");
        }

        if (empleadoDto.getSegundoApellido() == null || empleadoDto.getSegundoApellido().isEmpty()) {
            throw new SegundoApellidoException("El segundo apellido es requerido.");
        }
        if (!empleadoDto.getSegundoApellido().matches("^[A-Z]{1,20}$")) {
            throw new SegundoApellidoException(
                    "El segundo apellido solo permite caracteres de la A a la Z, mayúscula, sin acentos ni Ñ y su longitud máxima es de 20 letras.");
        }
    }

}
