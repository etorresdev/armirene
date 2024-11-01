package com.armirene.empleados.infrastructure.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class FechaUtils {

    public static Date obtenerFechaUnMesAntes(Date fechaActual) {

        LocalDate localDateActual = fechaActual.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate fechaUnMesAntes = localDateActual.minusMonths(1);
        return Date.from(fechaUnMesAntes.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static String formatearFecha(Date fecha) {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        return formato.format(fecha);
    }
}
