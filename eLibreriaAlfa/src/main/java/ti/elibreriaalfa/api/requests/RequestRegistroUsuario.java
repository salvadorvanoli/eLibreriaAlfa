package ti.elibreriaalfa.api.requests;

import lombok.Data;
import ti.elibreriaalfa.business.entities.Usuario;
import ti.elibreriaalfa.dtos.UsuarioDto;

@Data
public class RequestRegistroUsuario {

    private UsuarioDto usuario;


}