package co.edu.usbcali.autosusbcali.mapper;

import co.edu.usbcali.autosusbcali.domain.Rol;
import co.edu.usbcali.autosusbcali.dto.request.CrearModificarRolRequest;
import co.edu.usbcali.autosusbcali.dto.response.ObtenerRolResponse;
import tools.jackson.core.tree.ObjectTreeNode;

import java.util.ArrayList;
import java.util.List;

public class RolMapper {
    public static ObtenerRolResponse rolAObtenerRolResponse(Rol rol) {
        ObtenerRolResponse obtenerRolResponse =
                new ObtenerRolResponse(rol.getId(), rol.getNombre());
        return obtenerRolResponse;
    }

    public static List<ObtenerRolResponse> listaRolesAListaObtenerRolResponse(List<Rol> roles) {
        return roles.stream().map(RolMapper::rolAObtenerRolResponse).toList();
    }

    // Método que convierte un Request de Crear Rol a un Rol (Entidad de Dominio)
    public static Rol CrearModificarRolRequestARol(CrearModificarRolRequest crearModificarRolRequest) {
        // Forma 1 usando el constructor
        return new Rol(null,
                crearModificarRolRequest.nombre(),
                crearModificarRolRequest.descripcion(),
                true);

        // Forma 2 usando el constructor vacío
        /*Rol rol = new Rol();
        rol.setNombre(crearModificarRolRequest.nombre());
        rol.setDescripcion(crearModificarRolRequest.descripcion());
        rol.setActivo(true);
        return rol;*/
    }
}
