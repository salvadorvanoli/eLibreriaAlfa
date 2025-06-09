package ti.elibreriaalfa.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ti.elibreriaalfa.business.entities.Usuario;
import ti.elibreriaalfa.business.repositories.UsuarioRepository;
import ti.elibreriaalfa.dtos.usuario.AccesoUsuarioDto;
import ti.elibreriaalfa.dtos.usuario.UsuarioSimpleDto;
import ti.elibreriaalfa.exceptions.usuario.UsuarioNoEncontradoException;
import ti.elibreriaalfa.exceptions.usuario.UsuarioInicioSesionException;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeguridadService {

    @Autowired
    private final UsuarioRepository usuarioRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Value("${JWT_KEY}")
    private String key;

    @Value("${JWT_EXPIRATION}")
    private String expiration;

    public SeguridadService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioSimpleDto getUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null)
            throw new UsuarioNoEncontradoException("No existe un usuario con ese correo electrónico");

        return usuario.mapToDtoSimple();
    }

    public void authenticateUser (AccesoUsuarioDto datosUsuario, HttpServletResponse response) {
        Usuario usuario = this.authenticateUsuario(datosUsuario);
        String token = generateToken(usuario);

        ResponseCookie tokenCookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                /*.sameSite("None")
                .secure(true)*/
                .sameSite("Lax")
                .secure(false)
                .maxAge(Integer.parseInt(expiration))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, tokenCookie.toString());
    }

    public void logout(HttpServletResponse response) {
        ResponseCookie deleteTokenCookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, deleteTokenCookie.toString());
    }

    private String getRolByUsuario(Usuario usuario) {
        return usuario.getRol().name();
    }

    private Usuario authenticateUsuario(AccesoUsuarioDto datosUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(datosUsuario.getEmail());

        if (usuario == null) {
            throw new UsuarioNoEncontradoException("No existe un usuario con ese correo electrónico");
        } else if (!passwordEncoder.matches(datosUsuario.getContrasenia(), usuario.getContrasenia())) {
            throw new UsuarioInicioSesionException("Contraseña incorrecta");
        }

        return usuario;
    }

    private String generateToken(Usuario usuario) {
        List<GrantedAuthority> grantedAuthorityList
                = AuthorityUtils.createAuthorityList(
                this.getRolByUsuario(usuario)
        );

        String token = Jwts
                .builder()
                .setId(String.valueOf(usuario.getId()))
                .setSubject(usuario.getEmail())
                .claim("authorities",
                        grantedAuthorityList.stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList())
                )
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(expiration) * 1000))
                .signWith(SignatureAlgorithm.HS512, key.getBytes())
                .compact();

        return token;
    }
}
