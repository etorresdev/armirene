package com.armirene.empleados.infrastructure.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("statusError", HttpStatus.BAD_REQUEST.toString());

        Map<String, Integer> maxLengths = new HashMap<>();
        maxLengths.put("primerNombre", 20);
        maxLengths.put("otrosNombres", 50);
        maxLengths.put("primerApellido", 20);
        maxLengths.put("segundoApellido", 20);

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            String field = error.getField();
            Integer maxLength = maxLengths.getOrDefault(field, 20);
            String message = String.format(
                    "El campo '%s' solo permite caracteres de la A a la Z, mayúsculas, sin acentos ni Ñ y su longitud máxima es de %d letras",
                    field, maxLength);
            errors.put(field, message);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleUniqueConstraintViolation(DataIntegrityViolationException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        String message = ex.getMessage();

        if (message.contains("identificacion")) {
            errorResponse.put("statusError", HttpStatus.BAD_REQUEST.toString());
            errorResponse.put("message", "El número de identificación ya existe. Por favor, ingrese uno diferente.");
        } else if (message.contains("correo")) {
            errorResponse.put("statusError", HttpStatus.BAD_REQUEST.toString());
            errorResponse.put("message", "El correo electrónico ya está registrado. Por favor, ingrese uno diferente.");
        } else {
            errorResponse.put("statusError", HttpStatus.BAD_REQUEST.toString());
            errorResponse.put("message", "Error de integridad de datos. Verifique la información ingresada.");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

}
