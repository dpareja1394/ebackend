package com.angularSek.DemoSek.controller;

import com.angularSek.DemoSek.dto.ProductoRequestDTO;
import com.angularSek.DemoSek.dto.ProductoResponseDTO;
import com.angularSek.DemoSek.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/productos")
@RestController
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping("/all")
    List<ProductoResponseDTO> getAll() {
        return productoService.getProductos();
    }

    @GetMapping("/{id}")
    ProductoResponseDTO getById(@PathVariable Integer id) {
        return productoService.findProductoById(id);
    }

    @PostMapping
    ResponseEntity<ProductoResponseDTO> save(@RequestBody ProductoRequestDTO dto) throws Exception {
        ProductoResponseDTO response = productoService.saveProducto(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}

