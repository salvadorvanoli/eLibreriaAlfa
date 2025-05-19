package ti.elibreriaalfa.dtos.producto_encargue;

import lombok.Data;
import lombok.NoArgsConstructor;
import ti.elibreriaalfa.business.entities.Producto_Encargue;
import ti.elibreriaalfa.dtos.producto.ProductoSimpleDto;



@Data
@NoArgsConstructor
public class Producto_EncargueDto {
    private Long id;

    private Integer cantidad;

    private ProductoSimpleDto producto;

    private Long EncargueId;

    public Producto_EncargueDto(Producto_Encargue pe) {
        this.id = pe.getId();
        this.cantidad = pe.getCantidad();
        this.producto = new ProductoSimpleDto(pe.getProducto());
        this.EncargueId = pe.getEncargue().getId();
    }
}
