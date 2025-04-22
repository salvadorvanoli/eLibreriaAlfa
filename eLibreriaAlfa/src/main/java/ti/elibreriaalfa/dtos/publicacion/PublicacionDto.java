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

    private List<ComentarioDto> comentarios;

    public Publicacion mapToEntity() {
        Publicacion publicacion = new Publicacion();

        publicacion.setId(this.getId());
        publicacion.setContenido(this.getContenido());
        publicacion.setTitulo(this.getTitulo());
        publicacion.setFechaCreacion(this.getFechaCreacion());
        publicacion.setComentarios(
                this.getComentarios()
                        .stream()
                        .map(ComentarioDto::mapToEntity)
                        .collect(Collectors.toList())
        );

        return publicacion;
    }
}
