package co.edu.usbcali.aerolineaplus.mapper;

import co.edu.usbcali.aerolineaplus.domain.Ciudad;
import co.edu.usbcali.aerolineaplus.dto.CiudadDTO;

import java.util.List;

public class CiudadMapper {

    public static Ciudad dtoToDomain(CiudadDTO ciudadDTO) {
        return Ciudad.builder()
                .id(ciudadDTO.getId())
                .nombre(ciudadDTO.getNombre())
                .descripcion(ciudadDTO.getDescripcion())
                .latitud(ciudadDTO.getLatitud())
                .longitud(ciudadDTO.getLongitud())
                .build();
    }

    public static CiudadDTO domainToDto(Ciudad ciudad) {
        return CiudadDTO.builder()
                .id(ciudad.getId())
                .nombre(ciudad.getNombre())
                .descripcion(ciudad.getDescripcion())
                .latitud(ciudad.getLatitud())
                .longitud(ciudad.getLongitud())
                .paisId((ciudad.getPais() != null) ? ciudad.getPais().getId() : null)
                .build();
    }

    public static List<Ciudad> dtoToDomainList(List<CiudadDTO> ciudadesDTO) {
        return ciudadesDTO.stream().map(CiudadMapper::dtoToDomain).toList();
    }

    public static List<CiudadDTO> domainToDTOList(List<Ciudad> ciudades) {
        return ciudades.stream().map(CiudadMapper::domainToDto).toList();
    }

}
