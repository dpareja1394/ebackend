package co.edu.usbcali.cinema.mapper;

import co.edu.usbcali.cinema.domain.UserAccount;
import co.edu.usbcali.cinema.dto.UserAccountRequestDTO;
import co.edu.usbcali.cinema.dto.UserAccountResponseDTO;

import java.util.List;

public class UserAccountMapper {

    public static UserAccount requestDTOToEntity(UserAccountRequestDTO userAccountRequestDTO) {
        return UserAccount.builder()
                .firstName(userAccountRequestDTO.getFirstName())
                .lastName(userAccountRequestDTO.getLastName())
                .email(userAccountRequestDTO.getEmail())
                .phone(userAccountRequestDTO.getPhone())
                .password(userAccountRequestDTO.getPassword())
                .build();
    }

    public static UserAccountResponseDTO entityToResponseDTO(UserAccount userAccount) {
        return UserAccountResponseDTO.builder()
                .id(userAccount.getId())
                .firstName(userAccount.getFirstName())
                .lastName(userAccount.getLastName())
                .email(userAccount.getEmail())
                .phone(userAccount.getPhone())
                .status(userAccount.getStatus())
                .roleName( (userAccount.getRole() != null) ?
                        userAccount.getRole().getName() : null )
                .roleId( (userAccount.getRole() != null) ?
                        userAccount.getRole().getId() : null )
                .build();
    }

    public static List<UserAccountResponseDTO> entityToResponseDTOList(List<UserAccount> userAccounts) {
        return userAccounts.stream().map(UserAccountMapper::entityToResponseDTO).toList();
    }

}
