package ti.elibreriaalfa.exceptions.publicacion;

/**
 * Excepción lanzada cuando un usuario no tiene permisos para acceder o modificar una publicación
 */
public class PublicacionAccesoDenegadoException extends PublicacionException {
    
    public PublicacionAccesoDenegadoException(String message) {
        super(message);
    }
    
    public PublicacionAccesoDenegadoException() {
        super("No tiene permisos para realizar esta operación en la publicación");
    }
}
