package co.edu.usbcali.dnetwork_java.service;

import co.edu.usbcali.dnetwork_java.dto.request.CrearEtiquetaRequest;
import co.edu.usbcali.dnetwork_java.dto.response.ObtenerEtiquetaResponse;

import java.util.List;

public interface EtiquetaService {
    List<ObtenerEtiquetaResponse> obtenerEtiquetas();
    ObtenerEtiquetaResponse obtenerEtiquetaPorId(Integer id) throws Exception;
    ObtenerEtiquetaResponse crearEtiqueta(CrearEtiquetaRequest crearEtiqueta) throws Exception;
}
