package co.edu.usbcali.ecommerceusb.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DocumentTypeResponse {
    private Integer id;
    private String code;
    private String name;
}
