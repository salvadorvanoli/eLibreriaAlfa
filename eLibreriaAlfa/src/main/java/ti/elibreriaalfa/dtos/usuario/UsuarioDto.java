package ti.elibreriaalfa.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import ti.elibreriaalfa.business.entities.Rol;
import ti.elibreriaalfa.business.entities.Usuario;

import java.util.List;

@Data
@NoArgsConstructor
public class UsuarioDto {
    private Long id;

    private String email;

    private Rol rol;

    private String contrasenia;

    private String nombre;

    private String apellido;

    private String telefono;

    private List<ComentarioDto> comentarios;

    private List<ImpresionDto> impresiones;

    private List<EncargueDto> encargues;

    public UsuarioDto(Usuario usuario) {
        this.id = usuario.getId();
        this.nombre = usuario.getNombre();
    }
}
