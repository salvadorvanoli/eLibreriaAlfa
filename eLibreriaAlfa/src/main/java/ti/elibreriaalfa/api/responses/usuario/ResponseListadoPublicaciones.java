package ti.elibreriaalfa.api.responses.usuario;

import lombok.Data;
import ti.elibreriaalfa.business.entities.Publicacion;
import ti.elibreriaalfa.dtos.Publicacion.PublicacionDto;

import java.util.List;

@Data
public class ResponseListadoPublicaciones {
    private List<PublicacionDto> publicaciones;
}
