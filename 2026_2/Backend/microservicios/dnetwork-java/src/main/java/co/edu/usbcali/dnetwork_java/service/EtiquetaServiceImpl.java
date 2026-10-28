package co.edu.usbcali.dnetwork_java.service;

import co.edu.usbcali.dnetwork_java.domain.Etiqueta;
import co.edu.usbcali.dnetwork_java.dto.request.CrearEtiquetaRequest;
import co.edu.usbcali.dnetwork_java.dto.response.ObtenerEtiquetaResponse;
import co.edu.usbcali.dnetwork_java.exception.ConflictoException;
import co.edu.usbcali.dnetwork_java.exception.RecursoNoEncontradoException;
import co.edu.usbcali.dnetwork_java.exception.ValidacionException;
import co.edu.usbcali.dnetwork_java.mapper.EtiquetaMapper;
import co.edu.usbcali.dnetwork_java.repository.EtiquetaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class EtiquetaServiceImpl implements EtiquetaService{
    @Autowired
    private EtiquetaRepository etiquetaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ObtenerEtiquetaResponse> obtenerEtiquetas() {
        log.info("Se ha invocado método obtenerEtiquetas");
        List<Etiqueta> todasLasEtiquetas = etiquetaRepository.findAll();
        List<ObtenerEtiquetaResponse> etiquetasResponses =
                EtiquetaMapper.listaEtiquetasHaciaListaObtenerEtiquetasResponse(todasLasEtiquetas);
        return etiquetasResponses;
    }

    @Override
    @Transactional(readOnly = true)
    public ObtenerEtiquetaResponse obtenerEtiquetaPorId(Integer id) {
        log.info("Se ha invocado método obtenerEtiquetaPorId con argumentos id: {}", id);
        // Validar que id no sea nulo
        if (id == null) {
            log.error("El id no puede ser nulo");
            throw new ValidacionException("El id no puede ser nulo");
        }
        // Validar que id no tenga valor inferior a cero
        if (id <= 0) {
            log.error("El valor del id no puede ser inferior o igual a cero para buscar");
            throw new ValidacionException("El valor del id no puede ser inferior o igual a cero para buscar");
        }

        // Buscar la Etiqueta por medio de un Optional en la base de datos usando Repository
        Optional<Etiqueta> etiquetaOptional = etiquetaRepository.findById(id);

        // Si la etiqueta no fue encontrada, lanzar la excepción
        if (etiquetaOptional.isEmpty()) {
            log.error("No se ha encontrado la Etiqueta con el id: "+id);
            throw new RecursoNoEncontradoException("No se ha encontrado la Etiqueta con el id: "+id);
        }

        // Si la etiqueta fue encontrada, se debe mapear hacia el Response usando el Mapper
        ObtenerEtiquetaResponse etiquetaResponse =
                EtiquetaMapper.etiquetaAObtenerEtiquetaResponse(etiquetaOptional.get());

        // Retornar el record (objeto inmutable) ObtenerEtiquetaResponse
        return etiquetaResponse;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public ObtenerEtiquetaResponse crearEtiqueta(CrearEtiquetaRequest crearEtiqueta) {
        log.info("Se ha invocado método crearEtiqueta con objeto crearEtiqueta: {}", crearEtiqueta);
        // Validaciones lógicas para crear etiqueta
        // Validar que el objeto no sea nulo
        if (crearEtiqueta == null) {
            log.error("El objeto a crear no puede ser nulo");
            throw new ValidacionException("El objeto a crear no puede ser nulo");
        }

        // Validar que la etiqueta venga con nombre
        if (crearEtiqueta.nombre() == null || crearEtiqueta.nombre().isBlank()) {
            log.error("El nombre de la etiqueta no puede estar vacío ni ser nulo");
            throw new ValidacionException("El nombre de la etiqueta no puede estar vacío ni ser nulo");
        }

        // Validar con Repository que no exista una etiqueta con ese nombre
        Boolean existeEtiquetaPorNombre = etiquetaRepository.existsByNombre(crearEtiqueta.nombre());
        if (existeEtiquetaPorNombre) {
            log.error("El nombre de la etiqueta ya existe");
            throw new ConflictoException("El nombre de la etiqueta ya existe");
        }

        // Mapear desde el Request hacia la Entidad de Dominio
        Etiqueta etiqueta = EtiquetaMapper.crearEtiquetaRequestAEtiqueta(crearEtiqueta);

        // Persistir (almacenar) información en la base de datos
        etiqueta = etiquetaRepository.save(etiqueta);

        // Mapear desde Entidad de Dominio hacia el Response
        ObtenerEtiquetaResponse etiquetaResponse = EtiquetaMapper.etiquetaAObtenerEtiquetaResponse(etiqueta);

        // Método retorna EtiquetaResponse
        return etiquetaResponse;
    }
}
