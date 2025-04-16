package ti.elibreriaalfa.dtos;

import lombok.Data;
import ti.elibreriaalfa.dtos.usuario.UsuarioDto;

@Data
public class ImpresionDto {

    private Long id;

    private Boolean color;

    private Boolean simple;

    private Boolean vertical;

    private String comentarioAdicional;

    private UsuarioDto usuario;

}
