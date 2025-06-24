package ti.elibreriaalfa.business.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ti.elibreriaalfa.dtos.modelos.ElementoListaDto;
import ti.elibreriaalfa.dtos.publicacion.PublicacionConImagenDto;
import ti.elibreriaalfa.dtos.publicacion.PublicacionDto;
import ti.elibreriaalfa.dtos.publicacion.PublicacionRequestDto;
import ti.elibreriaalfa.dtos.publicacion.PublicacionSimpleDto;
import ti.elibreriaalfa.utils.Constants;

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
    @Size(min = Constants.MIN_TITULO_PUBLICACION_LENGTH, max = Constants.MAX_TITULO_PUBLICACION_LENGTH, message = Constants.ERROR_TITULO_PUBLICACION_INVALIDO)
    private String titulo;

    @Column( nullable = false )
    private LocalDateTime fechaCreacion;

    @Column( nullable = false )
    @Size(min = Constants.MIN_CONTENIDO_PUBLICACION_LENGTH, max = Constants.MAX_CONTENIDO_PUBLICACION_LENGTH, message = Constants.ERROR_CONTENIDO_PUBLICACION_INVALIDO)
    private String contenido;

    private String imagen;

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
        publicacionDto.setImagenUrl(this.getImagen());


        return publicacionDto;
    }

    public PublicacionSimpleDto mapToDtoSimple() {
        PublicacionSimpleDto publicacionDto = new PublicacionSimpleDto();

        publicacionDto.setId(this.getId());
        publicacionDto.setContenido(this.getContenido());
        publicacionDto.setTitulo(this.getTitulo());
        publicacionDto.setFechaCreacion(this.getFechaCreacion());
        publicacionDto.setImagenUrl(this.getImagen());

        return publicacionDto;
    }

    public PublicacionConImagenDto mapToDtoConImagen() {
        PublicacionConImagenDto publicacion = new PublicacionConImagenDto();

        publicacion.setId(this.id);
        publicacion.setTitulo(this.titulo);
        publicacion.setFechaCreacion(this.fechaCreacion);
        publicacion.setContenido(this.contenido);

        return publicacion;
    }

    public void setDatosPublicacion(PublicacionRequestDto publicacionDto) {
        this.titulo = publicacionDto.getTitulo();
        this.contenido = publicacionDto.getContenido();
    }

    public ElementoListaDto mapToElementoListaDto() {
        ElementoListaDto elementoListaDto = new ElementoListaDto();
        elementoListaDto.setId(this.id);
        elementoListaDto.setTexto2(this.titulo);
        elementoListaDto.setTexto3(this.comentarios.size() + " comentarios");
        elementoListaDto.setTexto4("Creado el " + this.fechaCreacion.toString());
        return elementoListaDto;
    }

}
