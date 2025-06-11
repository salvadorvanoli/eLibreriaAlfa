package ti.elibreriaalfa.exceptions.publicacion;

/**
 * Excepción lanzada cuando los datos de un comentario no son válidos
 */
public class ComentarioValidacionException extends PublicacionException {
    
    public ComentarioValidacionException(String message) {
        super(message);
    }
}
