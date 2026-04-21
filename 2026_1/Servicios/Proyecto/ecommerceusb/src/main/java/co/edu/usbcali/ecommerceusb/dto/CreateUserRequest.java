package co.edu.usbcali.ecommerceusb.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@lombok.Getter
public class CreateUserRequest {
    private String fullName;
    private String phone;
    private String email;
    private Integer documentTypeId;
    private String documentNumber;
    private String birthDate;
    private String country;
    private String address;
}
