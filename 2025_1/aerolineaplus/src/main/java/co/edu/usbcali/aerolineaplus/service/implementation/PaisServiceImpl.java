package co.edu.usbcali.aerolineaplus.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import co.edu.usbcali.aerolineaplus.domain.Pais;
import co.edu.usbcali.aerolineaplus.dto.PaisDTO;
import co.edu.usbcali.aerolineaplus.dto.request.CreatePaisRequest;
import co.edu.usbcali.aerolineaplus.mapper.PaisMapper;
import co.edu.usbcali.aerolineaplus.repository.CiudadRepository;
import co.edu.usbcali.aerolineaplus.repository.PaisRepository;
import co.edu.usbcali.aerolineaplus.service.PaisService;

@Service
public class PaisServiceImpl implements PaisService {

    private final PaisRepository paisRepository;
    private final CiudadRepository ciudadRepository;

    public PaisServiceImpl(PaisRepository paisRepository, CiudadRepository ciudadRepository) {
        this.paisRepository = paisRepository;
        this.ciudadRepository = ciudadRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PaisDTO buscarPaisPorId(Integer id) throws Exception {
        // Validar el id que no sea nulo y que sea diferente de cero
        if (id == null || id.equals(0)) {
            throw new Exception("El id no puede estar vacío ni ser cero (0)");
        }
        
        // Buscar el país por id si no existe lanzar una excepción
        Pais pais = paisRepository.findById(id)
            .orElseThrow(() -> new Exception("No se encuentra el país con el id " + id));

        // Convertir el país a un DTO y retornar
        return PaisMapper.domainToDTO(pais);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public PaisDTO guardarNuevoPais(CreatePaisRequest createPaisRequest) throws Exception {
        // 1. Validar el paisDTO que llegue al servicio
        if(createPaisRequest == null) {
            throw new Exception("El Pais no puede ser nulo");
        }
        // Convertir el paisDTO a un pais
        Pais pais = PaisMapper.createPaisRequestToDomain(createPaisRequest);

        // Guardar el país en la base de datos
        pais = paisRepository.save(pais);

        return PaisMapper.domainToDTO(pais);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaisDTO> obtenerPaises() {
        List<Pais> listaPaises = paisRepository.findAll();
        List<PaisDTO> paisesDTO = PaisMapper.domainToDTOList(listaPaises);
        return paisesDTO;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public PaisDTO modificarPais(PaisDTO paisDTO) throws Exception {
        // 1. Validar el paisDTO que llegue al servicio
        if(paisDTO == null) {
            throw new Exception("El paisDTO no puede ser nulo");
        }

        if (paisDTO.getId() == null || paisDTO.getId().equals(0)) {
            throw new Exception("El id del país no puede ser nulo o cero");
        }

        // Validar los campos del paisDTO
        if(paisDTO.getNombre() == null || paisDTO.getNombre().equals("")) {
            throw new Exception("El nombre del país no puede ser nulo o vacío");
        }

        if(paisDTO.getCodigo() == null || paisDTO.getCodigo().equals("")) {
            throw new Exception("El código del país no puede ser nulo o vacío");
        }

        if(paisDTO.getDescripcion() == null || paisDTO.getDescripcion().equals("")) {
            throw new Exception("La descripción del país no puede ser nula o vacía");
        }

        // Convertir el paisDTO a un pais
        Pais pais = PaisMapper.dtoToDomain(paisDTO);

        // Guardar el país en la base de datos
        pais = paisRepository.save(pais);

        return PaisMapper.domainToDTO(pais);
    }

    @Override
    @Transactional(readOnly = true)
    public PaisDTO buscarPaisPorCodigo(String codigo) throws Exception {
        // Validar el codigo que llegue al servicio
        if(codigo == null || codigo.equals("")) {
            throw new Exception("El código del país no puede ser nulo o vacío");
        }

        // Buscar el país por codigo si no existe lanzar una excepción
        Pais pais = paisRepository.findByCodigo(codigo)
            .orElseThrow(() -> new Exception("No se encuentra el país con el código " + codigo));

        // Convertir el país a un DTO y retornar
        return PaisMapper.domainToDTO(pais);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void eliminarPais(Integer id) throws Exception {
        // Primero debemos validar que el id no sea nulo y que tampoco sea cero
        if (id == null || id.equals(0)) {
            throw new Exception("El id del país no puede ser nulo o cero");
        }

        // Segundo debemos validar que exista el país con el id que nos están pasando
        Boolean existePais = paisRepository.existsById(id);
        if (existePais == false) {
            throw new Exception("No existe el país con el id" + id +" por lo tanto no se puede eliminar");
        }

        // Tercero validamos que el pais no tenga ciudades asociadas
        Boolean existeAlgunaCiudadAsociadaAlPais = ciudadRepository.existsByPaisId(id);
        if (existeAlgunaCiudadAsociadaAlPais == true) {
            throw new Exception("El país con el id " + id + 
            " tiene ciudades asociadas por lo tanto no se puede eliminar");
        }

        // Si el país no tiene ciudades asociadas, entonces lo eliminamos
        paisRepository.deleteById(id);
    }

    

}
