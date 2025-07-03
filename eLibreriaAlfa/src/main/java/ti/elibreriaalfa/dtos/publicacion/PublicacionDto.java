package ti.elibreriaalfa.dtos.publicacion;

import lombok.Data;
import ti.elibreriaalfa.business.entities.Publicacion;
import ti.elibreriaalfa.dtos.comentario.ComentarioDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class PublicacionDto {
    private Long id;
    private String titulo;
    private LocalDateTime fechaCreacion;
    private String contenido;
    private String imagenUrl;
    private List<ComentarioDto> comentarios;
}
