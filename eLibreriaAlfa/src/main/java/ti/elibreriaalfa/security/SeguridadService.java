package ti.elibreriaalfa.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ti.elibreriaalfa.business.entities.Usuario;
import ti.elibreriaalfa.business.repositories.UsuarioRepository;
import ti.elibreriaalfa.dtos.usuario.AccesoUsuarioDto;

@Service
public class SeguridadService {

    @Autowired
    private final UsuarioRepository usuarioRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    public SeguridadService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario authenticateUsuario(AccesoUsuarioDto datosUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(datosUsuario.getEmail());
        if (usuario == null) {
            throw new IllegalArgumentException("No existe un usuario con el correo electrónico especificado");
        } else if (!passwordEncoder.matches(datosUsuario.getContrasenia(), usuario.getContrasenia())) {
            throw new IllegalArgumentException("Contraseña incorrecta");
        }
        return usuario;
    }

    public String getRolByUsuario(Usuario usuario) {
        return usuario.getRol().name();
    }
}
