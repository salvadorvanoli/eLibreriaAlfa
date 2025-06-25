package ti.elibreriaalfa.exceptions.publicacion;

public class PublicacionNoEncontradaException extends PublicacionException {
    
    public PublicacionNoEncontradaException(String message) {
        super(message);
    }
    
    public PublicacionNoEncontradaException(Long id) {
        super("No se encontró la publicación con ID: " + id);
    }
}
