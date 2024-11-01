package com.armirene.empleados.infrastructure.util.enums;

public enum Dominio {

    COLOMBIA("tuarmi.com.co"),
    VENEZUELA("armirene.com.ve");

    private final String valor;

    Dominio(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static String obtenerDominio(String nombre) {
        switch (nombre.toUpperCase()) {
            case "COLOMBIA":
                return COLOMBIA.getValor();
            case "VENEZUELA":
                return VENEZUELA.getValor();
            default:
                throw new IllegalArgumentException("País no soportado para el dominio");
        }
    }
}
