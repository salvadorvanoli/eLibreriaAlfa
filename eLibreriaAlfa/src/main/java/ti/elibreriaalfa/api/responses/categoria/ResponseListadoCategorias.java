package ti.elibreriaalfa.api.responses.categoria;

import lombok.Data;
import ti.elibreriaalfa.dtos.categoria.CategoriaDto;

import java.util.List;

@Data
public class ResponseListadoCategorias {

    private List<CategoriaDto> categorias;

}
