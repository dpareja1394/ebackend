package co.edu.usbcali.dnetwork_java.service;

import co.edu.usbcali.dnetwork_java.domain.Publicacion;
import co.edu.usbcali.dnetwork_java.dto.request.CrearPublicacionRequest;
import co.edu.usbcali.dnetwork_java.dto.response.ObtenerPublicacionResponse;
import co.edu.usbcali.dnetwork_java.mapper.PublicacionMapper;
import co.edu.usbcali.dnetwork_java.repository.PublicacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PublicacionServiceImpl implements PublicacionService {

    // Inyección de dependencias del Repository
    @Autowired
    private PublicacionRepository publicacionRepository;

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

        // Validar existencia del Autor

        // Persistir Publicacion

        // Retornar Response
        return null;
    }


}
