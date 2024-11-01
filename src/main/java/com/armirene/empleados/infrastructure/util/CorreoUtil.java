package com.armirene.empleados.infrastructure.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CorreoUtil {

    private static final String CORREO_REGEX = "([a-zA-Z]+)\\.([a-zA-Z]+)(\\.\\d+)?@.*";

    public static String[] extraerNombreApellido(String correo) {
        Pattern pattern = Pattern.compile(CORREO_REGEX);
        Matcher matcher = pattern.matcher(correo);

        if (matcher.find()) {
            String nombre = matcher.group(1);
            String apellido = matcher.group(2);
            return new String[] { nombre, apellido };
        }

        return new String[] { "", "" };
    }

    public static boolean nombresHanCambiado(String correoAnterior, String nombreActual, String apellidoActual) {
        String[] nombreApellidoAnterior = extraerNombreApellido(correoAnterior);
        String nombreAnterior = nombreApellidoAnterior[0];
        String apellidoAnterior = nombreApellidoAnterior[1];

        return !nombreAnterior.equalsIgnoreCase(nombreActual) || !apellidoAnterior.equalsIgnoreCase(apellidoActual);
    }
}
