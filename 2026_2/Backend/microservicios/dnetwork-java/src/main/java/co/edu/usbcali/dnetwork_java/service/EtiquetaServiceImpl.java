package co.edu.usbcali.dnetwork_java.service;

import co.edu.usbcali.dnetwork_java.domain.Etiqueta;
import co.edu.usbcali.dnetwork_java.dto.request.CrearEtiquetaRequest;
import co.edu.usbcali.dnetwork_java.dto.response.ObtenerEtiquetaResponse;
import co.edu.usbcali.dnetwork_java.mapper.EtiquetaMapper;
import co.edu.usbcali.dnetwork_java.repository.EtiquetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EtiquetaServiceImpl implements EtiquetaService{
    @Autowired
    private EtiquetaRepository etiquetaRepository;

    @Override
    public List<ObtenerEtiquetaResponse> obtenerEtiquetas() {
        List<Etiqueta> todasLasEtiquetas = etiquetaRepository.findAll();
        List<ObtenerEtiquetaResponse> etiquetasResponses =
                EtiquetaMapper.listaEtiquetasHaciaListaObtenerEtiquetasResponse(todasLasEtiquetas);
        return etiquetasResponses;
    }

    @Override
    public ObtenerEtiquetaResponse obtenerEtiquetaPorId(Integer id) throws Exception {
        // Validar que id no sea nulo
        if (id == null) {
            throw new Exception("El id no puede ser nulo");
        }
        // Validar que id no tenga valor inferior a cero
        if (id <= 0) {
            throw new Exception("El valor del id no puede ser inferior o igual a cero para buscar");
        }

        // Buscar la Etiqueta por medio de un Optional en la base de datos usando Repository
        Optional<Etiqueta> etiquetaOptional = etiquetaRepository.findById(id);

        // Si la etiqueta no fue encontrada, lanzar la excepción
        if (etiquetaOptional.isEmpty()) {
            throw new Exception("No se ha encontrado la Etiqueta con el id: "+id);
        }

        // Si la etiqueta fue encontrada, se debe mapear hacia el Response usando el Mapper
        ObtenerEtiquetaResponse etiquetaResponse =
                EtiquetaMapper.etiquetaAObtenerEtiquetaResponse(etiquetaOptional.get());

        // Retornar el record (objeto inmutable) ObtenerEtiquetaResponse
        return etiquetaResponse;
    }

    @Override
    public ObtenerEtiquetaResponse crearEtiqueta(CrearEtiquetaRequest crearEtiqueta) throws Exception {
        return null;
    }
}
