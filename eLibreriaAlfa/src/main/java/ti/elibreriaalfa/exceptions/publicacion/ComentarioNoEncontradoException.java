package ti.elibreriaalfa.exceptions.publicacion;

/**
 * Excepción lanzada cuando no se encuentra un comentario específico
 */
public class ComentarioNoEncontradoException extends PublicacionException {
    
    public ComentarioNoEncontradoException(String message) {
        super(message);
    }
    
    public ComentarioNoEncontradoException(Long id) {
        super("No se encontró el comentario con ID: " + id);
    }
}
