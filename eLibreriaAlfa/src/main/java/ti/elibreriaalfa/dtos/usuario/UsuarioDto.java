package ti.elibreriaalfa.dtos.usuario;

import lombok.Data;
import lombok.NoArgsConstructor;
import ti.elibreriaalfa.business.entities.Usuario;
import ti.elibreriaalfa.business.entities.Rol;

@Data
@NoArgsConstructor
public class UsuarioDto {
    private Long id;
    private String email;
    private String contrasenia;
    private String nombre;
    private String apellido;
    private String telefono;
    private Rol rol;

    public Usuario mapToEntity() {
        Usuario usuario = new Usuario();

        usuario.setEmail(email);
        usuario.setContrasenia(contrasenia);
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setTelefono(telefono);
        usuario.setRol(this.rol);

        return usuario;
    }
}
