package ti.elibreriaalfa.api.responses.usuario;

import lombok.Data;
import ti.elibreriaalfa.dtos.usuario.UsuarioSimpleDto;

import java.util.List;

@Data
public class ResponseListadoUsuarios {
    private List<UsuarioSimpleDto> usuarios;
}
