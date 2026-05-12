/**
 * Utilidad para mapear entre entidades y DTOs relacionados con usuarios.
 *
 * <p>Contiene métodos estáticos simples que encapsulan la conversión
 * entidad ↔ DTO para mantener la lógica de mapeo en un solo lugar.</p>
 *
 * @author USB
 * @since 1.0
 */
package co.edu.usbcali.ecommerceusb.mapper;

import co.edu.usbcali.ecommerceusb.dto.CreateUserRequest;
import co.edu.usbcali.ecommerceusb.dto.UserResponse;
import co.edu.usbcali.ecommerceusb.model.DocumentType;
import co.edu.usbcali.ecommerceusb.model.User;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public class UserMapper {

    /**
     * Convierte una entidad {@link User} a {@link UserResponse}.
     *
     * @param user entidad a convertir
     * @return DTO {@link UserResponse}
     */
    public static UserResponse modelToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                // Uso de un If Ternario
                .documentTypeId(
                        user.getDocumentType() != null ? user.getDocumentType().getId() : null)
                // Uso de un If Ternario
                .documentTypeName(
                        user.getDocumentType() != null ? user.getDocumentType().getName() : null)
                .documentNumber(user.getDocumentNumber())
                .build();
    }

    /**
     * Convierte una lista de entidades {@link User} a una lista de DTOs.
     *
     * @param users lista de entidades
     * @return lista de {@link UserResponse}
     */
    public static List<UserResponse> modelToUserResponseList(List<User> users) {
        return users.stream().map(UserMapper::modelToUserResponse).toList();
    }

    /**
     * Crea una entidad {@link User} a partir de un DTO de creación.
     *
     * @param createUserRequest datos de entrada
     * @param documentType entidad {@link DocumentType} asociada
     * @return entidad {@link User} preparada para persistir
     */
    public static User createUserRequestToUser(CreateUserRequest createUserRequest,
                                               DocumentType documentType) {
        // Convertir el objeto createUserRequest a User y retornamos ese user
        return User.builder()
                .fullName(createUserRequest.getFullName())
                .phone(createUserRequest.getPhone())
                .email(createUserRequest.getEmail())
                .documentType(documentType)
                .documentNumber(createUserRequest.getDocumentNumber())
                .birthDate(createUserRequest.getBirthDate())
                .country(createUserRequest.getCountry())
                .address(createUserRequest.getAddress())
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }
}
