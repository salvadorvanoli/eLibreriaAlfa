package ti.elibreriaalfa.dtos.producto;

import lombok.Data;
import ti.elibreriaalfa.dtos.categoria.CategoriaSimpleDto;
import ti.elibreriaalfa.dtos.image.ImageDto;

import java.util.List;

@Data
public class ProductoConImagenesDto {
    private Long id;
    private boolean habilitado;
    private String nombre;
    private Float precio;
    private String descripcion;
    private List<ImageDto> imagenes;
    private List<CategoriaSimpleDto> categorias;
}
