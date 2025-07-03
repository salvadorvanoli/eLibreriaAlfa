package ti.elibreriaalfa.dtos.usuario;

import lombok.Data;
import ti.elibreriaalfa.business.entities.Rol;
import ti.elibreriaalfa.business.entities.Usuario;

@Data
public class UsuarioSimpleDto {
    private Long id;

    private String email;

    private Rol rol;

    private String nombre;

    private String apellido;

    private String telefono;

    public Usuario mapToEntity() {
        Usuario usuario = new Usuario();

        usuario.setId(this.getId());
        usuario.setRol(this.getRol());
        usuario.setNombre(this.getNombre());
        usuario.setEmail(this.getEmail());
        usuario.setApellido(this.getApellido());
        usuario.setTelefono(this.getTelefono());

        usuario.setComentarios(null);
        usuario.setEncargues(null);
        usuario.setImpresiones(null);
        usuario.setContrasenia(null);

        return usuario;
    }
}
