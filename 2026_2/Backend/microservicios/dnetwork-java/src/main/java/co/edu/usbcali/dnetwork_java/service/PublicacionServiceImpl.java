package co.edu.usbcali.dnetwork_java.service;

import co.edu.usbcali.dnetwork_java.domain.Publicacion;
import co.edu.usbcali.dnetwork_java.domain.Usuario;
import co.edu.usbcali.dnetwork_java.domain.enums.PrivacidadPublicacion;
import co.edu.usbcali.dnetwork_java.dto.request.CrearPublicacionRequest;
import co.edu.usbcali.dnetwork_java.dto.request.ModificarPublicacionRequest;
import co.edu.usbcali.dnetwork_java.dto.response.ObtenerPublicacionResponse;
import co.edu.usbcali.dnetwork_java.mapper.PublicacionMapper;
import co.edu.usbcali.dnetwork_java.repository.PublicacionRepository;
import co.edu.usbcali.dnetwork_java.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PublicacionServiceImpl implements PublicacionService {

    // Inyección de dependencias del Repository
    @Autowired
    private PublicacionRepository publicacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ObtenerPublicacionResponse> obtenerPublicaciones() {
        log.info("Se ha invocado método obtenerPublicaciones");
        return PublicacionMapper.
                listaPublicacionesHaciaListaObtenerPublicacionesResponse(
                        publicacionRepository.findAll()
                );
    }

    @Override
    @Transactional(readOnly = true)
    public ObtenerPublicacionResponse obtenerPublicacionPorId(Integer id) throws Exception {
        log.info("Se ha invocado método obtenerPublicacionPorId con argumentos id: "+id);
        return PublicacionMapper.publicacionAObtenerPublicacionResponse(obtenerPublicacion(id));
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public ObtenerPublicacionResponse crearPublicacion(CrearPublicacionRequest crearPublicacionRequest) throws Exception {
        log.info(
                "Se ha invocado método crearPublicacion con objeto crearPublicacionRequest: {}",
                crearPublicacionRequest);
        // Validar objeto #CrearPublicacionRequest
        if (crearPublicacionRequest == null) {
            log.error("El objeto a crear no puede ser nulo");
            throw new Exception("El objeto a crear no puede ser nulo");
        }
        if (crearPublicacionRequest.autorId() == null ||
                crearPublicacionRequest.autorId() <= 0) {
            log.error("El id del autor no puede ser nulo ni inferior o igual a cero");
            throw new Exception("El id del autor no puede ser nulo ni inferior o igual a cero");
        }
        if (crearPublicacionRequest.contenido() == null ||
                crearPublicacionRequest.contenido().isBlank()) {
            log.error("El contenido de la publicación no puede ser nulo ni vacío");
            throw new Exception("El contenido de la publicación no puede ser nulo ni vacío");
        }

        // Validar que la privacidad exista en el Enum
        validarPrivacidadPublicacion(crearPublicacionRequest.privacidadPublicacion());

        // Validar existencia del Autor en base de datos, tener en cuenta que el Autor es una llave foránea de Publicación
        Usuario autor = usuarioRepository
                .findById(crearPublicacionRequest.autorId())
                .orElseThrow(() -> {
                    log.error("No se ha encontrado el autor con el id: {}.", crearPublicacionRequest.autorId());
                    return new Exception("No se ha encontrado el autor con el id: " + crearPublicacionRequest.autorId() + ".");
                });

        // Convertir desde el Request hacia el Entity
        Publicacion publicacion = PublicacionMapper.crearPublicacionRequestAPublicacion(crearPublicacionRequest);

        // Agregar el Autor al Entity Publicacion
        publicacion.setAutor(autor);

        // Persistir Publicacion
        publicacion = publicacionRepository.save(publicacion);

        // Convertir desde el Entity hacia el Response
        ObtenerPublicacionResponse publicacionResponse = PublicacionMapper.publicacionAObtenerPublicacionResponse(publicacion);

        // Retornar Response
        return publicacionResponse;
    }

    // Implementar la lógica para modificar una publicación
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public ObtenerPublicacionResponse modificarPublicacion(Integer id, ModificarPublicacionRequest modificarPublicacionRequest) throws Exception {
        log.info("Se ha invocado método modificarPublicacion con argumentos id: {} y objeto modificarPublicacionRequest: {}", id, modificarPublicacionRequest);
        // Validar los atributos del objeto modificarPublicacionRequest
        if (modificarPublicacionRequest == null) {
            log.error("El objeto a modificar no puede ser nulo");
            throw new Exception("El objeto a modificar no puede ser nulo");
        }
        if (modificarPublicacionRequest.contenido() == null ||
                modificarPublicacionRequest.contenido().isBlank()) {
            log.error("El contenido de la publicación no puede ser nulo ni vacío");
            throw new Exception("El contenido de la publicación no puede ser nulo ni vacío");
        }
        PrivacidadPublicacion privacidad = validarPrivacidadPublicacion(modificarPublicacionRequest.privacidadPublicacion());
        Publicacion publicacion = obtenerPublicacion(id);

        // Set de la publicación con los atributos que llegaron
        publicacion.setContenido(modificarPublicacionRequest.contenido());
        publicacion.setPrivacidad(privacidad);

        publicacion = publicacionRepository.save(publicacion);

        // Mapear la publicación modificada a el DTO de respuesta ObtenerPublicacionResponse
        return PublicacionMapper.publicacionAObtenerPublicacionResponse(publicacion);
    }

    // Eliminación física de una publicación
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void eliminarPublicacion(Integer id) throws Exception {
        log.info("Se ha invocado método eliminarPublicacion con argumentos id: {}", id);
        validarId(id);
        Publicacion publicacion = obtenerPublicacion(id);
        // Borrado físico
        publicacionRepository.delete(publicacion);
        log.info("Se ha eliminado la publicación con id: {}", id);
    }


    // Valida que el texto de privacidad llegue con dato y exista en el Enum, retorna el valor del Enum
    private PrivacidadPublicacion validarPrivacidadPublicacion(String privacidadPublicacion) throws Exception {
        if (privacidadPublicacion == null || privacidadPublicacion.isBlank()) {
            log.error("No ha llegado un valor para privacidad-publicacion");
            throw new Exception("No ha llegado un valor para privacidad-publicacion");
        }
        PrivacidadPublicacion privacidad = PrivacidadPublicacion.getPrivacidadPublicacion(privacidadPublicacion);
        if (privacidad == null) {
            log.error("No se ha encontrado la privacidad-publicacion: " + privacidadPublicacion + ".");
            throw new Exception("No se ha encontrado la privacidad-publicacion: " + privacidadPublicacion + ".");
        }
        return privacidad;
    }

    private Publicacion obtenerPublicacion(Integer id) throws Exception {
        validarId(id);
        // Validar que existe la publicación por ID
        return publicacionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("No existe la publicación con el id: {}.", id);
                    return new Exception("No existe la publicación con el id: " + id + ".");
                });
    }

    private void validarId(Integer id) throws Exception {
        // Validar que id de la publicación trae dato
        if (id == null) {
            log.error("El id no puede ser nulo");
            throw new Exception("El id no puede ser nulo");
        }
        if (id <= 0) {
            log.error("El valor del id no puede ser inferior o igual a cero para eliminar");
            throw new Exception("El valor del id no puede ser inferior o igual a cero para eliminar");
        }
    }

}
