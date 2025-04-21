package ti.elibreriaalfa.dtos.categoriaDto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ti.elibreriaalfa.business.entities.Categoria;

@Data
@NoArgsConstructor
public class CategoriaSimpleDto {
    private Long id;
    private String nombre;

    public CategoriaSimpleDto(Categoria categoria) {
        this.id = categoria.getId();
        this.nombre = categoria.getNombre();
    }
}