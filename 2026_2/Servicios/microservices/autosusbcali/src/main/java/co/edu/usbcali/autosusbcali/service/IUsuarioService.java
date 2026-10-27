package co.edu.usbcali.autosusbcali.service;

import co.edu.usbcali.autosusbcali.dto.response.ObtenerUsuarioResponse;

import java.util.List;

public interface IUsuarioService {
    ObtenerUsuarioResponse obtenerUsuarioPorId(Long id) throws Exception;
    List<ObtenerUsuarioResponse> obtenerUsuarios();
}
