package ti.elibreriaalfa.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ti.elibreriaalfa.api.requests.usuario.RequestAccesoUsuario;
import ti.elibreriaalfa.business.entities.Usuario;
import ti.elibreriaalfa.business.repositories.UsuarioRepository;

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

    public Usuario authenticateUsuario(RequestAccesoUsuario datosUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(datosUsuario.getUsuario().getEmail());
        if (usuario == null) {
            throw new IllegalArgumentException("No existe un usuario con el correo electrónico especificado");
        } else if (!passwordEncoder.matches(datosUsuario.getUsuario().getContrasenia(), usuario.getContrasenia())) {
            throw new IllegalArgumentException("Contraseña incorrecta");
        }
        return usuario;
    }

    public String getRolByUsuario(Usuario usuario) {
        return usuario.getRol().name();
    }
}
