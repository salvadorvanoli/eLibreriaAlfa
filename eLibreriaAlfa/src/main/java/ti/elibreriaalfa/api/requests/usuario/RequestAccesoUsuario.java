package ti.elibreriaalfa.api.requests.usuario;

import lombok.Data;
import ti.elibreriaalfa.business.entities.Usuario;
import ti.elibreriaalfa.dtos.usuario.AccesoUsuarioDto;

@Data
public class RequestAccesoUsuario {
    private AccesoUsuarioDto usuario;

    public Usuario mapToEntity() {
        return usuario.mapToEntity();
    }
}