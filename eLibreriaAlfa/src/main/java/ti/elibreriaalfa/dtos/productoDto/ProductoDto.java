package ti.elibreriaalfa.dtos.productoDto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ti.elibreriaalfa.business.entities.Producto;
import ti.elibreriaalfa.dtos.categoriaDto.CategoriaSimpleDto;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class ProductoDto {
    private Long id;
    private String nombre;
    private List<CategoriaSimpleDto> categorias; // Usamos el DTO simple
    private Float precio;
    private String descripcion;

    public ProductoDto(Producto producto) {
        this.id = producto.getId();
        this.nombre = producto.getNombre();
        this.precio = producto.getPrecio();
        this.descripcion = producto.getDescripcion();
        this.categorias = (producto.getCategorias() != null)
                ? producto.getCategorias().stream()
                .map(CategoriaSimpleDto::new)
                .collect(Collectors.toList())
                : Collections.emptyList();
    }

}
