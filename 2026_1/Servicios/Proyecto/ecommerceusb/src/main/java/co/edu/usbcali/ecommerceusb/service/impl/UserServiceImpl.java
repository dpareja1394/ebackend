/**
 * Implementación del servicio de usuarios.
 *
 * <p>Contiene la lógica de negocio mínima para crear y actualizar
 * usuarios. Se aplicaron optimizaciones: inyección por constructor,
 * uso de transacciones en operaciones de escritura y validaciones de
 * unicidad sólo cuando los campos relevantes cambian.</p>
 *
 * @author USB
 * @since 1.0
 */
package co.edu.usbcali.ecommerceusb.service.impl;

import co.edu.usbcali.ecommerceusb.dto.CreateUserRequest;
import co.edu.usbcali.ecommerceusb.dto.UpdateUserRequest;
import co.edu.usbcali.ecommerceusb.dto.UserResponse;
import co.edu.usbcali.ecommerceusb.mapper.UserMapper;
import co.edu.usbcali.ecommerceusb.model.DocumentType;
import co.edu.usbcali.ecommerceusb.model.User;
import co.edu.usbcali.ecommerceusb.repository.DocumentTypeRepository;
import co.edu.usbcali.ecommerceusb.repository.UserRepository;
import co.edu.usbcali.ecommerceusb.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final DocumentTypeRepository documentTypeRepository;

    public UserServiceImpl(UserRepository userRepository,
                           DocumentTypeRepository documentTypeRepository) {
        this.userRepository = userRepository;
        this.documentTypeRepository = documentTypeRepository;
    }

    /**
     * Obtiene todos los usuarios mapeados a DTO.
     *
     * @return lista de {@link UserResponse}
     */
    @Override
    public List<UserResponse> getUsers() {
        List<User> users = userRepository.findAll();
        return users.isEmpty() ? List.of() : UserMapper.modelToUserResponseList(users);
    }

    /**
     * Busca un usuario por id.
     *
     * @param id identificador del usuario
     * @return {@link UserResponse} del usuario encontrado
     * @throws Exception si el id es inválido o no existe
     */
    @Override
    public UserResponse getUserById(Integer id) throws Exception {
        if (id == null || id <= 0) {
            throw new Exception("Debe ingresar el id para buscar");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new Exception(String.format("Usuario no encontrado con el id: %d", id)));
        return UserMapper.modelToUserResponse(user);
    }

    /**
     * Busca un usuario por email.
     *
     * @param email correo del usuario
     * @return {@link UserResponse}
     * @throws Exception si el email es inválido o no existe
     */
    @Override
    public UserResponse getUserByEmail(String email) throws Exception {
        if(email == null || email.isBlank()) {
            throw new Exception("Debe ingresar el email");
        }
        User userByEmail = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception(String.format("Usuario no encontrado con el email: %s", email)));
        return UserMapper.modelToUserResponse(userByEmail);
    }

    /**
     * Crea un nuevo usuario validando unicidades y el tipo de documento.
     *
     * @param createUserRequest DTO con los datos del nuevo usuario
     * @return {@link UserResponse} creado
     * @throws Exception en caso de reglas de negocio incumplidas
     */
    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest createUserRequest) throws Exception {
        if(Objects.isNull(createUserRequest)) {
            throw new Exception("El objeto createUserRequest no puede ser nulo");
        }

        DocumentType documentType = documentTypeRepository.findById(createUserRequest.getDocumentTypeId())
                .orElseThrow(() -> new Exception("El tipo de documento no existe"));

        if (userRepository.existsByEmail(createUserRequest.getEmail())) {
            throw new Exception("Ya existe un usuario con el email ingresado");
        }

        if (userRepository.existsByDocumentNumberAndDocumentTypeId(
                createUserRequest.getDocumentNumber(), createUserRequest.getDocumentTypeId())) {
            throw new Exception("Ya existe un usuario con el documento y tipo de documento ingresados");
        }

        User user = UserMapper.createUserRequestToUser(createUserRequest, documentType);
        user = userRepository.save(user);
        return UserMapper.modelToUserResponse(user);
    }

    /**
     * Actualiza un usuario existente. Sólo valida unicidad para los campos
     * que efectivamente cambian (email, documento), lo que reduce queries
     * innecesarias.
     *
     * @param id id del usuario a actualizar
     * @param updateUserRequest DTO con los nuevos datos
     * @return {@link UserResponse} actualizado
     * @throws Exception en caso de reglas de negocio incumplidas o si no existe
     */
    @Override
    @Transactional
    public UserResponse updateUser(Integer id, UpdateUserRequest updateUserRequest) throws Exception {
        if (id == null || id <= 0) {
            throw new Exception("Debe ingresar el id del usuario");
        }
        if (Objects.isNull(updateUserRequest)) {
            throw new Exception("El objeto updateUserRequest no puede ser nulo");
        }

        // Cargar el usuario primero
        User user = userRepository.findById(id)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        // Validar unicidad de email solo si cambió
        if (!Objects.equals(updateUserRequest.getEmail(), user.getEmail())) {
            if (userRepository.existsByIdNotAndEmail(id, updateUserRequest.getEmail())) {
                throw new Exception("Ya existe un usuario con el email ingresado");
            }
        }

        // Validar unicidad de documento solo si cambio número o tipo
        Integer currentDocTypeId = user.getDocumentType() != null ? user.getDocumentType().getId() : null;
        if (!Objects.equals(updateUserRequest.getDocumentNumber(), user.getDocumentNumber()) ||
                !Objects.equals(updateUserRequest.getDocumentTypeId(), currentDocTypeId)) {
            if (userRepository.existsByIdNotAndDocumentNumberAndDocumentTypeId(id,
                    updateUserRequest.getDocumentNumber(), updateUserRequest.getDocumentTypeId())) {
                throw new Exception("Ya existe un usuario con el documento y tipo de documento ingresados");
            }
        }

        DocumentType documentType = (currentDocTypeId != null && Objects.equals(updateUserRequest.getDocumentTypeId(), currentDocTypeId))
                ? user.getDocumentType()
                : documentTypeRepository.findById(updateUserRequest.getDocumentTypeId())
                .orElseThrow(() -> new Exception("El tipo de documento no existe"));

        user.setFullName(updateUserRequest.getFullName());
        user.setPhone(updateUserRequest.getPhone());
        user.setEmail(updateUserRequest.getEmail());
        user.setDocumentType(documentType);
        user.setDocumentNumber(updateUserRequest.getDocumentNumber());
        user.setBirthDate(updateUserRequest.getBirthDate());
        user.setCountry(updateUserRequest.getCountry());
        user.setAddress(updateUserRequest.getAddress());
        user.setUpdatedAt(OffsetDateTime.now());

        user = userRepository.save(user);
        return UserMapper.modelToUserResponse(user);
    }
}
