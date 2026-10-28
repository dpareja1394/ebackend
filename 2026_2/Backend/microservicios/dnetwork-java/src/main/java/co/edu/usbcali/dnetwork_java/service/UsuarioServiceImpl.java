package co.edu.usbcali.dnetwork_java.service;

import co.edu.usbcali.dnetwork_java.domain.Usuario;
import co.edu.usbcali.dnetwork_java.dto.response.ObtenerUsuarioResponse;
import co.edu.usbcali.dnetwork_java.mapper.UsuarioMapper;
import co.edu.usbcali.dnetwork_java.repository.PublicacionRepository;
import co.edu.usbcali.dnetwork_java.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PublicacionRepository publicacionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ObtenerUsuarioResponse> obtenerUsuarios() {
        return UsuarioMapper.listaUsuariosHaciaListaObtenerUsuariosResponse(usuarioRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public ObtenerUsuarioResponse obtenerUsuarioPorId(Integer id) throws Exception {
        return UsuarioMapper.usuarioAObtenerUsuarioResponse(obtenerUsuario(id));
    }

    // Eliminación física de un usuario, solo si no tiene publicaciones
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void eliminarUsuario(Integer id) throws Exception {
        Usuario usuario = obtenerUsuario(id);

        if (publicacionRepository.existsByAutorId(id)) {
            throw new Exception("No se puede eliminar el usuario con id: " + id + " porque tiene publicaciones asociadas.");
        }

        usuarioRepository.delete(usuario);
    }

    private Usuario obtenerUsuario(Integer id) throws Exception {
        if (id == null) {
            throw new Exception("El id no puede ser nulo");
        }
        if (id <= 0) {
            throw new Exception("El valor del id no puede ser inferior o igual a cero");
        }
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new Exception("No existe el usuario con el id: " + id + "."));
    }
}
