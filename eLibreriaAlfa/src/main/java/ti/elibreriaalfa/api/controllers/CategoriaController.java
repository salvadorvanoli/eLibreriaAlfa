package ti.elibreriaalfa.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ti.elibreriaalfa.api.responses.categoria.ResponseListadoCategorias;
import ti.elibreriaalfa.dtos.categoria.CategoriaCreateDto;
import ti.elibreriaalfa.dtos.categoria.CategoriaDto;
import ti.elibreriaalfa.services.CategoriaService;
import org.springframework.data.domain.Page;

@RestController
@RequestMapping(value = "category")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    // @Secured({"ADMIN"})
    public ResponseEntity<ResponseListadoCategorias> getCategorias() {


        ResponseListadoCategorias response = categoriaService.listadoCategorias();
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping
    @Operation(description = "Esta función crea una nueva categoria")
    public ResponseEntity<String> createCategoria(@RequestBody CategoriaCreateDto categoria) {
        String response = categoriaService.crearCategoria(categoria);
        if (response == null) {
            return new ResponseEntity<>("Error al crear socio", HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrarCategoria(@PathVariable(name = "id") Long idCategoria) {
        categoriaService.borrarCategoria(idCategoria);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> modificarCategoria(
            @PathVariable(name = "id") Long idCategoria,
            @RequestBody CategoriaDto categoriaDto) {

        try {
            String response = categoriaService.modificarCategoria(idCategoria, categoriaDto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno al modificar categoría", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //http://localhost:8080/category/paginado?pagina=0&cantidad=10
    @GetMapping("/paginado")
    public ResponseEntity<Page<CategoriaDto>> categoriasPaginadas(
            @RequestParam("pagina")  Integer pagina,
            @RequestParam("cantidad")  Integer cantidad) {

        return new ResponseEntity<>(categoriaService.listadoCategoriaPage(pagina, cantidad), HttpStatus.OK);
    }

    /* Ejemplos de Json para probar:

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