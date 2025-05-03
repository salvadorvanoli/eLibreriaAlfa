package ti.elibreriaalfa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ti.elibreriaalfa.business.entities.Usuario;
import ti.elibreriaalfa.business.repositories.UsuarioRepository;
import ti.elibreriaalfa.dtos.usuario.AccesoUsuarioDto;
import ti.elibreriaalfa.dtos.usuario.ModificarPerfilUsuarioDto;
import ti.elibreriaalfa.dtos.usuario.UsuarioSimpleDto;
import ti.elibreriaalfa.exceptions.usuario.UsuarioException;
import ti.elibreriaalfa.exceptions.usuario.UsuarioNoEncontradoException;
import ti.elibreriaalfa.exceptions.usuario.UsuarioYaExisteException;

import java.util.List;

@Service
public class UsuarioService {
    @Autowired
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UsuarioSimpleDto> getAllUsuarios() {
        return usuarioRepository.findAll().stream().map(Usuario::mapToDtoSimple).toList();
    }

    public Page<UsuarioSimpleDto> getUsuariosPage(Integer pagina, Integer cantidad) {
        PageRequest pageRequest = PageRequest.of(pagina, cantidad);
        Sort sort = Sort.by(Sort.Direction.DESC, "email");

        return usuarioRepository.findAll(pageRequest.withSort(sort)).map(Usuario::mapToDtoSimple);
    }

    public UsuarioSimpleDto getUsuarioByEmail(String usuarioEmail) {
        return getUsuarioEntityByEmail(usuarioEmail).mapToDtoSimple();
    }

    public UsuarioSimpleDto registerUsuario(AccesoUsuarioDto usuario) {
        validateRegistroUsuarioDto(usuario);

        Usuario nuevoUsuario = usuario.mapToEntity();

        if (usuarioRepository.existsByEmail(usuario.getEmail())) throw new UsuarioYaExisteException("El correo electrónico ya está registrado");

        String contraseniaEncriptada = passwordEncoder.encode(nuevoUsuario.getContrasenia());
        nuevoUsuario.setContrasenia(contraseniaEncriptada);

        usuarioRepository.save(nuevoUsuario);

        return nuevoUsuario.mapToDtoSimple();
        /*
        try {
            usuarioRepository.save(nuevoUsuario);
        } catch (DataIntegrityViolationException e) {
            throw new UsuarioException("Ya existe un usuario registrado con ese correo electrónico");
        }

        return nuevoUsuario.mapToDto();
        */
    }

    public UsuarioSimpleDto patchPerfilUsuario(String usuarioEmail, ModificarPerfilUsuarioDto perfilUsuario) {
        Usuario usuario = getUsuarioEntityByEmail(usuarioEmail);

        usuario.setDatosPerfil(perfilUsuario);
        usuarioRepository.save(usuario);

        return usuario.mapToDtoSimple();
    }

    private Usuario getUsuarioEntityById(Long usuarioId) throws UsuarioNoEncontradoException {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("No existe un usuario con el ID especificado"));
    }

    private Usuario getUsuarioEntityByEmail(String email) throws UsuarioNoEncontradoException {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario != null)
            return usuario;
        else
            throw new UsuarioNoEncontradoException("No existe un usuario con el correo electrónico especificado");
    }

    private void validateRegistroUsuarioDto(AccesoUsuarioDto usuario) {
        if (usuario.getEmail() == null || usuario.getEmail().isBlank())
            throw new UsuarioException("El correo electrónico no puede estar vacío");
        else if (usuario.getContrasenia() == null || usuario.getContrasenia().length() < 6)
            throw new UsuarioException("La contraseña debe tener al menos 6 caracteres");
    }

}
