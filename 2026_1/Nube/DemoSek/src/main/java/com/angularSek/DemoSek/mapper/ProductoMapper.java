package com.angularSek.DemoSek.mapper;


import com.angularSek.DemoSek.domain.Producto;
import com.angularSek.DemoSek.dto.ProductoRequestDTO;
import com.angularSek.DemoSek.dto.ProductoResponseDTO;

import java.util.List;

public class ProductoMapper {

    public static ProductoResponseDTO domainToResponseDTO(Producto domain) {

        ProductoResponseDTO response = ProductoResponseDTO.builder()
                .idProducto(domain.getIdProducto())
                .nombre(domain.getNombre())
                .descripcion(domain.getDescripcion())
                .precio(domain.getPrecio())
                .stock(domain.getStock())
                .imagen(domain.getImagen())
                .fechaCreacion(domain.getFechaCreacion())
                .build();

        return response;
    }

    public static List<ProductoResponseDTO> domainListToResponseList(List<Producto> list) {
        return list.stream()
                .map(ProductoMapper::domainToResponseDTO)
                .toList();
    }

    public static Producto requestToDomain(ProductoRequestDTO dto) {

        Producto producto = Producto.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .precio(dto.getPrecio())
                .stock(dto.getStock())
                .imagen(dto.getImagen())
                .fechaCreacion(dto.getFechaCreacion())
                .build();

        return producto;
    }
}
