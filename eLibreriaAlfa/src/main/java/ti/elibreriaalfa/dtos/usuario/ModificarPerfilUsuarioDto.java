package ti.elibreriaalfa.dtos.usuario;

import lombok.Data;

@Data
public class ModificarPerfilUsuarioDto {
    private String nombre;

    private String apellido;

    private String telefono;
}
