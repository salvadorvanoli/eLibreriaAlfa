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

@RestController
@RequestMapping(value = "product")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public ResponseEntity<List<ProductoSimpleDto>> getAllProductos() {
        return new ResponseEntity<>(productoService.getAllProductos(), HttpStatus.OK);
    }

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

    @GetMapping("/elements")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Object> getElements() {
        return new ResponseEntity<>(productoService.getElements(), HttpStatus.OK);
    }

    @GetMapping("/filteredElements")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Object> getElementsFiltrados(
            @RequestParam(name = "textoBusqueda", required = false) String textoBusqueda,
            @RequestParam(name = "orden", required = false) String orden
    ) {
        return new ResponseEntity<>(productoService.getElementsFiltrados(textoBusqueda, orden), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDto> getProductoPorId(@PathVariable(name = "id") Long idProducto) {
        return new ResponseEntity<>(productoService.obtenerProductoPorId(idProducto), HttpStatus.OK);
    }

    @GetMapping("/{id}/with-images")
    public ResponseEntity<ProductoConImagenesDto> getProductoConImagenesPorId(@PathVariable(name = "id") Long idProducto) {
        return new ResponseEntity<>(productoService.obtenerProductoConImagenesPorId(idProducto), HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(description = "Esta función crea una nueva categoria")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<ProductoDto> createProducto(@ModelAttribute ProductoRequestDto producto) {
        return new ResponseEntity<>(productoService.createProducto(producto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    //@PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Void> deleteProducto(@PathVariable(name = "id") Long idProducto) {
        productoService.deleteProducto(idProducto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<ProductoDto> modifyProducto(
            @PathVariable(name = "id") Long idProducto,
            @ModelAttribute ProductoRequestDto productoDto
    ) {
        return new ResponseEntity<>(productoService.modifyProducto(idProducto, productoDto), HttpStatus.OK);
    }

    //http://localhost:8080/product/paginado?pagina=0&cantidad=10
    @GetMapping("/paginado")
    public ResponseEntity<Page<ProductoSimpleDto>> productosPaginados(
            @RequestParam("pagina") Integer pagina,
            @RequestParam("cantidad")  Integer cantidad) {

        return new ResponseEntity<>(productoService.listadoProductoPage(pagina, cantidad), HttpStatus.OK);
    }

 /* Ejemplos de Json para probar:

    CREAR

    1)
    {
    "nombre": "Licuadora",
    "precio": 2590.99,
    "descripcion": "Licuadora de 3 velocidades con vaso de vidrio.",
    "categorias": []
    }

    2)
    {
    "nombre": "Horno Eléctrico",
    "precio": 8990.50,
    "descripcion": "Horno eléctrico 45L con temporizador y control de temperatura.",
    "categorias": [
        { "id": 1 },
        { "id": 2 }
        ]
    }


    MODIFICAR

    {
  "id": 54,
  "nombre": "Laptop",
  "categorias": [
        {
          "id": 2
        }
    ],
  "precio": 899660.5,
  "descripcion": "A."
    }

     */
}