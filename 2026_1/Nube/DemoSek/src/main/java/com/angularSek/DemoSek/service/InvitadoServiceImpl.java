package com.angularSek.DemoSek.service;

import com.angularSek.DemoSek.dto.InvitadoRequestDTO;
import com.angularSek.DemoSek.dto.InvitadoResponseDTO;
import com.angularSek.DemoSek.domain.Invitado;
import com.angularSek.DemoSek.mapper.InvitadoMapper;
import com.angularSek.DemoSek.repository.InvitadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InvitadoServiceImpl implements InvitadoService {

    private final InvitadoRepository invitadoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<InvitadoResponseDTO> getInvitados() {
        List<Invitado> list = invitadoRepository.findAll();
        return InvitadoMapper.domainListToResponseList(list);
    }

    @Override
    @Transactional(readOnly = true)
    public InvitadoResponseDTO findInvitadoById(Integer id) {
        Optional<Invitado> optional = invitadoRepository.findById(id);
        return optional
                .map(InvitadoMapper::domainToResponseDTO)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public InvitadoResponseDTO saveInvitado(InvitadoRequestDTO dto) throws Exception {

        if (dto == null)
            throw new Exception("El invitado no puede ser nulo.");

        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty())
            throw new Exception("El nombre es obligatorio.");

        Invitado invitado = InvitadoMapper.requestToDomain(dto);
        invitado = invitadoRepository.save(invitado);

        return InvitadoMapper.domainToResponseDTO(invitado);
    }
}

