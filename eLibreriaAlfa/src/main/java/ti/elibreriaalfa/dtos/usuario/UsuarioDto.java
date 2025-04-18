package ti.elibreriaalfa.dtos.usuario;

import lombok.Data;
import ti.elibreriaalfa.business.entities.Rol;
import ti.elibreriaalfa.dtos.Comentario.ComentarioDto;
import ti.elibreriaalfa.dtos.EncargueDto;
import ti.elibreriaalfa.dtos.impresion.ImpresionDto;

import java.util.List;

@Data
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
}
