package co.edu.usbcali.parqueaderos.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalTime;

public record ParqueaderoRequest(
        @NotBlank(message = "Es obligatorio") @Size(min = 3, max = 100, message = "Debe tener entre 3 y 100 caracteres")
        String nombre,
        @NotBlank(message = "Es obligatoria") @Size(min = 5, max = 200, message = "Debe tener entre 5 y 200 caracteres")
        String direccion,
        @NotBlank(message = "Es obligatoria") @Size(min = 2, max = 80, message = "Debe tener entre 2 y 80 caracteres")
        String ciudad,
        @NotNull(message = "Es obligatorio")
        Boolean abierto24h,
        @JsonFormat(pattern = "HH:mm")
        LocalTime horaApertura,
        @JsonFormat(pattern = "HH:mm")
        LocalTime horaCierre) {

    /**
     * Si no abre 24 h, ambas horas son obligatorias y distintas. Una hora de cierre menor que la de
     * apertura es válida: el parqueadero cierra al día siguiente.
     */
    @JsonIgnore
    @AssertTrue(message = "Si no abre 24 h, la hora de apertura y la de cierre son obligatorias y deben ser distintas")
    public boolean isHorario() {
        return HorarioValidator.esValido(abierto24h, horaApertura, horaCierre);
    }
}
