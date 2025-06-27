package ti.elibreriaalfa.exceptions.publicacion;

public class PublicacionAccesoDenegadoException extends PublicacionException {
    
    public PublicacionAccesoDenegadoException(String message) {
        super(message);
    }
    
    public PublicacionAccesoDenegadoException() {
        super("No tiene permisos para realizar esta operación en la publicación");
    }
}
