package co.edu.usbcali.autosusbcali.service;

import co.edu.usbcali.autosusbcali.domain.Usuario;
import co.edu.usbcali.autosusbcali.dto.response.ObtenerUsuarioResponse;
import co.edu.usbcali.autosusbcali.mapper.UsuarioMapper;
import co.edu.usbcali.autosusbcali.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UsuarioService implements IUsuarioService {
    private final UsuarioRepository usuarioRepository;

    @Override
    public ObtenerUsuarioResponse obtenerUsuarioPorId(Long id) throws Exception{
        if (id == null || id <= 0) {
            throw new  Exception("El id no puede ser nulo ni menor o igual a cero");
        }
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isEmpty()) {
            throw new  Exception("El usuario no existe");
        }
        return UsuarioMapper.usuarioAObtenerUsuarioResponse(usuario.get());
    }

    @Override
    public List<ObtenerUsuarioResponse> obtenerUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return UsuarioMapper.listaUsuariosAListaObtenerUsuarioResponse(usuarios);
    }
}
