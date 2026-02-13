package com.angularSek.DemoSek.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Date;

@Builder
@Getter
public class ProductoResponseDTO {

    private Integer idProducto;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private String imagen;
    private Date fechaCreacion;

}
