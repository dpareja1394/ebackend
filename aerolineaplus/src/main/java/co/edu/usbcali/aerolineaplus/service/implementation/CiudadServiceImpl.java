package co.edu.usbcali.aerolineaplus.service.implementation;

import co.edu.usbcali.aerolineaplus.domain.Ciudad;
import co.edu.usbcali.aerolineaplus.domain.Pais;
import co.edu.usbcali.aerolineaplus.dto.CiudadDTO;
import co.edu.usbcali.aerolineaplus.mapper.CiudadMapper;
import co.edu.usbcali.aerolineaplus.repository.CiudadRepository;
import co.edu.usbcali.aerolineaplus.repository.PaisRepository;
import co.edu.usbcali.aerolineaplus.service.CiudadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CiudadServiceImpl implements CiudadService {

    private final CiudadRepository ciudadRepository;
    private final PaisRepository paisRepository;

    public CiudadServiceImpl(CiudadRepository ciudadRepository, PaisRepository paisRepository) {
        this.ciudadRepository = ciudadRepository;
        this.paisRepository = paisRepository;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public CiudadDTO guardarNuevaCiudad(CiudadDTO ciudadDTO) throws Exception {
        //Validacion 1 id debe ser null
        if (ciudadDTO.getId() != null) {
            throw new Exception("El id debe ser nulo");
        }

        // Validacion 2 Que llegue el nombre
        if (ciudadDTO.getNombre() == null || ciudadDTO.getNombre().equals("")) {
            throw new Exception("El nombre no debe ser nulo");
        }

        // Validacion 3 Que llegue la descripcion
        if (ciudadDTO.getDescripcion() == null || ciudadDTO.getDescripcion().equals("")) {
            throw new Exception("La descripción no debe ser nula");
        }

        if (ciudadDTO.getPaisId() == null ) {
            throw new Exception("El PaisId no debe ser nulo");
        }

        Ciudad ciudad = CiudadMapper.dtoToDomain(ciudadDTO);
        Pais pais = paisRepository.findById(ciudadDTO.getPaisId())
            .orElseThrow(() -> new Exception("El País no existe"));

        ciudad.setPais(pais);
        ciudad = ciudadRepository.save(ciudad);

        return CiudadMapper.domainToDto(ciudad);
    }

    @Override
    @Transactional(readOnly = true)
    public CiudadDTO buscarCiudadPorId(Integer id) throws Exception {
        // Lógica para buscar la ciudad por id retornando el DTO
        //Validación del Id que llega por parámetro
        if(id == null || id.equals(0)) {
            throw new Exception("El id no puede estar vacío ni ser cero (0)");
        }

        Ciudad ciudad = ciudadRepository.findById(id)
        .orElseThrow(() -> new Exception("No se encuentra la ciudad con el id "+id));

        CiudadDTO ciudadDTO = CiudadMapper.domainToDto(ciudad);

        return ciudadDTO;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public CiudadDTO modificarCiudad(CiudadDTO ciudadDTO) throws Exception {
        // Lógica de validaciones similar al guardar Ciudad
        //Validacion 1 id debe ser null
        if (ciudadDTO.getId() == null) {
            throw new Exception("El id no puede ser nulo");
        }

        // Validacion 2 Que llegue el nombre
        if (ciudadDTO.getNombre() == null || ciudadDTO.getNombre().equals("")) {
            throw new Exception("El nombre no debe ser nulo");
        }

        // Validacion 3 Que llegue la descripcion
        if (ciudadDTO.getDescripcion() == null || ciudadDTO.getDescripcion().equals("")) {
            throw new Exception("La descripción no debe ser nula");
        }

        if (ciudadDTO.getPaisId() == null ) {
            throw new Exception("El PaisId no debe ser nulo");
        }

        Ciudad ciudad = CiudadMapper.dtoToDomain(ciudadDTO);
        Pais pais = paisRepository.findById(ciudadDTO.getPaisId())
            .orElseThrow(() -> new Exception("El País no existe"));

        ciudad.setPais(pais);
        ciudad = ciudadRepository.save(ciudad);

        return CiudadMapper.domainToDto(ciudad);
    }
}
