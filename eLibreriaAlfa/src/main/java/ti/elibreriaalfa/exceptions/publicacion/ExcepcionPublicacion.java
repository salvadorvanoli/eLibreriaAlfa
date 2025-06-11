package ti.elibreriaalfa.exceptions.publicacion;

/**
 * Excepción base para errores relacionados con publicaciones
 */
public class ExcepcionPublicacion extends RuntimeException {
    
    public ExcepcionPublicacion(String message) {
        super(message);
    }
    
    public ExcepcionPublicacion(String message, Throwable cause) {
        super(message, cause);
    }
}
