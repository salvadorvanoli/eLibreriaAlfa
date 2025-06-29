package ti.elibreriaalfa.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ti.elibreriaalfa.dtos.producto.ProductoConImagenesDto;
import ti.elibreriaalfa.dtos.producto.ProductoDto;
import ti.elibreriaalfa.dtos.producto.ProductoRequestDto;
import ti.elibreriaalfa.dtos.producto.ProductoSimpleDto;
import ti.elibreriaalfa.services.ProductoService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "product")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @Operation(summary = "Obtener todos los productos")
    @GetMapping
    public ResponseEntity<List<ProductoSimpleDto>> getAllProductos() {
        return new ResponseEntity<>(productoService.getAllProductos(), HttpStatus.OK);
    }

    @Operation(summary = "Obtener productos filtrados por categoria, texto de busqueda y orden")
    @GetMapping("/filtered")
    public ResponseEntity<List<ProductoSimpleDto>> getProductosFiltrados(
            @RequestParam(name = "categoria", required = false) Long idCategoria,
            @RequestParam(name = "textoBusqueda", required = false) String textoBusqueda,
            @RequestParam(name = "orden", required = false) String orden
    ) {
        try {
            List<ProductoSimpleDto> productosFitltrados = productoService.getProductosFiltrados(idCategoria, textoBusqueda, orden);
            return new ResponseEntity<>(productosFitltrados, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Obtener elementos de producto")
    @GetMapping("/elements")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Object> getElements() {
        return new ResponseEntity<>(productoService.getElements(), HttpStatus.OK);
    }

    @Operation(summary = "Obtener elementos de producto filtrados por texto de busqueda y orden")
    @GetMapping("/filteredElements")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Object> getElementsFiltrados(
            @RequestParam(name = "textoBusqueda", required = false) String textoBusqueda,
            @RequestParam(name = "orden", required = false) String orden
    ) {
        return new ResponseEntity<>(productoService.getElementsFiltrados(textoBusqueda, orden), HttpStatus.OK);
    }

    @Operation(summary = "Obtener un producto por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDto> getProductoPorId(@PathVariable(name = "id") Long idProducto) {
        return new ResponseEntity<>(productoService.obtenerProductoPorId(idProducto), HttpStatus.OK);
    }

    @Operation(summary = "Obtener un producto por su ID con imágenes")
    @GetMapping("/{id}/with-images")
    public ResponseEntity<ProductoConImagenesDto> getProductoConImagenesPorId(@PathVariable(name = "id") Long idProducto) {
        return new ResponseEntity<>(productoService.obtenerProductoConImagenesPorId(idProducto), HttpStatus.OK);
    }

    @Operation(summary = "Crear un nuevo producto")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<ProductoDto> createProducto(@ModelAttribute ProductoRequestDto producto) {
        return new ResponseEntity<>(productoService.createProducto(producto), HttpStatus.CREATED);
    }

    @Operation(summary = "Eliminar un producto por su ID")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Void> deleteProducto(@PathVariable(name = "id") Long idProducto) {
        productoService.deleteProducto(idProducto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Modificar un producto por su ID")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<ProductoDto> modifyProducto(
            @PathVariable(name = "id") Long idProducto,
            @ModelAttribute ProductoRequestDto productoDto
    ) {
        return new ResponseEntity<>(productoService.modifyProducto(idProducto, productoDto), HttpStatus.OK);
    }

    @Operation(summary = "Obtener una lista de productos paginados")
    @GetMapping("/paginado")
    public ResponseEntity<Page<ProductoSimpleDto>> productosPaginados(
            @RequestParam("pagina") Integer pagina,
            @RequestParam("cantidad")  Integer cantidad) {

        return new ResponseEntity<>(productoService.listadoProductoPage(pagina, cantidad), HttpStatus.OK);
    }

    @PatchMapping("/enable/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<?> enableProducto(@PathVariable(name = "id") Long idProducto) {
        try {
            return new ResponseEntity<>(productoService.enableProducto(idProducto), HttpStatus.OK);
        } catch (NumberFormatException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/disable/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<ProductoSimpleDto> disableProducto(@PathVariable(name = "id") Long idProducto) {
        return new ResponseEntity<>(productoService.disableProducto(idProducto), HttpStatus.OK);
    }

}