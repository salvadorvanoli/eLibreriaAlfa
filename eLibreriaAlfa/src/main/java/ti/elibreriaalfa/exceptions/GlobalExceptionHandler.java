package ti.elibreriaalfa.exceptions;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ti.elibreriaalfa.exceptions.categoria.CategoriaException;
import ti.elibreriaalfa.exceptions.categoria.CategoriaNoEncontradaException;
import ti.elibreriaalfa.exceptions.categoria.CategoriaYaExisteException;
import ti.elibreriaalfa.exceptions.image.ImageException;
import ti.elibreriaalfa.exceptions.image.ImageNotFoundException;
import ti.elibreriaalfa.exceptions.producto.ProductoException;
import ti.elibreriaalfa.exceptions.producto.ProductoNoEncontradoException;
import ti.elibreriaalfa.exceptions.producto.ProductoYaExisteException;
import ti.elibreriaalfa.exceptions.usuario.UsuarioException;
import ti.elibreriaalfa.exceptions.usuario.UsuarioInicioSesionException;
import ti.elibreriaalfa.exceptions.usuario.UsuarioNoEncontradoException;
import ti.elibreriaalfa.exceptions.usuario.UsuarioYaExisteException;
import ti.elibreriaalfa.exceptions.publicacion.PublicacionException;
import ti.elibreriaalfa.exceptions.publicacion.PublicacionNoEncontradaException;
import ti.elibreriaalfa.exceptions.publicacion.PublicacionValidacionException;
import ti.elibreriaalfa.exceptions.publicacion.PublicacionAccesoDenegadoException;
import ti.elibreriaalfa.exceptions.publicacion.ComentarioNoEncontradoException;
import ti.elibreriaalfa.exceptions.publicacion.ComentarioValidacionException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Manejadores de excepciones de usuario
    @ExceptionHandler(UsuarioException.class)
    public ResponseEntity<Map<String, String>> handleUsuarioException(UsuarioException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsuarioInicioSesionException.class)
    public ResponseEntity<Map<String, String>> handleUsuarioInicioSesionException(UsuarioInicioSesionException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> handleUsuarioNoEncontradoException(UsuarioNoEncontradoException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsuarioYaExisteException.class)
    public ResponseEntity<Map<String, String>> handleUsuarioYaExisteException(UsuarioYaExisteException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    // Manejadores de excepciones de publicación
    @ExceptionHandler(PublicacionException.class)
    public ResponseEntity<Map<String, String>> handlePublicacionException(PublicacionException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PublicacionNoEncontradaException.class)
    public ResponseEntity<Map<String, String>> handlePublicacionNoEncontradaException(PublicacionNoEncontradaException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PublicacionValidacionException.class)
    public ResponseEntity<Map<String, String>> handlePublicacionValidacionException(PublicacionValidacionException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PublicacionAccesoDenegadoException.class)
    public ResponseEntity<Map<String, String>> handlePublicacionAccesoDenegadoException(PublicacionAccesoDenegadoException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ComentarioNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> handleComentarioNoEncontradoException(ComentarioNoEncontradoException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ComentarioValidacionException.class)
    public ResponseEntity<Map<String, String>> handleComentarioValidacionException(ComentarioValidacionException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Manejadores de excepciones de producto
    @ExceptionHandler(ProductoException.class)
    public ResponseEntity<Map<String, String>> handleProductoException(ProductoException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductoNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> handleProductoNoEncontradoException(ProductoNoEncontradoException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ProductoYaExisteException.class)
    public ResponseEntity<Map<String, String>> handleProductoYaExisteException(ProductoYaExisteException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    // Manejadores de excepciones de imagen
    @ExceptionHandler(ImageException.class)
    public ResponseEntity<Map<String, String>> handleImageException(ImageException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ImageNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleImageNotFoundException(ImageNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CategoriaException.class)
    public ResponseEntity<Map<String, String>> handleCategoriaException(CategoriaException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CategoriaNoEncontradaException.class)
    public ResponseEntity<Map<String, String>> handleCategoriaNoEncontradaException(CategoriaNoEncontradaException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CategoriaYaExisteException.class)
    public ResponseEntity<Map<String, String>> handleCategoriaYaExisteException(CategoriaYaExisteException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }
}
