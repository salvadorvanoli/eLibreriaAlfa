package ti.elibreriaalfa.dtos.categoria;

import lombok.Data;
import ti.elibreriaalfa.business.entities.Categoria;
import ti.elibreriaalfa.exceptions.categoria.CategoriaException;
import ti.elibreriaalfa.utils.Constants;

import java.util.ArrayList;

@Data
public class CategoriaRequestDto {
    private String nombre;
    private Long padreId;

    public Categoria mapToEntity() {
        Categoria categoria = new Categoria();

        categoria.setNombre(this.nombre);
        categoria.setProductos(new ArrayList<>());
        categoria.setHijos(new ArrayList<>());

        return categoria;
    }

    public void validateCategoriaRequestDto() {
        if (this.nombre == null || this.nombre.isBlank() || this.nombre.length() > Constants.MAX_NOMBRE_CATEGORIA_LENGTH) {
            throw new CategoriaException(Constants.ERROR_NOMBRE_CATEGORIA_INVALIDO);
        }
    }
}
