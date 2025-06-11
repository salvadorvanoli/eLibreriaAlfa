package ti.elibreriaalfa.exceptions.publicacion;

/**
 * Excepción lanzada cuando no se encuentra una publicación específica
 */
public class PublicacionNoEncontradaException extends PublicacionException {
    
    public PublicacionNoEncontradaException(String message) {
        super(message);
    }
    
    public PublicacionNoEncontradaException(Long id) {
        super("No se encontró la publicación con ID: " + id);
    }
}
