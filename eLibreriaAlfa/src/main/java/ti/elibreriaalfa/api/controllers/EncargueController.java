package ti.elibreriaalfa.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ti.elibreriaalfa.api.responses.encargue.ResponseListadoEncargues;
import ti.elibreriaalfa.business.entities.Encargue_Estado;
import ti.elibreriaalfa.dtos.encargue.EncargueDto;
import ti.elibreriaalfa.dtos.producto_encargue.Producto_EncargueDto;
import ti.elibreriaalfa.services.EncargueService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "order")
public class EncargueController {

    private final EncargueService encargueService;

    public EncargueController(EncargueService encargueService) {
        this.encargueService = encargueService;
    }

    @GetMapping
    public ResponseEntity<ResponseListadoEncargues> getEncargues() {
        ResponseListadoEncargues response = encargueService.listadoEncargues();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    @Operation(description = "Esta función crea un nuevo encargue")
    public ResponseEntity<String> createEncargue(@RequestBody EncargueDto encargue) {
        String response = encargueService.crearEncargue(encargue);
        if (response == null) {
            return new ResponseEntity<>("Error al crear socio", HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrarEncargue(@PathVariable(name = "id") Long idEncargue) {
        try {
            encargueService.borrarEncargue(idEncargue);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> modificarEncargue(
            @PathVariable(name = "id") Long idEncargue,
            @Valid @RequestBody EncargueDto encargueDto) {

        try {
            String response = encargueService.modificarEncargue(idEncargue, encargueDto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno al modificar el encargue", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/paginado")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Page<EncargueDto>> encarguesPaginados(
            @RequestParam("pagina")  Integer pagina,
            @RequestParam("cantidad") Integer cantidad) {

        return new ResponseEntity<>(encargueService.listadoEncarguePage(pagina, cantidad), HttpStatus.OK);
    }

    // Agregar producto a un encargue
    @PostMapping("/usuario/{usuarioId}/producto")
    public ResponseEntity<Void> agregarProductoAEncarguePorUsuario(
            @PathVariable("usuarioId") Long usuarioId,
            @RequestBody Producto_EncargueDto productoDto) {
        encargueService.agregarProductoAEncarguePorUsuario(usuarioId, productoDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // Eliminar producto de un encargue
    @DeleteMapping("/{id}/producto/{productoEncargueId}")
    public ResponseEntity<Void> eliminarProductoDeEncargue(
            @PathVariable("id") Long encargueId,
            @PathVariable("productoEncargueId") Long productoEncargueId) {
        encargueService.eliminarProductoDeEncargue(encargueId, productoEncargueId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Obtener encargue por usuario y estado
    @GetMapping("/usuario/{usuarioId}/estado/{estado}")
    public ResponseEntity<EncargueDto> obtenerEncarguePorUsuarioYEstado(
            @PathVariable("usuarioId") Long usuarioId,
            @PathVariable("estado") Encargue_Estado estado) {
        EncargueDto dto = encargueService.obtenerEncarguePorUsuarioYEstado(usuarioId, estado);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/usuario/{usuarioId}/estado/{estado}/productos")
    public ResponseEntity<Page<Producto_EncargueDto>> listarProductosEncarguePorUsuarioYEstado(
            @PathVariable("usuarioId") Long usuarioId,
            @PathVariable("estado") Encargue_Estado estado,
            @RequestParam("pagina") Integer pagina,
            @RequestParam("cantidad") Integer cantidad) {

        Page<Producto_EncargueDto> page = encargueService.listarProductosEncarguePorUsuarioYEstado(usuarioId, estado, pagina, cantidad);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @GetMapping("/usuario/{usuarioId}/tiene-en-creacion")
    public ResponseEntity<Boolean> usuarioTieneEncargueEnCreacion(@PathVariable("usuarioId") Long usuarioId) {
        boolean tiene = encargueService.usuarioTieneEncargueEnCreacion(usuarioId);
        return new ResponseEntity<>(tiene, HttpStatus.OK);
    }

    @PostMapping("/{id}/enviar")
    public ResponseEntity<Void> marcarComoEnviado(
            @PathVariable("id") Long idEncargue,
            @RequestBody Map<String, String> body) {
        String fechaStr = body.get("fecha");
        if (fechaStr == null) {
            return ResponseEntity.badRequest().build();
        }
        LocalDate fecha = LocalDate.parse(fechaStr);
        encargueService.marcarEncargueComoEnviado(idEncargue, fecha);
        return ResponseEntity.ok().build();
    }

    // src/main/java/ti/elibreriaalfa/api/controllers/EncargueController.java

    @PostMapping("/usuario/{usuarioId}/cancelar-enviado")
    public ResponseEntity<Void> cancelarEnviadoYCrearNuevo(@PathVariable("usuarioId") Long usuarioId) {
        encargueService.cancelarEncargueEnviadoYCrearNuevo(usuarioId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}/history")
    public ResponseEntity<?> listarEncarguesFinalizadosPorUsuario(@PathVariable("userId") Long usuarioId) {
        var encargues = encargueService.listarEncarguesPorUsuarioEstados(usuarioId,
                List.of(Encargue_Estado.CANCELADO, Encargue_Estado.ENTREGADO, Encargue_Estado.COMPLETADO));
        return ResponseEntity.ok(encargues);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<String> cambiarEstadoEncargue(
            @PathVariable("id") Long idEncargue,
            @RequestBody Map<String, String> body) {
        String nuevoEstado = body.get("estado");
        if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("El estado es requerido");
        }
        try {
            encargueService.cambiarEstadoEncargue(idEncargue, nuevoEstado);
            return ResponseEntity.ok("Estado actualizado correctamente");
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

/* Ejemplos de Json para probar:

    //http://localhost:8080/order/paginado?pagina=0&cantidad=10

    CREAR

    1)
    {
        "idUsuarioComprador": 10,
            "productos": [
            {
            "producto": { "id": 1 },
            "cantidad": 2
            },
            {
            "producto": { "id": 3 },
            "cantidad": 1
            },
            {
            "producto": { "id": 54 },
            "cantidad": 3
            }
        ]
    }

    MODIFICAR


*/
}
