package ti.elibreriaalfa.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PublicacionDto {

    private Long id;

    private String titulo;

    private LocalDateTime fechaCreacion;

    private String contenido;

    private List<ComentarioDto> comentarios;

}
