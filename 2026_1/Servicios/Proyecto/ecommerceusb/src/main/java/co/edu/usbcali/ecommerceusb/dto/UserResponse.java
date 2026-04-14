package co.edu.usbcali.ecommerceusb.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserResponse {
    /*
    * Esto es una clase Inmutable
    * La cual solo vamos a instanciar por medio del Builder
    * No se le pueden modificar los valores a sus atributos
    * */
    private Integer id;
    private String fullName;
    private String email;
    private Integer documentTypeId;
    private String documentTypeName;
    private String documentNumber;
}
