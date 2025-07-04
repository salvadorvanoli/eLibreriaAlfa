package ti.elibreriaalfa.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ti.elibreriaalfa.business.entities.Comentario;
import ti.elibreriaalfa.business.entities.Usuario;
import ti.elibreriaalfa.business.repositories.ComentarioRepository;
import ti.elibreriaalfa.business.repositories.PublicacionRepository;
import ti.elibreriaalfa.business.repositories.UsuarioRepository;
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

    private final UsuarioRepository usuarioRepository;

    public ComentarioService(ComentarioRepository comentarioRepository, PublicacionRepository publicacionRepository, UsuarioRepository usuarioRepository) {
        this.comentarioRepository = comentarioRepository;
        this.publicacionRepository = publicacionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public String crearComentario(ComentarioDto comentarioDto) {

        try {
            validarComentario(comentarioDto);
            
            if (comentarioDto.getId() != null) {
                throw new ComentarioValidacionException("No se puede especificar ID al crear un nuevo comentario");
            }

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
            
            return Constants.SUCCESS_COMENTARIO_CREADO + comentarioGuardado.getId();
            
        } catch (Exception e) {
            throw e;
        }
    }

    public void borrarComentario(Long idComentario) {

        if (idComentario == null || idComentario <= 0) {
            throw new ComentarioValidacionException("El ID del comentario debe ser un número positivo");
        }

        if (!comentarioRepository.existsById(idComentario)) {
            log.warn("Intento de eliminar comentario inexistente con ID: {}", idComentario);
            throw new ComentarioNoEncontradoException(idComentario);
        }
        
        try {
            comentarioRepository.deleteById(idComentario);
        } catch (Exception e) {
            log.error("Error al eliminar comentario con ID {}: {}", idComentario, e.getMessage(), e);
            throw new ComentarioValidacionException("Error al eliminar el comentario: " + e.getMessage());
        }
    }

    public Page<ComentarioDto> listadoComentariosPageByPublicacion(Integer pagina, Integer cantidad, Long idPublicacion) {
        
        if (pagina < 0) {
            throw new ComentarioValidacionException("El número de página no puede ser negativo");
        }
        
        if (cantidad <= 0) {
            throw new ComentarioValidacionException("La cantidad de elementos debe ser mayor a cero");
        }
        
        if (idPublicacion == null || idPublicacion <= 0) {
            throw new ComentarioValidacionException("El ID de la publicación debe ser un número positivo");
        }

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

        if (comentarioDto.getUsuario() == null || comentarioDto.getUsuario().getId() == null || !usuarioRepository.existsById(comentarioDto.getUsuario().getId())) {
            throw new ComentarioValidacionException(Constants.ERROR_COMENTARIO_SIN_USUARIO);
        }

        if (comentarioDto.getPublicacion() == null || comentarioDto.getPublicacion().getId() == null) {
            throw new ComentarioValidacionException(Constants.ERROR_COMENTARIO_SIN_PUBLICACION);
        }

        if (comentarioDto.getTitulo() != null)
            comentarioDto.setTitulo(comentarioDto.getTitulo().trim());

        if (comentarioDto.getTitulo() == null || comentarioDto.getTitulo().isEmpty() || comentarioDto.getTitulo().length() > Constants.MAX_COMENTARIO_PUBLICACION_LENGTH) {
            throw new ComentarioValidacionException(Constants.ERROR_COMENTARIO_LENGTH);
        }

        if (comentarioDto.getTexto() != null)
            comentarioDto.setTexto(comentarioDto.getTexto().trim());

        if (comentarioDto.getTexto() == null || comentarioDto.getTexto().isEmpty() || comentarioDto.getTexto().length() > Constants.MAX_COMENTARIO_PUBLICACION_LENGTH) {
            throw new ComentarioValidacionException(Constants.ERROR_COMENTARIO_LENGTH);
        }

    }
}
