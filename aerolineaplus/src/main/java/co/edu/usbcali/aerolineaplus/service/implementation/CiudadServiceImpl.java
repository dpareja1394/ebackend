package co.edu.usbcali.aerolineaplus.service.implementation;

import co.edu.usbcali.aerolineaplus.domain.Ciudad;
import co.edu.usbcali.aerolineaplus.domain.Pais;
import co.edu.usbcali.aerolineaplus.dto.CiudadDTO;
import co.edu.usbcali.aerolineaplus.mapper.CiudadMapper;
import co.edu.usbcali.aerolineaplus.repository.AeropuertoRepository;
import co.edu.usbcali.aerolineaplus.repository.CiudadRepository;
import co.edu.usbcali.aerolineaplus.repository.PaisRepository;
import co.edu.usbcali.aerolineaplus.service.CiudadService;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CiudadServiceImpl implements CiudadService {

    private final CiudadRepository ciudadRepository;
    private final PaisRepository paisRepository;
    private final AeropuertoRepository aeropuertoRepository;

    public CiudadServiceImpl(CiudadRepository ciudadRepository, PaisRepository paisRepository, AeropuertoRepository aeropuertoRepository) {
        this.ciudadRepository = ciudadRepository;
        this.paisRepository = paisRepository;
        this.aeropuertoRepository = aeropuertoRepository;
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

    @Override
    @Transactional(readOnly = true)
    public List<CiudadDTO> obtenerCiudades() {
        List<Ciudad> listaCiudades = ciudadRepository.findAll();
        List<CiudadDTO> listaCiudadesDTO = CiudadMapper.domainToDTOList(listaCiudades);
        return listaCiudadesDTO;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void eliminarCiudad(Integer id) throws Exception {
        // Primero debemos validar que el id no sea nulo y que tampoco sea cero
        if (id == null || id.equals(0)) {
            throw new Exception("El id de la ciudad no puede ser nulo o cero");
        }

        // Segundo debemos validar que exista la ciudad con el id que nos están pasando
        Boolean existeCiudad = ciudadRepository.existsById(id);
        if (existeCiudad == false) {
            throw new Exception("No existe la ciudad con el id " + id + " por lo tanto no se puede eliminar");
        }

        // Tercero debemos validar que la ciudad no tenga aeropuertos asociados
        Boolean existeAeropuerto = aeropuertoRepository.existsByCiudadId(id);
        if (existeAeropuerto == true) {
            throw new Exception("La ciudad con el id " + id + " tiene aeropuertos asociados por lo tanto no se puede eliminar");
        }

        // Si la ciudad no tiene aeropuertos asociados, entonces la eliminamos
        ciudadRepository.deleteById(id);
    }
}
