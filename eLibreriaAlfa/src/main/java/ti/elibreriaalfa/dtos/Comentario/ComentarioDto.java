package ti.elibreriaalfa.dtos.Comentario;

import lombok.Data;
import ti.elibreriaalfa.business.entities.Comentario;
import ti.elibreriaalfa.business.entities.Publicacion;
import ti.elibreriaalfa.dtos.Publicacion.PublicacionDto;
import ti.elibreriaalfa.dtos.Publicacion.PublicacionSimpleDto;
import ti.elibreriaalfa.dtos.usuario.UsuarioDto;
import ti.elibreriaalfa.dtos.usuario.UsuarioSimpleDto;

import java.time.LocalDateTime;

@Data
public class ComentarioDto {
    private Long id;

    private LocalDateTime fechaCreacion;

    private UsuarioSimpleDto usuario;

    private String titulo;

    private String texto;

    private PublicacionSimpleDto publicacion;

    public Comentario mapToEntity() {
        Comentario comentario = new Comentario();

        comentario.setId(this.getId());
        comentario.setTexto(this.getTexto());
        comentario.setTitulo(this.getTitulo());
        comentario.setFechaCreacion(this.getFechaCreacion());
        comentario.setUsuario(this.getUsuario().mapToEntity());
        comentario.setPublicacion(new Publicacion(this.getPublicacion().getId()));

        return comentario;
    }
}
