package co.edu.usbcali.cinema.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserAccountResponseDTO {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Boolean status;
    private String roleName;
    private Integer roleId;
}
