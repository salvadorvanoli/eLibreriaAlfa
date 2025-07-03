package ti.elibreriaalfa.api.responses.publicacion;

import lombok.Data;
import ti.elibreriaalfa.dtos.publicacion.PublicacionDto;

import java.util.List;

@Data
public class ResponseListadoPublicaciones {
    private List<PublicacionDto> publicaciones;
}
