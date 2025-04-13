package ti.elibreriaalfa.api.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ti.elibreriaalfa.business.entities.Rol;
import ti.elibreriaalfa.business.entities.Usuario;

import java.util.ArrayList;

@Data
public class RequestRegistroUsuario {

    @Email(message = "El correo electrónico debe ser válido")
    private String email;

    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String contrasenia;

    public Usuario toUsuario() {
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setContrasenia(contrasenia);
        usuario.setRol(Rol.CLIENTE);
        usuario.setComentarios(new ArrayList<>());
        return usuario;
    }
}
