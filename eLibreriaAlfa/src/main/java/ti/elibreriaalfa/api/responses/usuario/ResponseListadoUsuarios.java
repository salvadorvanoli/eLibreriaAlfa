package ti.elibreriaalfa.api.responses.usuario;

import lombok.Data;
import ti.elibreriaalfa.dtos.usuario.UsuarioDto;

import java.util.List;

@Data
public class ResponseListadoUsuarios {
    private List<UsuarioDto> usuarios;
}
