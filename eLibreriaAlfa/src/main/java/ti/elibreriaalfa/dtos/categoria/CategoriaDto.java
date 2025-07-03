package ti.elibreriaalfa.dtos.categoria;

import lombok.Data;
import ti.elibreriaalfa.business.entities.Categoria;
import ti.elibreriaalfa.dtos.producto.ProductoSimpleDto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.NoArgsConstructor;

@Data
public class CategoriaDto {
    private Long id;
    private String nombre;
    private CategoriaSimpleDto padre;
    private List<ProductoSimpleDto> productos;
}
