package ti.elibreriaalfa.dtos;

import lombok.Data;
import ti.elibreriaalfa.dtos.usuario.UsuarioDto;

import java.time.LocalDateTime;

@Data
public class ComentarioDto {
    private Long id;

    private LocalDateTime fechaCreacion;

    private UsuarioDto usuario;

    private PublicacionDto publicacion;

}
