package co.edu.usbcali.aerolineaplus.service;

import co.edu.usbcali.aerolineaplus.dto.CiudadDTO;
import java.util.List;

public interface CiudadService {

    CiudadDTO guardarNuevaCiudad(CiudadDTO ciudadDTO) throws Exception;
    CiudadDTO buscarCiudadPorId(Integer id) throws Exception;
    CiudadDTO modificarCiudad(CiudadDTO ciudadDTO) throws Exception;
    List<CiudadDTO> obtenerCiudades();
}
