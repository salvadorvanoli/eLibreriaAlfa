package ti.elibreriaalfa.api.controllers;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ti.elibreriaalfa.api.responses.impresion.ResponseImpresion;
import ti.elibreriaalfa.api.responses.impresion.ResponseListadoImpresiones;
import ti.elibreriaalfa.dtos.impresion.ImpresionDto;
import ti.elibreriaalfa.services.ImpresionService;

@RestController
@RequestMapping(value = "print")
public class ImpresionController {
    private final ImpresionService impresionService;

    public ImpresionController(ImpresionService impresionService) {
        this.impresionService = impresionService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<ResponseListadoImpresiones> getAllImpresiones() {
        return new ResponseEntity<>(impresionService.getAllImpresiones(), HttpStatus.OK);
    }

    @GetMapping("/{idImpresion}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Object> getImpresionById(@PathVariable (name = "idImpresion") Long idImpresion) {
        try {
            ResponseImpresion response = impresionService.getImpresionById(idImpresion);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Page<ImpresionDto>> getImpresionPage(@RequestParam(name = "pagina") Integer pagina, @RequestParam(name = "cantidad") Integer cantidad) {
        return new ResponseEntity<>(impresionService.listadoImpresionPage(pagina, cantidad), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> createImpresion(@RequestBody ImpresionDto impresionDto) {
        String response = impresionService.crearImpresion(impresionDto);

        if(response == null) {
            return new ResponseEntity<>("Error al crear la impresión", HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Void> borrarImpresion(@PathVariable(name = "id") Long idImpresion) {
        impresionService.borrarImpresion(idImpresion);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/user/{usuarioId}")
    public ResponseEntity<?> listarImpresionesPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(impresionService.listarImpresionesPorUsuario(usuarioId));
    }

}
