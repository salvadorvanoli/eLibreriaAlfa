package ti.elibreriaalfa.api.controllers;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ti.elibreriaalfa.business.entities.Usuario;
import ti.elibreriaalfa.dtos.usuario.AccesoUsuarioDto;
import ti.elibreriaalfa.security.SeguridadService;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/security")
public class SeguridadController {

    @Autowired
    private SeguridadService seguridadService;

    @Value("${JWT_KEY}")
    private String key;

    @Value("${JWT_EXPIRATION}")
    private String expiration;

    @PostMapping("/auth")
    @Transactional(readOnly = true)
    public ResponseEntity<String> authenticateUser(@RequestBody AccesoUsuarioDto datosUsuario, HttpServletResponse response) {
        try {
            Usuario usuario = seguridadService.authenticateUsuario(datosUsuario);
            String token = generateToken(usuario);

            ResponseCookie tokenCookie = ResponseCookie.from("token", token)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .sameSite("None")
                    .maxAge(Integer.parseInt(expiration))
                    .build();

            ResponseCookie emailCookie = ResponseCookie.from("userEmail", usuario.getEmail())
                    .httpOnly(false)
                    .secure(false)
                    .path("/")
                    .sameSite("None")
                    .maxAge(Integer.parseInt(expiration))
                    .build();

            ResponseCookie idCookie = ResponseCookie.from("userId", usuario.getId().toString())
                    .httpOnly(false)
                    .secure(false)
                    .path("/")
                    .sameSite("None")
                    .maxAge(Integer.parseInt(expiration))
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, tokenCookie.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, emailCookie.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, idCookie.toString());

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    private String generateToken(Usuario usuario) {
        List<GrantedAuthority> grantedAuthorityList
                = AuthorityUtils.createAuthorityList(
                        seguridadService.getRolByUsuario(usuario)
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
                .setExpiration(new Date(System.currentTimeMillis() + Integer.parseInt(expiration)))
                .signWith(SignatureAlgorithm.HS512, key.getBytes())
                .compact();

        return token;
    }

}
