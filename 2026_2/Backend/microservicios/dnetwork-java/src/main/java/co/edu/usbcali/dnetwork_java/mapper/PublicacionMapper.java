package co.edu.usbcali.dnetwork_java.mapper;

import co.edu.usbcali.dnetwork_java.domain.Publicacion;
import co.edu.usbcali.dnetwork_java.domain.enums.PrivacidadPublicacion;
import co.edu.usbcali.dnetwork_java.dto.request.CrearPublicacionRequest;
import co.edu.usbcali.dnetwork_java.dto.response.ObtenerPublicacionResponse;

import java.util.List;

public class PublicacionMapper {
    public static ObtenerPublicacionResponse publicacionAObtenerPublicacionResponse(Publicacion publicacion) {
        // Declaración e inicialización del DTO Response
        ObtenerPublicacionResponse publicacionResponse = new ObtenerPublicacionResponse(
                publicacion.getId(),
                // if ternario para validar que venga la información del autor
                publicacion.getAutor() != null ? publicacion.getAutor().getUsername() : "",
                // if ternario para validar que venga la información del autor
                publicacion.getAutor() != null ? publicacion.getAutor().getId() : null,
                publicacion.getContenido(),
                publicacion.getPrivacidad().toString(),
                publicacion.getCreatedAt(),
                publicacion.getUpdatedAt()
        );
        // Retorno del Response
        return publicacionResponse;
    }

    public static List<ObtenerPublicacionResponse> listaPublicacionesHaciaListaObtenerPublicacionesResponse(List<Publicacion> publicaciones) {
        return publicaciones.stream().map(PublicacionMapper::publicacionAObtenerPublicacionResponse).toList();
    }

    public static Publicacion crearPublicacionRequestAPublicacion(CrearPublicacionRequest publicacionRq) {
        Publicacion publicacion = Publicacion.builder()
                .contenido(publicacionRq.contenido())
                .privacidad(PrivacidadPublicacion.getPrivacidadPublicacion(publicacionRq.privacidadPublicacion()))
                .build();
        return publicacion;
    }
}
