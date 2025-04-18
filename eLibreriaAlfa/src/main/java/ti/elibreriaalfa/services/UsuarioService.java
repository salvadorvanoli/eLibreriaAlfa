package ti.elibreriaalfa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ti.elibreriaalfa.api.requests.usuario.RequestAccesoUsuario;
import ti.elibreriaalfa.api.responses.usuario.ResponseListadoUsuarios;
import ti.elibreriaalfa.api.responses.usuario.ResponseUsuario;
import ti.elibreriaalfa.business.entities.Usuario;
import ti.elibreriaalfa.business.repositories.UsuarioRepository;
import ti.elibreriaalfa.dtos.usuario.AccesoUsuarioDto;
import ti.elibreriaalfa.dtos.usuario.UsuarioDto;

@Service
public class UsuarioService {
    @Autowired
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseListadoUsuarios getAllUsuarios() {
        ResponseListadoUsuarios response = new ResponseListadoUsuarios();
        response.setUsuarios(usuarioRepository.findAll().stream().map(Usuario::mapToDto).toList());
        return response;
    }

    public Page<UsuarioDto> getUsuariosPage(Integer pagina, Integer cantidad) {
        PageRequest pageRequest = PageRequest.of(pagina, cantidad);
        Sort sort = Sort.by(Sort.Direction.DESC, "email");

        return usuarioRepository.findAll(pageRequest.withSort(sort)).map(Usuario::mapToDto);
    }

    public ResponseUsuario getUsuarioByEmail(String usuarioEmail) {
        Usuario usuario = usuarioRepository.findByEmail(usuarioEmail);
        if (usuario == null) throw new IllegalArgumentException("No existe un usuario con el correo electrónico especificado");

        ResponseUsuario response = new ResponseUsuario();
        response.setUsuario(usuario.mapToDto());
        return response;
    }

    public String registerUsuario(RequestAccesoUsuario usuario) {
        validateRegistroUsuarioDto(usuario);

        Usuario nuevoUsuario = usuario.mapToEntity();

        String contraseniaEncriptada = passwordEncoder.encode(nuevoUsuario.getContrasenia());
        nuevoUsuario.setContrasenia(contraseniaEncriptada);

        usuarioRepository.save(nuevoUsuario);

        return "Usuario registrado exitosamente!";
        /*
        try {
            usuarioRepository.save(nuevoUsuario);
        } catch (DataIntegrityViolationException e) {
            throw new UsuarioException("Ya existe un usuario registrado con ese correo electrónico");
        }

        return nuevoUsuario.mapToDto();
        */
    }

    private Usuario getUsuarioEntityById(Long usuarioId) throws IllegalArgumentException {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("No existe un usuario con el ID especificado"));
    }

    private void validateRegistroUsuarioDto(RequestAccesoUsuario usuario) {
        // TODO: Implementar validación de los datos del usuario (email y contraseña válidos)
    }

}
