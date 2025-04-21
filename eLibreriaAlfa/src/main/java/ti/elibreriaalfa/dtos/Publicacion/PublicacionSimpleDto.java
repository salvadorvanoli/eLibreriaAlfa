package ti.elibreriaalfa.dtos.Publicacion;

import lombok.Data;
import ti.elibreriaalfa.business.entities.Impresion;
import ti.elibreriaalfa.business.entities.Publicacion;
import ti.elibreriaalfa.dtos.Comentario.ComentarioDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class PublicacionSimpleDto {

    private Long id;

    private String titulo;

    private LocalDateTime fechaCreacion;

    private String contenido;

    public Publicacion mapToEntity() {
        Publicacion publicacion = new Publicacion();

        publicacion.setId(this.getId());
        publicacion.setContenido(this.getContenido());
        publicacion.setTitulo(this.getTitulo());
        publicacion.setFechaCreacion(this.getFechaCreacion());

        return publicacion;
    }
}
