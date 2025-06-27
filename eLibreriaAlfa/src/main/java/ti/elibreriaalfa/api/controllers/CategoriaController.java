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

    @Operation(summary = "Obtener todas las categorías en formato árbol")
    @GetMapping
    public ResponseEntity<List<CategoriaNodoDto>> getAllCategoriasTree() {
        return new ResponseEntity<>(categoriaService.getAllCategoriasTree(), HttpStatus.OK);
    }

    @Operation(summary = "Obtener categoría por ID")
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaSimpleDto> getCategoriaPorId(@PathVariable(name = "id") Long idCategoria) {
        return new ResponseEntity<>(categoriaService.obtenerCategoriaPorId(idCategoria), HttpStatus.OK);
    }

    @Operation(summary = "Crear una nueva categoría")
    @PostMapping
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<CategoriaSimpleDto> createCategoria(@RequestBody CategoriaRequestDto categoria) {
        return new ResponseEntity<>(categoriaService.createCategoria(categoria), HttpStatus.CREATED);
    }

    @Operation(summary = "Borrar una categoría por ID")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Void> deleteCategoria(@PathVariable(name = "id") Long idCategoria) {
        categoriaService.deleteCategoria(idCategoria);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Modificar una categoría por ID")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<CategoriaSimpleDto> modifyCategoria(
            @PathVariable(name = "id") Long idCategoria,
            @RequestBody CategoriaRequestDto categoriaDto
    ) {
        return new ResponseEntity<>(categoriaService.modifyCategoria(idCategoria, categoriaDto), HttpStatus.OK);
    }

    @Operation(summary = "Obtener todas las categorías paginadas")
    @GetMapping("/paginado")
    public ResponseEntity<Page<CategoriaDto>> categoriasPaginadas(
            @RequestParam("pagina")  Integer pagina,
            @RequestParam("cantidad")  Integer cantidad
    ) {
        return new ResponseEntity<>(categoriaService.listadoCategoriaPage(pagina, cantidad), HttpStatus.OK);
    }
}