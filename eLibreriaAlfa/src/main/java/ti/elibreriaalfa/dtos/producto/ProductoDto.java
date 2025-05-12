package ti.elibreriaalfa.dtos.producto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ti.elibreriaalfa.business.entities.Producto;
import ti.elibreriaalfa.dtos.categoria.CategoriaSimpleDto;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class ProductoDto {
    private Long id;
    private String nombre;
    private List<CategoriaSimpleDto> categorias;
    private Float precio;
    private String descripcion;
    private  String[] imagenes;

    public ProductoDto(Producto producto) {
        this.id = producto.getId();
        this.nombre = producto.getNombre();
        this.precio = producto.getPrecio();
        this.imagenes = producto.getImagenes();
        this.descripcion = producto.getDescripcion();
        this.categorias = (producto.getCategorias() != null)
                ? producto.getCategorias().stream()
                .map(CategoriaSimpleDto::new)
                .collect(Collectors.toList())
                : Collections.emptyList();
    }

}
