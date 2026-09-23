package co.edu.usbcali.dnetwork_java.service;

import co.edu.usbcali.dnetwork_java.domain.Publicacion;
import co.edu.usbcali.dnetwork_java.domain.Usuario;
import co.edu.usbcali.dnetwork_java.domain.enums.PrivacidadPublicacion;
import co.edu.usbcali.dnetwork_java.dto.request.CrearPublicacionRequest;
import co.edu.usbcali.dnetwork_java.dto.response.ObtenerPublicacionResponse;
import co.edu.usbcali.dnetwork_java.mapper.PublicacionMapper;
import co.edu.usbcali.dnetwork_java.repository.PublicacionRepository;
import co.edu.usbcali.dnetwork_java.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class PublicacionServiceImpl implements PublicacionService {

    // Inyección de dependencias del Repository
    @Autowired
    private PublicacionRepository publicacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ObtenerPublicacionResponse> obtenerPublicaciones() {
        return PublicacionMapper.
                listaPublicacionesHaciaListaObtenerPublicacionesResponse(
                        publicacionRepository.findAll()
                );
    }

    @Override
    @Transactional(readOnly = true)
    public ObtenerPublicacionResponse obtenerPublicacionPorId(Integer id) throws Exception {
        if (id == null) {
            throw new Exception("El id no puede ser nulo");
        }
        if (id <= 0) {
            throw new Exception("El valor del id no puede ser inferior o igual a cero para buscar");
        }
        Optional<Publicacion> publicacionOptional = publicacionRepository.findById(id);
        if(publicacionOptional.isEmpty()) {
            throw new Exception("No existe la publicación");
        }

        return PublicacionMapper.publicacionAObtenerPublicacionResponse(publicacionOptional.get());
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public ObtenerPublicacionResponse crearPublicacion(CrearPublicacionRequest crearPublicacionRequest) throws Exception {
        // Validar objeto #CrearPublicacionRequest
        if (crearPublicacionRequest == null) {
            throw new Exception("El objeto a crear no puede ser nulo");
        }
        if (crearPublicacionRequest.autorId() == null ||
                crearPublicacionRequest.autorId() <= 0) {
            throw new Exception("El id del autor no puede ser nulo ni inferior o igual a cero");
        }
        if (crearPublicacionRequest.contenido() == null ||
                crearPublicacionRequest.contenido().isBlank()) {
            throw new Exception("El contenido de la publicación no puede ser nulo ni vacío");
        }
        if (crearPublicacionRequest.privacidadPublicacion() == null ||
                crearPublicacionRequest.privacidadPublicacion().isBlank()) {
            throw new Exception("No ha llegado un valor para privacidad-publicacion");
        }

        // Validar existencia del Autor en base de datos, tener en cuenta que el Autor es una llave foránea de Publicación
        Usuario autor = usuarioRepository
                .findById(crearPublicacionRequest.autorId())
                .orElseThrow(() ->
                        new Exception("No se ha encontrado el autor con el id: " + crearPublicacionRequest.autorId() + "."));

        // Validar que la privacidad se encuentre en el Enum
        if (Arrays.stream(PrivacidadPublicacion.values()).noneMatch(privacidad ->
                privacidad.toString().equalsIgnoreCase(crearPublicacionRequest.privacidadPublicacion()))) {
            throw new Exception("No se ha encontrado la privacidad-publicacion: " + crearPublicacionRequest.privacidadPublicacion() + ".");
        }

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


}
