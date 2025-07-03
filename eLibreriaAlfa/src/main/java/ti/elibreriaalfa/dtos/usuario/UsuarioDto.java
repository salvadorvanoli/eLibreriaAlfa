package ti.elibreriaalfa.dtos.usuario;

import lombok.Data;
import lombok.NoArgsConstructor;
import ti.elibreriaalfa.business.entities.Usuario;
import ti.elibreriaalfa.business.entities.Rol;
import ti.elibreriaalfa.exceptions.usuario.UsuarioException;
import ti.elibreriaalfa.utils.Constants;

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

    public void validateUsuarioDto() {
        if (this.email == null || this.email.isBlank() || !this.email.matches(Constants.EMAIL_REGEX) || this.email.length() > Constants.MAX_CORREO_ELECTRONICO_LENGTH) {
            throw new UsuarioException(Constants.ERROR_EMAIL_USUARIO_INVALIDO);
        }

        if (this.contrasenia == null || this.contrasenia.isBlank() || this.contrasenia.length() < Constants.MIN_CONTRASENIA_LENGTH || this.contrasenia.length() > Constants.MAX_CONTRASENIA_LENGTH) {
            throw new UsuarioException(Constants.ERROR_CONTRASENIA_USUARIO_INVALIDA);
        }

        if (this.rol == null) {
            throw new UsuarioException(Constants.ERROR_ROL_USUARIO_INVALIDO);
        }

        if (this.telefono == null || this.telefono.isBlank() || !this.telefono.matches(Constants.TELEFONO_REGEX)) {
            throw new UsuarioException(Constants.ERROR_TELEFONO_USUARIO_INVALIDO);
        }

        if (this.nombre != null) {
            this.nombre = this.nombre.trim();
            if (this.nombre.length() > Constants.MAX_NOMBRE_USUARIO_LENGTH)
                throw new UsuarioException(Constants.ERROR_NOMBRE_USUARIO_INVALIDO);
        }

        if (this.apellido != null) {
            this.apellido = this.apellido.trim();
            if (this.apellido.length() > Constants.MAX_NOMBRE_USUARIO_LENGTH)
                throw new UsuarioException(Constants.ERROR_APELLIDO_USUARIO_INVALIDO);
        }
    }
}
