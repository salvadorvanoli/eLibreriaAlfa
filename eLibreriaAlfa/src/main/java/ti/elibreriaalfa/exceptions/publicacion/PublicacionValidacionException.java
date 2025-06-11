package ti.elibreriaalfa.exceptions.publicacion;

/**
 * Excepción lanzada cuando los datos de una publicación no son válidos
 */
public class PublicacionValidacionException extends PublicacionException {
    
    public PublicacionValidacionException(String message) {
        super(message);
    }
}
