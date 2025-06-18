package ti.elibreriaalfa.dtos.producto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ti.elibreriaalfa.business.entities.Producto;

import java.util.List;

@Data
public class ProductoSimpleDto {
    private Long id;
    private String nombre;
    private Float precio;
    private String descripcion;
    private String[] imagenes;
}
