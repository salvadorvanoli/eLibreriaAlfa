package ti.elibreriaalfa.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ti.elibreriaalfa.api.responses.publicacion.ResponseListadoPublicaciones;
import ti.elibreriaalfa.api.responses.publicacion.ResponsePublicacion;
import ti.elibreriaalfa.dtos.comentario.ComentarioDto;
import ti.elibreriaalfa.dtos.publicacion.PublicacionConImagenDto;
import ti.elibreriaalfa.dtos.publicacion.PublicacionDto;
import ti.elibreriaalfa.dtos.publicacion.PublicacionRequestDto;
import ti.elibreriaalfa.dtos.publicacion.PublicacionSimpleDto;
import ti.elibreriaalfa.services.ComentarioService;
import ti.elibreriaalfa.services.PublicacionService;

@RestController
@RequestMapping(value = "publication")
@Slf4j
public class PublicacionController {
    private final PublicacionService publicacionService;
    private final ComentarioService comentarioService;
    
    public PublicacionController(PublicacionService publicacionService, ComentarioService comentarioService) {
        this.publicacionService = publicacionService;
        this.comentarioService = comentarioService;
    }

    @Operation(summary = "Obtener todas las publicaciones")
    @GetMapping
    public ResponseEntity<ResponseListadoPublicaciones> getAllPublicaciones() {
        return new ResponseEntity<>(publicacionService.getAllPublicaciones(), HttpStatus.OK);
    }

    @Operation(summary = "Obtener elementos de publicaciones (Admin)")
    @GetMapping("/elements")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Object> getElements() {
        return new ResponseEntity<>(publicacionService.getElements(), HttpStatus.OK);
    }

    @Operation(summary = "Obtener elementos filtrados de publicaciones (Admin)")
    @GetMapping("/filteredElements")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Object> getElementsFiltrados(@RequestParam(name = "textoBusqueda", required = false) String textoBusqueda, @RequestParam(name = "orden", required = false) String orden) {
        return new ResponseEntity<>(publicacionService.getElementsFiltrados(textoBusqueda, orden), HttpStatus.OK);
    }

    @Operation(summary = "Obtener publicación por ID")
    @GetMapping("/{idPublicacion}")
    public ResponseEntity<ResponsePublicacion> getPublicacionById(@PathVariable(name = "idPublicacion") Long idPublicacion) {
        ResponsePublicacion response = publicacionService.getPublicacionById(idPublicacion);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Obtener publicación por  con imagen")
    @GetMapping("/{idPublicacion}/with-images")
    public ResponseEntity<PublicacionConImagenDto> getPublicacionConImagenById(@PathVariable(name = "idPublicacion") Long idPublicacion) {
        return new ResponseEntity<>(publicacionService.getPublicacionConImagenById(idPublicacion), HttpStatus.OK);
    }

    @Operation(summary = "Obtener publicaciones paginadas")
    @GetMapping("/page")
    public ResponseEntity<Page<PublicacionDto>> getPublicacionPage(
            @RequestParam(name = "pagina") Integer pagina, 
            @RequestParam(name = "cantidad") Integer cantidad) {
        return new ResponseEntity<>(publicacionService.listadoPublicacionPage(pagina, cantidad), HttpStatus.OK);
    }

    @Operation(summary = "Obtener publicaciones paginadas ordenadas por fecha DESC")
    @GetMapping("/page/date")
    public ResponseEntity<Page<PublicacionDto>> getPublicacionPageByFechaDESC(
            @RequestParam(name = "pagina") Integer pagina, 
            @RequestParam(name = "cantidad") Integer cantidad) {
        return new ResponseEntity<>(publicacionService.listadoPublicacionPageByFechaDESC(pagina, cantidad), HttpStatus.OK);
    }

    @Operation(summary = "Obtener comentarios paginados por publicación")
    @GetMapping("/comment/page")
    public ResponseEntity<Page<ComentarioDto>> getComentariosPageByPublicacion(
            @RequestParam(name = "pagina") Integer pagina, 
            @RequestParam(name = "cantidad") Integer cantidad, 
            @RequestParam(name = "idPublicacion") Long idPublicacion) {
        return new ResponseEntity<>(comentarioService.listadoComentariosPageByPublicacion(pagina, cantidad, idPublicacion), HttpStatus.OK);
    }

    @Operation(summary = "Crear nueva publicación")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<PublicacionSimpleDto> createPublicacion(@ModelAttribute PublicacionRequestDto publicacionDto) {
        return new ResponseEntity<>(publicacionService.createPublicacion(publicacionDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Eliminar publicación")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Void> deletePublicacion(@PathVariable(name = "id") Long idPublicacion) {
        publicacionService.deletePublicacion(idPublicacion);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Modificar publicación existente")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<PublicacionSimpleDto> modifyPublicacion(
            @PathVariable(name = "id") Long idPublicacion, @ModelAttribute PublicacionRequestDto publicacionDto) {
        return new ResponseEntity<>(publicacionService.modifyPublicacion(idPublicacion, publicacionDto), HttpStatus.OK);
    }

    @Operation(summary = "Crear comentario en publicación")
    @PostMapping("/comment")
    public ResponseEntity<String> createComentarioEnPublicacion(@RequestBody ComentarioDto comentarioDto) {
        String response = comentarioService.crearComentario(comentarioDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Eliminar comentario")
    @DeleteMapping("/comment/{id}")
    public ResponseEntity<Void> borrarComentario(@PathVariable(name = "id") Long idComentario) {
        comentarioService.borrarComentario(idComentario);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
