package ti.elibreriaalfa.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Obtener todos los usuarios")
    @GetMapping
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Object> getAllUsuarios() {
        return new ResponseEntity<>(usuarioService.getAllUsuarios(), HttpStatus.OK);
    }

    @Operation(summary = "Obtener todos los elementos de usuario")
    @GetMapping("/elements")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Object> getElements() {
        return new ResponseEntity<>(usuarioService.getElements(), HttpStatus.OK);
    }

    @Operation(summary = "Obtener todos los elementos de usuario filtrados por texto de búsqueda y orden")
    @GetMapping("/filteredElements")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Object> getElementsFiltrados(
            @RequestParam(name = "textoBusqueda", required = false) String textoBusqueda,
            @RequestParam(name = "orden", required = false) String orden
    ) {
        return new ResponseEntity<>(usuarioService.getElementsFiltrados(textoBusqueda, orden), HttpStatus.OK);
    }

    @Operation(summary = "Obtener todos los usuarios con paginación")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Object> getUsuariosPage(@RequestParam(name = "pagina") Integer pagina,
                                                  @RequestParam(name = "cantidad") Integer cantidad) {
        return new ResponseEntity<>(usuarioService.getUsuariosPage(pagina, cantidad), HttpStatus.OK);
    }

    @Operation(summary = "Obtener usuario por email")
    @GetMapping("/{usuarioEmail}")
    @PreAuthorize("#email == authentication.name or hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Object> getUsuarioById(@PathVariable (name = "usuarioEmail") String usuarioEmail) {
        return new ResponseEntity<>(usuarioService.getUsuarioByEmail(usuarioEmail), HttpStatus.OK);
    }

    @Operation(summary = "Registrar un nuevo usuario")
    @PostMapping("/register")
    public ResponseEntity<Object> registerUsuario(@RequestBody AccesoUsuarioDto usuario) {
        return new ResponseEntity<>(usuarioService.registerUsuario(usuario), HttpStatus.CREATED);
    }

    @Operation(summary = "Crear un nuevo usuario")
    @PostMapping()
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Object> createUsuario(@RequestBody UsuarioDto usuario) {
        return new ResponseEntity<>(usuarioService.createUsuario(usuario), HttpStatus.CREATED);
    }

    @Operation(summary = "Modificar un usuario")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Object> modifyUsuario(
            @PathVariable(name = "id") Long id,
            @RequestBody UsuarioDto usuario
    ) {
        return new ResponseEntity<>(usuarioService.modifyUsuario(id, usuario), HttpStatus.OK);
    }

    @Operation(summary = "Modificar perfil de usuario")
    @PatchMapping("/{usuarioEmail}/profile")
    public ResponseEntity<Object> patchPerfilUsuario(@PathVariable (name = "usuarioEmail") String usuarioEmail,
                                                     @RequestBody ModificarPerfilUsuarioDto perfilUsuario) {
        return new ResponseEntity<>(usuarioService.patchPerfilUsuario(usuarioEmail, perfilUsuario), HttpStatus.OK);
    }

    @Operation(summary = "Obtener usuario por ID")
    @GetMapping("/id/{usuarioId}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR') || hasAuthority('EMPLEADO')")
    public ResponseEntity<Object> getUsuarioById(@PathVariable(name = "usuarioId") Long usuarioId) {
        return new ResponseEntity<>(usuarioService.getUsuarioById(usuarioId), HttpStatus.OK);
    }
}
