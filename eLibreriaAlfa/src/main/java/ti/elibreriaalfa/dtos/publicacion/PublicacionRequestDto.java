package ti.elibreriaalfa.dtos.publicacion;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import ti.elibreriaalfa.business.entities.Publicacion;
import ti.elibreriaalfa.exceptions.publicacion.PublicacionException;
import ti.elibreriaalfa.utils.Constants;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Data
public class PublicacionRequestDto {
    private String titulo;
    private String contenido;
    private MultipartFile imagen;
    private String imagenAEliminar;

    public Publicacion mapToEntity() {
        Publicacion publicacion = new Publicacion();

        publicacion.setTitulo(this.getTitulo());
        publicacion.setFechaCreacion(LocalDateTime.now());
        publicacion.setContenido(this.getContenido());
        publicacion.setComentarios(new ArrayList<>());

        return publicacion;
    }

    public void validatePublicacionDto() {
        if (this.titulo == null || this.titulo.isBlank() || this.titulo.length() > Constants.MAX_TITULO_PUBLICACION_LENGTH) {
            throw new PublicacionException(Constants.ERROR_TITULO_PUBLICACION_INVALIDO);
        }

        if (this.contenido == null || this.contenido.isBlank() || this.contenido.length() > Constants.MAX_CONTENIDO_PUBLICACION_LENGTH) {
            throw new PublicacionException(Constants.ERROR_PRECIO_PRODUCTO_INVALIDO);
        }

        if (this.imagen == null) {
            throw new PublicacionException(Constants.ERROR_IMAGEN_PUBLICACION_NULA);
        }
    }
}
