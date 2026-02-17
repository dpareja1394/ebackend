package co.edu.usbcali.ecommerceusb.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Integer id;
    private String fullName;
    private String phone;
    private String email;
    private String documentNumber;
    private String birthDate;
    private String country;
    private String address;
    private String createdAt;
    private String updatedAt;
}