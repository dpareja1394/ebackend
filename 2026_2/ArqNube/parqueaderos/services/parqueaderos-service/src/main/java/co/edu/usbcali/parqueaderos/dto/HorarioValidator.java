package co.edu.usbcali.parqueaderos.dto;

import java.time.LocalTime;

final class HorarioValidator {

    private HorarioValidator() {
    }

    static boolean esValido(Boolean abierto24h, LocalTime apertura, LocalTime cierre) {
        if (abierto24h == null || abierto24h) {
            return true;
        }
        return apertura != null && cierre != null && !apertura.equals(cierre);
    }
}
