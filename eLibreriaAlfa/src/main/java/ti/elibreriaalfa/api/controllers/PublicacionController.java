package ti.elibreriaalfa.api.controllers;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ti.elibreriaalfa.api.responses.publicacion.ResponseListadoPublicaciones;
import ti.elibreriaalfa.api.responses.publicacion.ResponsePublicacion;
import ti.elibreriaalfa.dtos.comentario.ComentarioDto;
import ti.elibreriaalfa.dtos.publicacion.PublicacionDto;
import ti.elibreriaalfa.services.ComentarioService;
import ti.elibreriaalfa.services.PublicacionService;

@RestController
@RequestMapping(value = "publication")
public class PublicacionController {
    private final PublicacionService publicacionService;
    private final ComentarioService comentarioService;


    public PublicacionController(PublicacionService publicacionService, ComentarioService comentarioService) {
        this.publicacionService = publicacionService;
        this.comentarioService = comentarioService;
    }

    @GetMapping
    public ResponseEntity<ResponseListadoPublicaciones> getAllPublicaciones() {
        return new ResponseEntity<>(publicacionService.getAllPublicaciones(), HttpStatus.OK);
    }

    @GetMapping("/elements")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Object> getElements() {
        return new ResponseEntity<>(publicacionService.getElements(), HttpStatus.OK);
    }

    @GetMapping("/filteredElements")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Object> getElementsFiltrados(
            @RequestParam(name = "textoBusqueda", required = false) String textoBusqueda,
            @RequestParam(name = "orden", required = false) String orden
    ) {
        return new ResponseEntity<>(publicacionService.getElementsFiltrados(textoBusqueda, orden), HttpStatus.OK);
    }

    @GetMapping("/{idPublicacion}")
    public ResponseEntity<Object> getPublicacionById(@PathVariable(name = "idPublicacion") Long idPublicacion) {
        try {
            ResponsePublicacion response = publicacionService.getPublicacionById(idPublicacion);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/page")
    public ResponseEntity<Page<PublicacionDto>> getPublicacionPage(@RequestParam(name = "pagina") Integer pagina, @RequestParam(name = "cantidad") Integer cantidad) {
        return new ResponseEntity<>(publicacionService.listadoPublicacionPage(pagina, cantidad), HttpStatus.OK);
    }

    @GetMapping("/page/date")
    public ResponseEntity<Page<PublicacionDto>> getPublicacionPageByFechaDESC(@RequestParam(name = "pagina") Integer pagina, @RequestParam(name = "cantidad") Integer cantidad) {
        return new ResponseEntity<>(publicacionService.listadoPublicacionPageByFechaDESC(pagina, cantidad), HttpStatus.OK);
    }

    @GetMapping("/comment/page")
    public ResponseEntity<Page<ComentarioDto>> getComentariosPageByPublicacion(@RequestParam(name = "pagina") Integer pagina, @RequestParam(name = "cantidad") Integer cantidad, @RequestParam(name = "idPublicacion") Long idPublicacion) {
        return new ResponseEntity<>(comentarioService.listadoComentariosPageByPublicacion(pagina, cantidad, idPublicacion), HttpStatus.OK);
    }

    @PostMapping
    //@PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<String> createPublicacion(@RequestBody PublicacionDto publicacionDto) {
        String response = publicacionService.crearPublicacion(publicacionDto);

        if(response == null) {
            return new ResponseEntity<>("Error al crear la publicación", HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Void> borrarPublicacion(@PathVariable(name = "id") Long idPublicacion) {
        publicacionService.borrarPublicacion(idPublicacion);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/comment")
    public ResponseEntity<String> createComentarioEnPublicacion(@RequestBody ComentarioDto comentarioDto) {
        String response = comentarioService.crearComentario(comentarioDto);

        System.out.println("Llegué a entrar");

        if(response == null) {
            return new ResponseEntity<>("Error al crear el comentario", HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
    }

    @DeleteMapping("/comment/{id}")
    public ResponseEntity<Void> borrarComentario(@PathVariable(name = "id") Long idComentario) {
        comentarioService.borrarComentario(idComentario);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
