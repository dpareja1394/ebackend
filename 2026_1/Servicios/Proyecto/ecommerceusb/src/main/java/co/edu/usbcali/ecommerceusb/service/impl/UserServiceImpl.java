package co.edu.usbcali.ecommerceusb.service.impl;

import co.edu.usbcali.ecommerceusb.dto.CreateUserRequest;
import co.edu.usbcali.ecommerceusb.dto.UserResponse;
import co.edu.usbcali.ecommerceusb.mapper.UserMapper;
import co.edu.usbcali.ecommerceusb.model.DocumentType;
import co.edu.usbcali.ecommerceusb.model.User;
import co.edu.usbcali.ecommerceusb.repository.DocumentTypeRepository;
import co.edu.usbcali.ecommerceusb.repository.UserRepository;
import co.edu.usbcali.ecommerceusb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    // Inyección de dependencias de UserRepository
    @Autowired
    private UserRepository userRepository;

    // Inyección de dependencias de DocumentTypeRepository
    @Autowired
    private DocumentTypeRepository documentTypeRepository;

    @Override
    public List<UserResponse> getUsers() {
        // Definir una lista de Users
        List<User> users = userRepository.findAll();

        // Validar si la lista está vacía
        if (users.isEmpty()) {
            return List.of();
        }

        /* Si la lista contiene información, entonces
        * retornamos una lista mapeada de UserResponse */
        List<UserResponse> userResponses = UserMapper.modelToUserResponseList(users);
        return userResponses;
    }

    @Override
    public UserResponse getUserById(Integer id) throws Exception {
        // Validar que venga un valor en id
        if (id == null || id <= 0) {
            throw new Exception("Debe ingresar el id para buscar");
        }
        // Buscar usuario en base de datos por id
        // Si no lo encuentra, lanza excepción
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new Exception(
                                String.format("Usuario no encontrado con el id: %d", id)));
        // Mapear a Response
        UserResponse userResponse = UserMapper.modelToUserResponse(user);
        // Retornar usuario encontrado
        return userResponse;
    }

    @Override
    public UserResponse getUserByEmail(String email) throws Exception {
        // Validar que el email contenga un valor
        if(email == null || email.isBlank()) {
            throw new Exception("Debe ingresar el email");
        }
        // Buscar usuario en base de datos por email
        User userByEmail = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new Exception(
                                String.format("Usuario no encontrado con el email: %s", email)));
        // Mapear a Response y retornar
        return UserMapper.modelToUserResponse(userByEmail);
    }

    @Override
    public UserResponse createUser(CreateUserRequest createUserRequest) throws Exception {
        // Validar el objeto createUserRequest
        // Validar que el objeto no sea nulo
        if(Objects.isNull(createUserRequest)) {
            throw new Exception("El objeto createUserRequest no puede ser nulo");
        }

        // Validar que el campo fullName no sea nulo
        if(Objects.isNull(createUserRequest.getFullName()) ||
            createUserRequest.getFullName().isBlank()) {
            throw new Exception("El campo fullName no puede ser nulo ni vacío");
        }

        // Validar que el campo phone no sea nulo ni vacío
        if(Objects.isNull(createUserRequest.getPhone()) ||
            createUserRequest.getPhone().isBlank()) {
            throw new Exception("El campo phone no puede ser nulo ni vacío");
        }

        // Validar que el email no sea nulo ni vacío
        if(Objects.isNull(createUserRequest.getEmail())
        || createUserRequest.getEmail().isBlank()) {
            throw new Exception("El campo email no puede ser nulo ni vacío");
        }

        // Validar que el campo documentTypeId no sea nulo
        if (createUserRequest.getDocumentTypeId() == null || createUserRequest.getDocumentTypeId() <= 0) {
            throw new Exception("El campo documentTypeId debe contener un valor mayor a 0");
        }

        // Validar el campo documentNumber no sea nulo ni vacío
        if (createUserRequest.getDocumentNumber() == null || createUserRequest.getDocumentNumber().isBlank()) {
            throw new Exception("El campo documentNumber no puede estar nulo ni vacío");
        }

        // Validar campo birthDate
        if (Objects.isNull(createUserRequest.getBirthDate()) || createUserRequest.getBirthDate().isBlank()) {
            throw new Exception("El campo birthDate no puede estar nulo ni vacío");
        }

        // Validar campo country
        if (Objects.isNull(createUserRequest.getCountry()) || createUserRequest.getCountry().isBlank()) {
            throw new Exception("El campo country no puede estar nulo ni vacío");
        }

        // Validar campo address
        if (Objects.isNull(createUserRequest.getAddress()) || createUserRequest.getAddress().isBlank()) {
            throw new Exception("El campo address no puede estar nulo ni vacío");
        }

        // Validar que existe el document type, para no tener problemas de integridad referencial
        DocumentType documentType = documentTypeRepository.findById(createUserRequest.getDocumentTypeId())
                .orElseThrow(() -> new Exception("El tipo de documento no existe"));

        // Validar que no exista un usuario creado con el mismo email
        if (userRepository.existsByEmail(createUserRequest.getEmail())) {
            throw new Exception("Ya existe un usuario con el email ingresado");
        }

        // Validar que no exista un usuario creado con el mismo documento y tipo de documento
        if (userRepository.existsByDocumentNumberAndDocumentTypeId(
                createUserRequest.getDocumentNumber(), createUserRequest.getDocumentTypeId())) {
            throw new Exception("Ya existe un usuario con el documento y tipo de documento ingresados");
        }

        // Convertir el objeto createUserRequest a User
        User user = User.builder()
                .fullName(createUserRequest.getFullName())
                .phone(createUserRequest.getPhone())
                .email(createUserRequest.getEmail())
                .documentType(documentType)
                .documentNumber(createUserRequest.getDocumentNumber())
                .birthDate(
                        LocalDate.parse(
                                createUserRequest.getBirthDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .country(createUserRequest.getCountry())
                .address(createUserRequest.getAddress())
                .build();

        user = userRepository.save(user); // Persistir el usuario en la base de datos
        UserResponse userResponse = UserMapper.modelToUserResponse(user); // Mapear a Response
        return userResponse; // Retornar el Response
    }
}
