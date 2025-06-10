package ti.elibreriaalfa.dtos.usuario;

import lombok.Data;
import ti.elibreriaalfa.business.entities.Rol;
import ti.elibreriaalfa.business.entities.Usuario;
import ti.elibreriaalfa.exceptions.usuario.UsuarioException;
import ti.elibreriaalfa.utils.Constants;

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

    public void validateAccesoUsuarioDto() {
        if (this.email == null || this.email.isBlank()) {
            throw new UsuarioException(Constants.ERROR_EMAIL_VACIO);
        } else if (!this.email.matches(Constants.EMAIL_REGEX)) {
            throw new UsuarioException(Constants.ERROR_EMAIL_FORMATO);
        }

        if (this.contrasenia == null || this.contrasenia.length() < Constants.MIN_CONTRASENIA_LENGTH) {
            throw new UsuarioException(Constants.ERROR_CONTRASENIA_LENGTH);
        }
    }
}
