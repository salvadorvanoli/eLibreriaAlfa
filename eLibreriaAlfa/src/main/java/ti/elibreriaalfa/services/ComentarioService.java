package ti.elibreriaalfa.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ti.elibreriaalfa.business.entities.Comentario;
import ti.elibreriaalfa.business.repositories.ComentarioRepository;
import ti.elibreriaalfa.business.repositories.PublicacionRepository;
import ti.elibreriaalfa.dtos.comentario.ComentarioDto;
import ti.elibreriaalfa.exceptions.publicacion.*;
import ti.elibreriaalfa.exceptions.publicacion.ComentarioValidacionException;
import ti.elibreriaalfa.exceptions.publicacion.PublicacionNoEncontradaException;
import ti.elibreriaalfa.utils.Constants;

@Service
@Slf4j
public class ComentarioService {
    @Autowired
    private final ComentarioRepository comentarioRepository;

    private final PublicacionRepository publicacionRepository;

    public ComentarioService(ComentarioRepository comentarioRepository, PublicacionRepository publicacionRepository) {
        this.comentarioRepository = comentarioRepository;
        this.publicacionRepository = publicacionRepository;
    }

    public String crearComentario(ComentarioDto comentarioDto) {
        log.info("Iniciando creación de comentario");
        
        try {
            // Validaciones
            validarComentario(comentarioDto);
            
            if (comentarioDto.getId() != null) {
                throw new ComentarioValidacionException("No se puede especificar ID al crear un nuevo comentario");
            }

            // Verificar que la publicación existe
            if (comentarioDto.getPublicacion() == null || comentarioDto.getPublicacion().getId() == null) {
                throw new ComentarioValidacionException("Debe especificar una publicación válida para el comentario");
            }
            
            Long publicacionId = comentarioDto.getPublicacion().getId();
            if (!publicacionRepository.existsById(publicacionId)) {
                log.warn("Intento de crear comentario en publicación inexistente con ID: {}", publicacionId);
                throw new PublicacionNoEncontradaException(publicacionId);
            }

            Comentario comentario = comentarioDto.mapToEntity();
            Comentario comentarioGuardado = comentarioRepository.save(comentario);
            
            log.info("Comentario creado exitosamente con ID: {}", comentarioGuardado.getId());
            return Constants.SUCCESS_COMENTARIO_CREADO + comentarioGuardado.getId();
            
        } catch (Exception e) {
            log.error("Error al crear comentario: {}", e.getMessage(), e);
            throw e;
        }
    }

    public void borrarComentario(Long idComentario) {
        log.info("Iniciando eliminación de comentario con ID: {}", idComentario);
        
        if (idComentario == null || idComentario <= 0) {
            throw new ComentarioValidacionException("El ID del comentario debe ser un número positivo");
        }
        
        // Verificar que el comentario existe antes de eliminarlo
        if (!comentarioRepository.existsById(idComentario)) {
            log.warn("Intento de eliminar comentario inexistente con ID: {}", idComentario);
            throw new ComentarioNoEncontradoException(idComentario);
        }
        
        try {
            comentarioRepository.deleteById(idComentario);
            log.info("Comentario eliminado exitosamente con ID: {}", idComentario);
        } catch (Exception e) {
            log.error("Error al eliminar comentario con ID {}: {}", idComentario, e.getMessage(), e);
            throw new ComentarioValidacionException("Error al eliminar el comentario: " + e.getMessage());
        }
    }

    public Page<ComentarioDto> listadoComentariosPageByPublicacion(Integer pagina, Integer cantidad, Long idPublicacion) {
        log.info("Obteniendo comentarios paginados para publicación ID: {}, página: {}, cantidad: {}", 
                idPublicacion, pagina, cantidad);
        
        // Validar parámetros
        if (pagina < 0) {
            throw new ComentarioValidacionException("El número de página no puede ser negativo");
        }
        
        if (cantidad <= 0) {
            throw new ComentarioValidacionException("La cantidad de elementos debe ser mayor a cero");
        }
        
        if (idPublicacion == null || idPublicacion <= 0) {
            throw new ComentarioValidacionException("El ID de la publicación debe ser un número positivo");
        }
        
        // Verificar que la publicación existe
        if (!publicacionRepository.existsById(idPublicacion)) {
            log.warn("Intento de obtener comentarios de publicación inexistente con ID: {}", idPublicacion);
            throw new PublicacionNoEncontradaException(idPublicacion);
        }
        
        Pageable pageable = PageRequest.of(pagina, cantidad);
        return comentarioRepository.findByPublicacionId(idPublicacion, pageable).map(Comentario::mapToDto);
    }
    
    // Métodos de validación
    private void validarComentario(ComentarioDto comentarioDto) {
        if (comentarioDto == null) {
            throw new ComentarioValidacionException("Los datos del comentario no pueden ser nulos");
        }
        
        // Validar usuario
        if (comentarioDto.getUsuario() == null || comentarioDto.getUsuario().getId() == null) {
            throw new ComentarioValidacionException("Debe especificar un usuario válido para el comentario");
        }
        
        // Validar publicación
        if (comentarioDto.getPublicacion() == null || comentarioDto.getPublicacion().getId() == null) {
            throw new ComentarioValidacionException("Debe especificar una publicación válida para el comentario");
        }
    }
}
