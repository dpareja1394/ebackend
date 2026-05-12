/**
 * Controlador REST para la gestión de usuarios.
 *
 * <p>Expone endpoints para crear, obtener y actualizar usuarios.
 * Las validaciones de entrada se realizan mediante Jakarta Bean Validation
 * y se activan con la anotación {@code @Valid} en los parámetros.</p>
 *
 * @author USB
 * @since 1.0
 */
package co.edu.usbcali.ecommerceusb.controller;

import co.edu.usbcali.ecommerceusb.dto.CreateUserRequest;
import co.edu.usbcali.ecommerceusb.dto.UpdateUserRequest;
import co.edu.usbcali.ecommerceusb.dto.UserResponse;
import co.edu.usbcali.ecommerceusb.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Obtiene todos los usuarios.
     *
     * @return lista de usuarios (UserResponse)
     */
    @GetMapping("/all")
    public List<UserResponse> getAll() {
        return userService.getUsers();
    }

    /**
     * Obtiene un usuario por su id.
     *
     * @param id identificador del usuario
     * @return UserResponse con datos del usuario
     * @throws Exception si el id es inválido o no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Integer id) throws Exception {
        return new ResponseEntity<>(userService.getUserById(id),
                HttpStatus.OK);
    }

    /**
     * Obtiene un usuario por email.
     *
     * @param email correo del usuario
     * @return UserResponse con datos del usuario
     * @throws Exception si el email no está presente
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) throws Exception {
        return new ResponseEntity<>(userService.getUserByEmail(email),
                HttpStatus.OK);
    }

    /**
     * Crea un nuevo usuario.
     *
     * @param createUserRequest DTO con los datos del nuevo usuario
     * @return UserResponse creado
     * @throws Exception en caso de validaciones de negocio
     */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest createUserRequest) throws Exception {
        return new ResponseEntity<>(userService.createUser(createUserRequest),
                HttpStatus.CREATED);
    }

    /**
     * Actualiza un usuario existente (completo, PUT).
     *
     * @param id id del usuario a actualizar
     * @param updateUserRequest DTO con los nuevos datos
     * @return UserResponse actualizado
     * @throws Exception en caso de validaciones de negocio o si no existe
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable("id") Integer id,
            @Valid @RequestBody UpdateUserRequest updateUserRequest) throws Exception {
        return new ResponseEntity<>(userService.updateUser(id, updateUserRequest),
            HttpStatus.OK);
    }
}
