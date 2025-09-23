package co.edu.usbcali.cinema.service;

import co.edu.usbcali.cinema.domain.Role;
import co.edu.usbcali.cinema.domain.UserAccount;
import co.edu.usbcali.cinema.dto.UserAccountRequestDTO;
import co.edu.usbcali.cinema.dto.UserAccountResponseDTO;
import co.edu.usbcali.cinema.mapper.UserAccountMapper;
import co.edu.usbcali.cinema.repository.RoleRepository;
import co.edu.usbcali.cinema.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserAccountResponseDTO> getUserAccounts() {
        return UserAccountMapper.entityToResponseDTOList(
                userAccountRepository.findAll()
        );
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public UserAccountResponseDTO saveUserAccount(UserAccountRequestDTO userAccountRequestDTO) throws Exception {
        // Validaciones de negocio, que ninguno de los campos sea nulo
        if (userAccountRequestDTO == null) {
            throw new Exception("UserAccountRequestDTO is null");
        }
        if (userAccountRequestDTO.getFirstName() == null || userAccountRequestDTO.getFirstName().isBlank()) {
            throw new Exception("UserAccountRequestDTO firstName is null");
        }
        // Las otras validaciones

        // Validación de dependencia con Rol
        // Validar si existe el rol por el id
        Role role = roleRepository.findById(userAccountRequestDTO.getRoleId())
                        .orElseThrow(() -> new Exception("Role not found"));

        // Convertir a Entity desde el RequestDTO
        UserAccount userAccount = UserAccountMapper.requestDTOToEntity(userAccountRequestDTO);
        // Agregar el rol al usuario para que sea guardado
        userAccount.setRole(role);

        // Persistir el usuario en la base de datos
        userAccount = userAccountRepository.save(userAccount);

        return UserAccountMapper.entityToResponseDTO(userAccount);
    }
}
