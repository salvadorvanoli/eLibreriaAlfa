package ti.elibreriaalfa.exceptions.usuario;

public class UsuarioYaExisteException extends RuntimeException {
  public UsuarioYaExisteException(String message) {
    super(message);
  }
}
