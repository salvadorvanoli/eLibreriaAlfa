package ti.elibreriaalfa.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ti.elibreriaalfa.dtos.categoria.CategoriaRequestDto;
import ti.elibreriaalfa.dtos.categoria.CategoriaDto;
import ti.elibreriaalfa.dtos.categoria.CategoriaNodoDto;
import ti.elibreriaalfa.dtos.categoria.CategoriaSimpleDto;
import ti.elibreriaalfa.services.CategoriaService;
import org.springframework.data.domain.Page;

import java.util.List;

@RestController
@RequestMapping(value = "category")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public ResponseEntity<List<CategoriaNodoDto>> getAllCategoriasTree() {
        return new ResponseEntity<>(categoriaService.getAllCategoriasTree(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDto> getCategoriaPorId(@PathVariable(name = "id") Long idCategoria) {
        return new ResponseEntity<>(categoriaService.obtenerCategoriaPorId(idCategoria), HttpStatus.OK);
    }

    @PostMapping
    @Operation(description = "Esta función crea una nueva categoria")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<CategoriaSimpleDto> createCategoria(@RequestBody CategoriaRequestDto categoria) {
        return new ResponseEntity<>(categoriaService.crearCategoria(categoria), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Void> borrarCategoria(@PathVariable(name = "id") Long idCategoria) {
        categoriaService.borrarCategoria(idCategoria);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<CategoriaSimpleDto> modificarCategoria(
            @PathVariable(name = "id") Long idCategoria,
            @RequestBody CategoriaRequestDto categoriaDto
    ) {
        return new ResponseEntity<>(categoriaService.modificarCategoria(idCategoria, categoriaDto), HttpStatus.OK);
    }

    @GetMapping("/paginado")
    public ResponseEntity<Page<CategoriaDto>> categoriasPaginadas(
            @RequestParam("pagina")  Integer pagina,
            @RequestParam("cantidad")  Integer cantidad
    ) {
        return new ResponseEntity<>(categoriaService.listadoCategoriaPage(pagina, cantidad), HttpStatus.OK);
    }


    /* Ejemplos de Json para probar:

    http://localhost:8080/category/paginado?pagina=0&cantidad=10

    CREAR

    1)
    {
    "nombre": "Electrodomésticos"
    }

    2)
    {
    "nombre": "Heladeras",
    "padreId": 1
    }

    MODIFICAR

   {
  "id": 2,
  "nombre": "Heladeras",
  "padre": {
        "id": 1
        },
  "productos": [
        { "id": 53 },
        { "id": 54 }
         ]
    }
     */

}