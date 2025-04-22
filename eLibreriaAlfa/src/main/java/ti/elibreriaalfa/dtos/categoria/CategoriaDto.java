package ti.elibreriaalfa.dtos.categoria;

import lombok.Data;
import ti.elibreriaalfa.business.entities.Categoria;
import ti.elibreriaalfa.dtos.productoDto.ProductoSimpleDto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // Constructor sin argumentos (OBLIGATORIO para Jackson)
public class CategoriaDto {
    private Long id;
    private String nombre;
    private CategoriaSimpleDto padre;
    private List<ProductoSimpleDto> productos;

    public CategoriaDto(Categoria categoria) {
        this.id = categoria.getId();
        this.nombre = categoria.getNombre();
        this.padre = categoria.getPadre() != null ? new CategoriaSimpleDto(categoria.getPadre()) : null;
        this.productos = categoria.getProductos() != null ?
                categoria.getProductos().stream()
                        .map(ProductoSimpleDto::new)
                        .collect(Collectors.toList()) :
                Collections.emptyList();
    }
}
