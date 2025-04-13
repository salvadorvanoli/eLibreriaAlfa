package ti.elibreriaalfa.dtos;

import lombok.Data;

@Data
public class ImpresionDto {

    private Long id;

    private Boolean color;

    private Boolean simple;

    private Boolean vertical;

    private String comentarioAdicional;

    private UsuarioDto usuario;

}
