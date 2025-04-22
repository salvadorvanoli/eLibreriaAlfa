package ti.elibreriaalfa.dtos.producto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ti.elibreriaalfa.business.entities.Producto;

@Data
@NoArgsConstructor
public class ProductoSimpleDto {
    private Long id;
    private String nombre;
    private Float precio;

    public ProductoSimpleDto(Producto producto) {
        this.id = producto.getId();
        this.nombre = producto.getNombre();
        this.precio = producto.getPrecio();
    }

}
