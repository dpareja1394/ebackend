package co.edu.usbcali.dnetwork_java.service;

import co.edu.usbcali.dnetwork_java.dto.response.ObtenerUsuarioResponse;

import java.util.List;

public interface UsuarioService {
    // Método para obtener TODOS los usuarios
    List<ObtenerUsuarioResponse> obtenerUsuarios();

    // Método para obtener un usuario por su Id
    ObtenerUsuarioResponse obtenerUsuarioPorId(Integer id);

    // Elimina un usuario siempre que no tenga publicaciones asociadas
    void eliminarUsuario(Integer id);
}
