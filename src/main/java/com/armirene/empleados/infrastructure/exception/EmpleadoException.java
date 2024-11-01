package com.armirene.empleados.infrastructure.exception;

public class EmpleadoException {

    public static class PrimerApellidoException extends RuntimeException {
        public PrimerApellidoException(String message) {
            super(message);
        }
    }

    public static class SegundoApellidoException extends RuntimeException {
        public SegundoApellidoException(String message) {
            super(message);
        }
    }

    public static class PrimerNombreException extends RuntimeException {
        public PrimerNombreException(String message) {
            super(message);
        }
    }

    public static class OtrosNombresException extends RuntimeException {
        public OtrosNombresException(String message) {
            super(message);
        }
    }
}
