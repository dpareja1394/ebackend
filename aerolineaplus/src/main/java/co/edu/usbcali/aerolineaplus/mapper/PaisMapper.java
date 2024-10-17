package co.edu.usbcali.aerolineaplus.mapper;

import co.edu.usbcali.aerolineaplus.domain.Pais;
import co.edu.usbcali.aerolineaplus.dto.PaisDTO;

import java.util.List;

public class PaisMapper {

    //Método que convierte un PaisDTO en un objeto Entity Pais
    public static Pais dtoToDomain(PaisDTO paisDTO) {
        Pais pais = new Pais();
        pais.setId(paisDTO.getId());
        pais.setCodigo(paisDTO.getCodigo());
        pais.setNombre(paisDTO.getNombre());
        pais.setDescripcion(paisDTO.getDescripcion());
        return pais;
    }

    //Método que convierte un objeto Entity Pais en un objeto PaisDTO
    public static PaisDTO domainToDTO(Pais pais) {
        return PaisDTO.builder()
                .id(pais.getId())
                .codigo(pais.getCodigo())
                .nombre(pais.getNombre())
                .descripcion(pais.getDescripcion())
                .build();
    }

    public static List<Pais> dtoToDomainList(List<PaisDTO> paisesDTO) {
        /*List<Pais> paises = new ArrayList<>();

        for (PaisDTO paisDTO: paisesDTO) {
            Pais pais = dtoToDomain(paisDTO);
            paises.add(pais);
        }
        return paises;
        */
        return paisesDTO.stream().map(PaisMapper::dtoToDomain).toList();
    }

    public static List<PaisDTO> domainToDTOList(List<Pais> paises) {
        return paises.stream().map(PaisMapper::domainToDTO).toList();
    }

}
