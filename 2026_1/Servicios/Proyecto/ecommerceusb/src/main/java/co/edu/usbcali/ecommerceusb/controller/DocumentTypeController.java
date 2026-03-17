package co.edu.usbcali.ecommerceusb.controller;

import co.edu.usbcali.ecommerceusb.model.DocumentType;
import co.edu.usbcali.ecommerceusb.repository.DocumentTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/document-type")
public class DocumentTypeController {

    @Autowired
    private DocumentTypeRepository documentTypeRepository;

    @GetMapping("/all")
    public List<DocumentType> getAll() {
        return documentTypeRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentType> getById(@PathVariable Integer id) {
        return new ResponseEntity<>(
                documentTypeRepository.getReferenceById(id),
                HttpStatus.OK
        );
    }
}
