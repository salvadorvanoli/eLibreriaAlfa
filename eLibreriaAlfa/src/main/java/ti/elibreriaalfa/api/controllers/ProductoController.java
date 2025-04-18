package ti.elibreriaalfa.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ti.elibreriaalfa.api.respones.ResponseListadoProductos;
import ti.elibreriaalfa.dtos.productoDto.ProductoDto;
import ti.elibreriaalfa.services.ProductoService;

@RestController
@RequestMapping(value = "product")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    // @Secured({"ADMIN"})
    public ResponseEntity<ResponseListadoProductos> getCProductos() {


        ResponseListadoProductos response = productoService.listadoProductos();
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping
    @Operation(description = "Esta función crea una nueva categoria")
    public ResponseEntity<String> createCategoria(@RequestBody ProductoDto producto) {
        String response = productoService.crearProducto(producto);
        if (response == null) {
            return new ResponseEntity<>("Error al crear socio", HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrarCategoria(@PathVariable(name = "id") Long idProducto) {
        productoService.borrarProducto(idProducto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> modificarProducto(
            @PathVariable(name = "id") Long idProducto,
            @RequestBody ProductoDto productoDto) {

        try {
            String response = productoService.modificarProducto(idProducto, productoDto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno al modificar producto", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}