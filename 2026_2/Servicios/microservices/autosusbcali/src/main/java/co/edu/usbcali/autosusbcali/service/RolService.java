package co.edu.usbcali.autosusbcali.service;

import co.edu.usbcali.autosusbcali.domain.Rol;
import co.edu.usbcali.autosusbcali.dto.request.CrearModificarRolRequest;
import co.edu.usbcali.autosusbcali.dto.response.ObtenerRolResponse;
import co.edu.usbcali.autosusbcali.mapper.RolMapper;
import co.edu.usbcali.autosusbcali.repository.RolRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RolService implements IRolService{
    private final RolRepository rolRepository;

    @Override
    public List<ObtenerRolResponse> obtenerTodos() {
        return RolMapper.listaRolesAListaObtenerRolResponse(rolRepository.findAll());
    }

    @Override
    public List<ObtenerRolResponse> buscarRolPorEstado(Boolean activo) {
        List<Rol> rolesPorEstado = rolRepository.findByActivo(activo);
        return RolMapper.listaRolesAListaObtenerRolResponse(rolesPorEstado);
    }

    @Override
    public ObtenerRolResponse obtenerRolPorNombre(String nombre) {
        Rol rolPorNombre = rolRepository.findByNombre(nombre)
                .orElseThrow(() -> new RuntimeException("No se ha encontrado el rol por nombre: "+nombre));
        return RolMapper.rolAObtenerRolResponse(rolPorNombre);
    }

    @Override
    public ObtenerRolResponse obtenerRolPorId(Long id) {
        Rol rolPorId = rolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se ha encontrado el rol por id "+id));
        return RolMapper.rolAObtenerRolResponse(rolPorId);
    }

    @Override
    public ObtenerRolResponse crearRol(CrearModificarRolRequest crearModificarRolRequest) throws Exception {
        // Validaciones del objeto #CrearModificarRolRequest
        if (crearModificarRolRequest == null) {
            throw new Exception("El objeto CrearModificarRolRequest no puede ser nulo.");
        }
        if (crearModificarRolRequest.nombre() == null
            || crearModificarRolRequest.nombre().isBlank()) {
            throw new Exception("El nombre no puede ser nulo ni estar vacío.");
        }
        if (crearModificarRolRequest.descripcion() == null
            || crearModificarRolRequest.descripcion().isBlank()) {
            throw new Exception("La descripcion no puede ser nulo ni estar vacía.");
        }
        // Buscar si existe un rol con el nombre que se quiere agregar Opción 1 traer el objeto
        /*
        Optional<Rol> rolPorNombre = rolRepository.findByNombre(crearModificarRolRequest.nombre());
        if (rolPorNombre.isPresent()) {
            throw new Exception("Ya existe un rol con el nombre "+crearModificarRolRequest.nombre());
        }*/

        // Buscar si existe un rol con el nombre que se quiere agregar Opción 2 buscar boolean
        if (rolRepository.existsByNombre(crearModificarRolRequest.nombre())) {
            throw new Exception("Ya existe un rol con el nombre "+crearModificarRolRequest.nombre());
        }

        // Usar mapper para convertir DTO a objeto de Domain
        Rol rol = RolMapper.CrearModificarRolRequestARol(crearModificarRolRequest);

        // Persistir el Rol en base de datos
        rol = rolRepository.save(rol);

        // Crear DTO response obtenerRolResponse
        ObtenerRolResponse rolResponse = RolMapper.rolAObtenerRolResponse(rol);

        // Retornar rolResponse
        return rolResponse;
    }

    @Override
    public ObtenerRolResponse modificarRol(Long id, CrearModificarRolRequest modificarRolRequest) throws Exception {
        return null;
    }
}
