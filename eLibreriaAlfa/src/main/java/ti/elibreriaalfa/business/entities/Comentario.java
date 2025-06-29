package ti.elibreriaalfa.business.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ti.elibreriaalfa.dtos.comentario.ComentarioDto;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "COMENTARIOS")
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private String texto;

    @Column(nullable = false)
    private String Titulo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "publicacion_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Publicacion publicacion;

    public ComentarioDto mapToDto() {
        ComentarioDto comentarioDto = new ComentarioDto();

        comentarioDto.setTexto(this.getTexto());
        comentarioDto.setId(this.getId());
        comentarioDto.setUsuario(this.getUsuario().mapToDtoSimple());
        comentarioDto.setTitulo(this.getTitulo());
        comentarioDto.setFechaCreacion(this.getFechaCreacion());
        comentarioDto.setPublicacion(this.getPublicacion().mapToDtoSimple());

        return comentarioDto;
    }
}
