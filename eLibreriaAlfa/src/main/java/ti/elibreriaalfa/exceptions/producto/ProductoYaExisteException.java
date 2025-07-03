package ti.elibreriaalfa.exceptions.producto;

public class ProductoYaExisteException extends RuntimeException {
    public ProductoYaExisteException(String message) {
        super(message);
    }
}
