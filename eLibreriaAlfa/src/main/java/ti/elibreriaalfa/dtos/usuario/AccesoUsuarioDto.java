package ti.elibreriaalfa.dtos.usuario;

import lombok.Data;
import ti.elibreriaalfa.business.entities.Rol;
import ti.elibreriaalfa.business.entities.Usuario;

@Data
public class AccesoUsuarioDto {
    private String email;

    private String contrasenia;

    public Usuario mapToEntity() {
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setRol(Rol.CLIENTE);
        usuario.setContrasenia(contrasenia);
        return usuario;
    }
}
