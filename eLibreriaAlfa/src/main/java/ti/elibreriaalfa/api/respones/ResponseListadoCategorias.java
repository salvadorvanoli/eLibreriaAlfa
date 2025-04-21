package ti.elibreriaalfa.api.respones;

import lombok.Data;
import ti.elibreriaalfa.dtos.categoriaDto.CategoriaDto;

import java.util.List;

@Data
public class ResponseListadoCategorias {

    private List<CategoriaDto> categorias;

}
