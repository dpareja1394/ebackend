package com.angularSek.DemoSek.service;


import com.angularSek.DemoSek.dto.ProductoRequestDTO;
import com.angularSek.DemoSek.dto.ProductoResponseDTO;

import java.util.List;

public interface ProductoService {

    List<ProductoResponseDTO> getProductos();

    ProductoResponseDTO findProductoById(Integer id);

    ProductoResponseDTO saveProducto(ProductoRequestDTO producto) throws Exception;

}