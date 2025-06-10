package ti.elibreriaalfa.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ti.elibreriaalfa.dtos.producto.ProductoDto;
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
        try {
            ProductoDto producto = productoService.obtenerProductoPorId(idProducto);
            return new ResponseEntity<>(producto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    @Operation(description = "Esta función crea una nueva categoria")
    //@PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<String> createProducto(@RequestBody ProductoDto producto) {
        String response = productoService.crearProducto(producto);
        if (response == null) {
            return new ResponseEntity<>("Error al crear socio", HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
    }

    @DeleteMapping("/{id}")
    //@PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Void> borrarCategoria(@PathVariable(name = "id") Long idProducto) {
        productoService.borrarProducto(idProducto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
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

    //http://localhost:8080/product/paginado?pagina=0&cantidad=10
    @GetMapping("/paginado")
    public ResponseEntity<Page<ProductoDto>> productosPaginados(
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