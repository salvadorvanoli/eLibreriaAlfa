package ti.elibreriaalfa.exceptions.publicacion;

/**
 * Excepción base para errores relacionados con publicaciones
 */
public class PublicacionException extends RuntimeException {
    
    public PublicacionException(String message) {
        super(message);
    }
    
    public PublicacionException(String message, Throwable cause) {
        super(message, cause);
    }
}