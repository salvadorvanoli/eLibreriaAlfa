package ti.elibreriaalfa.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ti.elibreriaalfa.dtos.usuario.AccesoUsuarioDto;
import ti.elibreriaalfa.dtos.usuario.ModificarPerfilUsuarioDto;
import ti.elibreriaalfa.dtos.usuario.UsuarioDto;
import ti.elibreriaalfa.services.UsuarioService;

@RestController
@RequestMapping(value = "user")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Object> getAllUsuarios() {
        return new ResponseEntity<>(usuarioService.getAllUsuarios(), HttpStatus.OK);
    }

    @GetMapping("/elements")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Object> getElements() {
        return new ResponseEntity<>(usuarioService.getElements(), HttpStatus.OK);
    }

    @GetMapping("/filteredElements")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Object> getElementsFiltrados(
            @RequestParam(name = "textoBusqueda", required = false) String textoBusqueda,
            @RequestParam(name = "orden", required = false) String orden
    ) {
        return new ResponseEntity<>(usuarioService.getElementsFiltrados(textoBusqueda, orden), HttpStatus.OK);
    }

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Object> getUsuariosPage(@RequestParam(name = "pagina") Integer pagina,
                                                  @RequestParam(name = "cantidad") Integer cantidad) {
        return new ResponseEntity<>(usuarioService.getUsuariosPage(pagina, cantidad), HttpStatus.OK);
    }

    @GetMapping("/{usuarioEmail}")
    @PreAuthorize("#email == authentication.name or hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Object> getUsuarioById(@PathVariable (name = "usuarioEmail") String usuarioEmail) {
        return new ResponseEntity<>(usuarioService.getUsuarioByEmail(usuarioEmail), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<Object> registerUsuario(@RequestBody AccesoUsuarioDto usuario) {
        return new ResponseEntity<>(usuarioService.registerUsuario(usuario), HttpStatus.CREATED);
    }

    @PostMapping()
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Object> createUsuario(@RequestBody UsuarioDto usuario) {
        return new ResponseEntity<>(usuarioService.createUsuario(usuario), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Object> modifyUsuario(@RequestBody UsuarioDto usuario) {
        return new ResponseEntity<>(usuarioService.createUsuario(usuario), HttpStatus.CREATED);
    }


    @PatchMapping("/{usuarioEmail}/profile")
    public ResponseEntity<Object> patchPerfilUsuario(@PathVariable (name = "usuarioEmail") String usuarioEmail,
                                                     @RequestBody ModificarPerfilUsuarioDto perfilUsuario) {
        return new ResponseEntity<>(usuarioService.patchPerfilUsuario(usuarioEmail, perfilUsuario), HttpStatus.OK);
    }
}
