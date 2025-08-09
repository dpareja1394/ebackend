package co.edu.usbcali.aerolineaplus.service;

import co.edu.usbcali.aerolineaplus.dto.PaisDTO;
import co.edu.usbcali.aerolineaplus.dto.request.CreatePaisRequest;

import java.util.List;

public interface PaisService {
    PaisDTO buscarPaisPorId(Integer id) throws Exception;
    PaisDTO guardarNuevoPais(CreatePaisRequest createPaisRequest) throws Exception;
    List<PaisDTO> obtenerPaises();
    PaisDTO modificarPais(PaisDTO paisDTO) throws Exception;
    PaisDTO buscarPaisPorCodigo(String codigo) throws Exception;
    void eliminarPais(Integer id) throws Exception;
}
