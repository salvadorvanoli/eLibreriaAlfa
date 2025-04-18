package ti.elibreriaalfa.dtos.usuario;

import lombok.Data;

@Data
public class ModificarPerfilUsuarioDto {
    private Long id;

    private String nombre;

    private String apellido;

    private String telefono;
}
