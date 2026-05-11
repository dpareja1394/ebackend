package co.edu.usbcali.ecommerceusb.controller;

import co.edu.usbcali.ecommerceusb.dto.CreateUserRequest;
import co.edu.usbcali.ecommerceusb.dto.UpdateUserRequest;
import co.edu.usbcali.ecommerceusb.dto.UserResponse;
import co.edu.usbcali.ecommerceusb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    // Inyección de dependencias de UserService
    @Autowired
    private UserService userService;

    @GetMapping("/all")
    public List<UserResponse> getAll() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Integer id) throws Exception {
        return new ResponseEntity<>(userService.getUserById(id),
                HttpStatus.OK);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) throws Exception {
        return new ResponseEntity<>(userService.getUserByEmail(email),
                HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @RequestBody CreateUserRequest createUserRequest) throws Exception {
        return new ResponseEntity<>(userService.createUser(createUserRequest),
                HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable("id") Integer id,
            @RequestBody UpdateUserRequest updateUserRequest) throws Exception {
        return new ResponseEntity<>(userService.updateUser(id, updateUserRequest),
            HttpStatus.OK);
    }
}
