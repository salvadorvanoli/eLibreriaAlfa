package ti.elibreriaalfa.api.controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ti.elibreriaalfa.dtos.usuario.AccesoUsuarioDto;
import ti.elibreriaalfa.security.SeguridadService;

@RestController
@RequestMapping("/security")
public class SeguridadController {

    @Autowired
    private SeguridadService seguridadService;

    @Value("${JWT_KEY}")
    private String key;

    @Value("${JWT_EXPIRATION}")
    private String expiration;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> getUsuarioActual() {
        return new ResponseEntity<>(seguridadService.getUsuarioActual(), HttpStatus.OK);
    }

    @PostMapping("/auth")
    @Transactional(readOnly = true)
    public ResponseEntity<Void> authenticateUser(@RequestBody AccesoUsuarioDto datosUsuario, HttpServletResponse response) {
        seguridadService.authenticateUser(datosUsuario, response);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        seguridadService.logout(response);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
