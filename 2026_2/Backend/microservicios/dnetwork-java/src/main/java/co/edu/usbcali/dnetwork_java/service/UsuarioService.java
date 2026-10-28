package co.edu.usbcali.dnetwork_java.service;

import co.edu.usbcali.dnetwork_java.dto.response.ObtenerUsuarioResponse;

import java.util.List;

public interface UsuarioService {
    // Método para obtener TODOS los usuarios
    List<ObtenerUsuarioResponse> obtenerUsuarios();

    // Método para obtener un usuario por su Id
    ObtenerUsuarioResponse obtenerUsuarioPorId(Integer id) throws Exception;

    // Elimina un usuario siempre que no tenga publicaciones asociadas
    void eliminarUsuario(Integer id) throws Exception;

    // Activa un usuario que se encuentra inactivo
    ObtenerUsuarioResponse activarUsuario(Integer id) throws Exception;

    // Desactiva un usuario que se encuentra activo
    ObtenerUsuarioResponse desactivarUsuario(Integer id) throws Exception;
}
