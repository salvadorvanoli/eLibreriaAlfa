package ti.elibreriaalfa.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.*;
import ti.elibreriaalfa.dtos.usuario.AccesoUsuarioDto;
import ti.elibreriaalfa.dtos.usuario.ModificarPerfilUsuarioDto;
import ti.elibreriaalfa.dtos.usuario.UsuarioSimpleDto;
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
        //try {
            return new ResponseEntity<>(usuarioService.getAllUsuarios(), HttpStatus.OK);
        /*} catch (AuthorizationDeniedException ex) {
            return new ResponseEntity<>("Acceso denegado", HttpStatus.FORBIDDEN);
        }*/
    }

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Object> getUsuariosPage(@RequestParam(name = "pagina") Integer pagina,
                                                  @RequestParam(name = "cantidad") Integer cantidad) {
        //try {
            return new ResponseEntity<>(usuarioService.getUsuariosPage(pagina, cantidad), HttpStatus.OK);
        /*} catch (AuthorizationDeniedException ex) {
            return new ResponseEntity<>("Acceso denegado", HttpStatus.FORBIDDEN);
        }*/
    }

    @GetMapping("/{usuarioEmail}")
    @PreAuthorize("#email == authentication.name or hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Object> getUsuarioById(@PathVariable (name = "usuarioEmail") String usuarioEmail) {
        //try {
            UsuarioSimpleDto response = usuarioService.getUsuarioByEmail(usuarioEmail);
            return new ResponseEntity<>(response, HttpStatus.OK);
        /*} catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (AuthorizationDeniedException ex) {
            return new ResponseEntity<>("Acceso denegado", HttpStatus.FORBIDDEN);
        }*/
    }

    @PostMapping("/register")
    public ResponseEntity<Object> registerUsuario(@RequestBody AccesoUsuarioDto usuario) {
        // try {
            return new ResponseEntity<>(usuarioService.registerUsuario(usuario), HttpStatus.CREATED);
        /*} catch (UsuarioBadRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (UsuarioException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>("Algo ha salido mal", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        */
    }

    @PatchMapping("/{usuarioEmail}/profile")
    @PreAuthorize("#email == authentication.name")
    public ResponseEntity<Object> patchPerfilUsuario(@PathVariable (name = "usuarioEmail") String usuarioEmail,
                                                     @RequestBody ModificarPerfilUsuarioDto perfilUsuario) {
        //try {
            return new ResponseEntity<>(usuarioService.patchPerfilUsuario(usuarioEmail, perfilUsuario), HttpStatus.OK);
        /*} catch (UsuarioBadRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (UsuarioException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>("Algo ha salido mal", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        */
    }
}
