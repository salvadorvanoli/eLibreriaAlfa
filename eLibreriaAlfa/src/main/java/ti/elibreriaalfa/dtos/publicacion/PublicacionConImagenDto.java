package ti.elibreriaalfa.dtos.publicacion;

import lombok.Data;
import ti.elibreriaalfa.dtos.image.ImageDto;

import java.time.LocalDateTime;

@Data
public class PublicacionConImagenDto {
    private Long id;
    private String titulo;
    private LocalDateTime fechaCreacion;
    private String contenido;
    private ImageDto imagen;
}
