package ti.elibreriaalfa.business.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.parameters.P;
import ti.elibreriaalfa.dtos.Comentario.ComentarioDto;
import ti.elibreriaalfa.dtos.Publicacion.PublicacionDto;
import ti.elibreriaalfa.dtos.Publicacion.PublicacionSimpleDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Entity
@Table(name = "PUBLICACIONES")
public class Publicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column( nullable = false )
    private String titulo;

    @Column( nullable = false )
    private LocalDateTime fechaCreacion;

    @Column( nullable = false )
    private String contenido;

    @OneToMany(mappedBy = "publicacion", fetch = FetchType.LAZY)
    @Column(nullable = false)
    @JsonBackReference
    private List<Comentario> comentarios;

    public Publicacion(Long id) {
        this.id = id;
    }

    public Publicacion() {
    }

    public PublicacionDto mapToDto() {
        PublicacionDto publicacionDto = new PublicacionDto();

        publicacionDto.setId(this.getId());
        publicacionDto.setContenido(this.getContenido());
        publicacionDto.setTitulo(this.getTitulo());
        publicacionDto.setFechaCreacion(this.getFechaCreacion());
        if(!this.getComentarios().isEmpty()) {
            publicacionDto.setComentarios(
                    this.getComentarios()
                            .stream()
                            .map(Comentario::mapToDto)
                            .collect(Collectors.toList())
            );
        } else {
            publicacionDto.setComentarios(null);
        }


        return publicacionDto;
    }

    public PublicacionSimpleDto mapToDtoSimple() {
        PublicacionSimpleDto publicacionDto = new PublicacionSimpleDto();

        publicacionDto.setId(this.getId());
        publicacionDto.setContenido(this.getContenido());
        publicacionDto.setTitulo(this.getTitulo());
        publicacionDto.setFechaCreacion(this.getFechaCreacion());

        return publicacionDto;
    }

}
