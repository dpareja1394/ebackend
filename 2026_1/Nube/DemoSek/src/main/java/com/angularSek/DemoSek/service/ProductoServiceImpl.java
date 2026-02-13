package com.angularSek.DemoSek.service;

import com.angularSek.DemoSek.domain.Producto;
import com.angularSek.DemoSek.dto.ProductoRequestDTO;
import com.angularSek.DemoSek.dto.ProductoResponseDTO;
import com.angularSek.DemoSek.mapper.ProductoMapper;
import com.angularSek.DemoSek.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> getProductos() {
        List<Producto> list = productoRepository.findAll();
        return ProductoMapper.domainListToResponseList(list);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponseDTO findProductoById(Integer id) {
        Optional<Producto> optional = productoRepository.findById(id);
        return optional
                .map(ProductoMapper::domainToResponseDTO)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public ProductoResponseDTO saveProducto(ProductoRequestDTO dto) throws Exception {

        if (dto == null)
            throw new Exception("El producto no puede ser nulo.");

        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty())
            throw new Exception("El nombre es obligatorio.");

        if (dto.getPrecio() == null)
            throw new Exception("El precio es obligatorio.");

        if (dto.getStock() == null)
            dto.setStock(0);

        Producto producto = ProductoMapper.requestToDomain(dto);

        producto = productoRepository.save(producto);

        return ProductoMapper.domainToResponseDTO(producto);
    }
}

