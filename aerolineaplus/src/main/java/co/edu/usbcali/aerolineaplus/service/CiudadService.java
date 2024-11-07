package co.edu.usbcali.aerolineaplus.service;

import co.edu.usbcali.aerolineaplus.dto.CiudadDTO;
import co.edu.usbcali.aerolineaplus.dto.request.CreateCiudadRequest;
import co.edu.usbcali.aerolineaplus.dto.response.ListarCiudadesResponse;

import java.util.List;

public interface CiudadService {

    CiudadDTO guardarNuevaCiudad(CreateCiudadRequest createCiudadRequest) throws Exception;
    CiudadDTO buscarCiudadPorId(Integer id) throws Exception;
    CiudadDTO modificarCiudad(CiudadDTO ciudadDTO) throws Exception;
    List<ListarCiudadesResponse> obtenerCiudades();
    void eliminarCiudad(Integer id) throws Exception;
}
