package co.edu.usbcali.ecommerceusb.mapper;

import co.edu.usbcali.ecommerceusb.dto.DocumentTypeResponse;
import co.edu.usbcali.ecommerceusb.model.DocumentType;

import java.util.ArrayList;
import java.util.List;

public class DocumentTypeMapper {

    public static DocumentTypeResponse modelToDocumentTypeResponse(DocumentType documentType) {
        return DocumentTypeResponse.builder()
                .id(documentType.getId())
                .code(documentType.getCode())
                .name(documentType.getName())
                .build();
    }

    /*
    * Este método va a iterar una lista de DocumentType y la va a convertir en
    *         una lista de documentTypeResponse */
    public static List<DocumentTypeResponse> modelToDocumentTypeResponseList(
            List<DocumentType> documentTypes) {
        // Implementación usando for each
        /*List<DocumentTypeResponse> documentTypeResponseList = new ArrayList<>();
        for(DocumentType documentType : documentTypes) {
            DocumentTypeResponse documentTypeResponse = modelToDocumentTypeResponse(documentType);
            documentTypeResponseList.add(documentTypeResponse);
        }
        return documentTypeResponseList;*/
        return documentTypes.stream().map(DocumentTypeMapper::modelToDocumentTypeResponse).toList();
    }

}
