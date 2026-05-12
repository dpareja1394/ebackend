/**
 * DTO para la creación de usuarios.
 *
 * <p>Este DTO contiene las validaciones de entrada (Jakarta Bean Validation)
 * utilizadas por el controller. El campo {@code birthDate} se mapea a
 * {@link java.time.LocalDate} y se espera con formato {@code yyyy-MM-dd}.
 *</p>
 *
 * @author USB
 * @since 1.0
 */
package co.edu.usbcali.ecommerceusb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {
    /** Nombre completo del usuario (no vacío). */
    @NotBlank
    private String fullName;

    /** Teléfono de contacto (no vacío). */
    @NotBlank
    private String phone;

    /** Correo electrónico válido (no vacío). */
    @Email
    @NotBlank
    private String email;

    /** Identificador del tipo de documento (positivo). */
    @NotNull
    @Positive
    private Integer documentTypeId;

    /** Número del documento (no vacío). */
    @NotBlank
    private String documentNumber;

    /** Fecha de nacimiento. Debe ser una fecha pasada. Formato: yyyy-MM-dd. */
    @NotNull
    @Past
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    /** País de residencia (no vacío). */
    @NotBlank
    private String country;

    /** Dirección del usuario (no vacío). */
    @NotBlank
    private String address;
}
