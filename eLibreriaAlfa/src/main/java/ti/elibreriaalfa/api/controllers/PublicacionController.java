package ti.elibreriaalfa.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class PublicacionController {
    private final PublicacionService publicacionService;
    private final ComentarioService comentarioService;
    
    public PublicacionController(PublicacionService publicacionService, ComentarioService comentarioService) {
        this.publicacionService = publicacionService;
        this.comentarioService = comentarioService;
    }

    @Operation(summary = "Obtener todas las publicaciones")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de publicaciones"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<ResponseListadoPublicaciones> getAllPublicaciones() {
        return new ResponseEntity<>(publicacionService.getAllPublicaciones(), HttpStatus.OK);
    }

    @Operation(summary = "Obtener elementos de publicaciones (Admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Elementos obtenidos exitosamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ADMINISTRADOR")
    })
    @GetMapping("/elements")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Object> getElements() {
        log.info("Solicitando elementos de publicaciones");
        return new ResponseEntity<>(publicacionService.getElements(), HttpStatus.OK);
    }

    @Operation(summary = "Obtener elementos filtrados de publicaciones (Admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Elementos filtrados obtenidos exitosamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ADMINISTRADOR")
    })
    @GetMapping("/filteredElements")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Object> getElementsFiltrados(
            @RequestParam(name = "textoBusqueda", required = false) String textoBusqueda,
            @RequestParam(name = "orden", required = false) String orden
    ) {
        log.info("Solicitando elementos filtrados - Texto: {}, Orden: {}", textoBusqueda, orden);
        return new ResponseEntity<>(publicacionService.getElementsFiltrados(textoBusqueda, orden), HttpStatus.OK);
    }

    @Operation(summary = "Obtener publicación por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Publicación encontrada"),
            @ApiResponse(responseCode = "400", description = "ID de publicación inválido"),
            @ApiResponse(responseCode = "404", description = "Publicación no encontrada")
    })
    @GetMapping("/{idPublicacion}")
    public ResponseEntity<ResponsePublicacion> getPublicacionById(@PathVariable(name = "idPublicacion") Long idPublicacion) {
        log.info("Solicitando publicación con ID: {}", idPublicacion);
        ResponsePublicacion response = publicacionService.getPublicacionById(idPublicacion);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Obtener publicaciones paginadas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de publicaciones obtenida exitosamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros de paginación inválidos")
    })
    @GetMapping("/page")
    public ResponseEntity<Page<PublicacionDto>> getPublicacionPage(
            @RequestParam(name = "pagina") Integer pagina, 
            @RequestParam(name = "cantidad") Integer cantidad) {
        log.info("Solicitando página {} con {} elementos", pagina, cantidad);
        return new ResponseEntity<>(publicacionService.listadoPublicacionPage(pagina, cantidad), HttpStatus.OK);
    }

    @Operation(summary = "Obtener publicaciones paginadas ordenadas por fecha DESC")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de publicaciones ordenada por fecha obtenida exitosamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros de paginación inválidos")
    })
    @GetMapping("/page/date")
    public ResponseEntity<Page<PublicacionDto>> getPublicacionPageByFechaDESC(
            @RequestParam(name = "pagina") Integer pagina, 
            @RequestParam(name = "cantidad") Integer cantidad) {
        log.info("Solicitando página {} con {} elementos ordenados por fecha DESC", pagina, cantidad);
        return new ResponseEntity<>(publicacionService.listadoPublicacionPageByFechaDESC(pagina, cantidad), HttpStatus.OK);
    }

    @Operation(summary = "Obtener comentarios paginados por publicación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de comentarios obtenida exitosamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos"),
            @ApiResponse(responseCode = "404", description = "Publicación no encontrada")
    })
    @GetMapping("/comment/page")
    public ResponseEntity<Page<ComentarioDto>> getComentariosPageByPublicacion(
            @RequestParam(name = "pagina") Integer pagina, 
            @RequestParam(name = "cantidad") Integer cantidad, 
            @RequestParam(name = "idPublicacion") Long idPublicacion) {
        log.info("Solicitando comentarios de publicación {} - Página: {}, Cantidad: {}", idPublicacion, pagina, cantidad);
        return new ResponseEntity<>(comentarioService.listadoComentariosPageByPublicacion(pagina, cantidad, idPublicacion), HttpStatus.OK);
    }

    @Operation(summary = "Crear nueva publicación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Publicación creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de publicación inválidos"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PostMapping
    //@PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<String> createPublicacion(@RequestBody PublicacionDto publicacionDto) {
        log.info("Creando nueva publicación con título: {}", publicacionDto.getTitulo());
        String response = publicacionService.crearPublicacion(publicacionDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Eliminar publicación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Publicación eliminada exitosamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ADMINISTRADOR"),
            @ApiResponse(responseCode = "404", description = "Publicación no encontrada")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Void> borrarPublicacion(@PathVariable(name = "id") Long idPublicacion) {
        log.info("Eliminando publicación con ID: {}", idPublicacion);
        publicacionService.borrarPublicacion(idPublicacion);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Crear comentario en publicación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Comentario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de comentario inválidos"),
            @ApiResponse(responseCode = "404", description = "Publicación no encontrada")
    })
    @PostMapping("/comment")
    public ResponseEntity<String> createComentarioEnPublicacion(@RequestBody ComentarioDto comentarioDto) {
        log.info("Creando comentario en publicación ID: {}", 
                comentarioDto.getPublicacion() != null ? comentarioDto.getPublicacion().getId() : "N/A");
        String response = comentarioService.crearComentario(comentarioDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Eliminar comentario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comentario eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Comentario no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })

    @DeleteMapping("/comment/{id}")
    public ResponseEntity<Void> borrarComentario(@PathVariable(name = "id") Long idComentario) {
        log.info("Eliminando comentario con ID: {}", idComentario);
        comentarioService.borrarComentario(idComentario);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
