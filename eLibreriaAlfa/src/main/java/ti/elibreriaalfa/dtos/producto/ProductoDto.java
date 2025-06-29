package ti.elibreriaalfa.dtos.producto;

import lombok.Data;
import ti.elibreriaalfa.dtos.categoria.CategoriaSimpleDto;

import java.util.List;

@Data
public class ProductoDto {
    private Long id;
    private boolean habilitado;
    private String nombre;
    private Float precio;
    private String descripcion;
    private String[] imagenes;
    private List<CategoriaSimpleDto> categorias;
}
