package ti.elibreriaalfa.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ti.elibreriaalfa.api.responses.encargue.ResponseListadoEncargues;
import ti.elibreriaalfa.dtos.encargue.EncargueDto;
import ti.elibreriaalfa.services.EncargueService;

@RestController
@RequestMapping(value = "order")
public class EncargueController {

    private final EncargueService encargueService;

    public EncargueController(EncargueService encargueService) {
        this.encargueService = encargueService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
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
