package co.edu.usbcali.cinema.service;

import co.edu.usbcali.cinema.dto.UserAccountRequestDTO;
import co.edu.usbcali.cinema.dto.UserAccountResponseDTO;

import java.util.List;

public interface UserAccountService {
    List<UserAccountResponseDTO> getUserAccounts();
    UserAccountResponseDTO saveUserAccount(UserAccountRequestDTO userAccountRequestDTO) throws Exception;

}
