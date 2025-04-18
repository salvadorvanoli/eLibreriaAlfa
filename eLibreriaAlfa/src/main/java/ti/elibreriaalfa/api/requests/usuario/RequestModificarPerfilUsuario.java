package ti.elibreriaalfa.api.requests.usuario;

import lombok.Data;
import ti.elibreriaalfa.dtos.usuario.ModificarPerfilUsuarioDto;

@Data
public class RequestModificarPerfilUsuario {
    private ModificarPerfilUsuarioDto perfilUsuario;
}
