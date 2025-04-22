package ti.elibreriaalfa.dtos.publicacion;

import lombok.Data;
import ti.elibreriaalfa.business.entities.Publicacion;

import java.time.LocalDateTime;

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
