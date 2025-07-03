package ti.elibreriaalfa.dtos.categoria;

import lombok.Data;
import lombok.NoArgsConstructor;
import ti.elibreriaalfa.business.entities.Categoria;

@Data
@NoArgsConstructor
public class CategoriaSimpleDto {
    private Long id;
    private String nombre;
    private Long padreId;
}