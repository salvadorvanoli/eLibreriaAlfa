package ti.elibreriaalfa.exceptions.publicacion;

public class ComentarioNoEncontradoException extends PublicacionException {
    
    public ComentarioNoEncontradoException(String message) {
        super(message);
    }
    
    public ComentarioNoEncontradoException(Long id) {
        super("No se encontró el comentario con ID: " + id);
    }
}
