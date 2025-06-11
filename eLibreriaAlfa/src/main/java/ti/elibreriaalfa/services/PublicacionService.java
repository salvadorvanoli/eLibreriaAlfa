package ti.elibreriaalfa.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import ti.elibreriaalfa.api.responses.publicacion.ResponseListadoPublicaciones;
import ti.elibreriaalfa.api.responses.publicacion.ResponsePublicacion;
import ti.elibreriaalfa.business.entities.Producto;
import ti.elibreriaalfa.business.entities.Publicacion;
import ti.elibreriaalfa.business.repositories.PublicacionRepository;
import ti.elibreriaalfa.business.repositories.UsuarioRepository;
import ti.elibreriaalfa.dtos.comentario.ComentarioDto;
import ti.elibreriaalfa.dtos.modelos.ElementoListaDto;
import ti.elibreriaalfa.dtos.publicacion.PublicacionDto;
import ti.elibreriaalfa.exceptions.publicacion.PublicacionException;
import ti.elibreriaalfa.exceptions.publicacion.PublicacionNoEncontradaException;
import ti.elibreriaalfa.exceptions.publicacion.PublicacionValidacionException;
import ti.elibreriaalfa.utils.Constants;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PublicacionService {
    @Autowired
    private final PublicacionRepository publicacionRepository;

    public PublicacionService(PublicacionRepository publicacionRepository, UsuarioRepository usuarioRepository) {
        this.publicacionRepository = publicacionRepository;
    }    public String crearPublicacion(PublicacionDto publicacionDto) {
        log.info("Iniciando creación de publicación con título: {}", publicacionDto.getTitulo());
        
        try {
            // Validaciones
            validarPublicacion(publicacionDto);
            
            if (publicacionDto.getId() != null) {
                throw new PublicacionValidacionException("No se puede especificar ID al crear una nueva publicación");
            }

            Publicacion publicacion = publicacionDto.mapToEntity();
            Publicacion publicacionGuardada = publicacionRepository.save(publicacion);
            
            log.info("Publicación creada exitosamente con ID: {}", publicacionGuardada.getId());
            return Constants.SUCCESS_PUBLICACION_CREADA + publicacionGuardada.getId();
            
        } catch (Exception e) {
            log.error("Error al crear publicación: {}", e.getMessage(), e);
            throw e;
        }
    }

    public void borrarPublicacion(Long idPublicacion) {
        log.info("Iniciando eliminación de publicación con ID: {}", idPublicacion);
        
        if (idPublicacion == null || idPublicacion <= 0) {
            throw new PublicacionValidacionException("El ID de la publicación debe ser un número positivo");
        }
        
        // Verificar que la publicación existe antes de eliminarla
        if (!publicacionRepository.existsById(idPublicacion)) {
            log.warn("Intento de eliminar publicación inexistente con ID: {}", idPublicacion);
            throw new PublicacionNoEncontradaException(idPublicacion);
        }
        
        try {
            publicacionRepository.deleteById(idPublicacion);
            log.info("Publicación eliminada exitosamente con ID: {}", idPublicacion);
        } catch (Exception e) {
            log.error("Error al eliminar publicación con ID {}: {}", idPublicacion, e.getMessage(), e);
            throw new PublicacionException("Error al eliminar la publicación: " + e.getMessage());
        }
    }

    public ResponseListadoPublicaciones getAllPublicaciones() {
        ResponseListadoPublicaciones response = new ResponseListadoPublicaciones();
        response.setPublicaciones(publicacionRepository.findAll().stream().map(Publicacion::mapToDto).toList());
        return response;
    }

    public List<ElementoListaDto> getElements() {
        return publicacionRepository.findAll().stream()
                .map(Publicacion::mapToElementoListaDto)
                .toList();
    }

    public List<ElementoListaDto> getElementsFiltrados(String textoBusqueda, String orden) {
        Specification<Publicacion> spec = Specification.where(null);

        if (textoBusqueda != null && !textoBusqueda.trim().isEmpty()) {
            String busqueda = "%" + textoBusqueda.toLowerCase().trim() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("titulo")), busqueda),
                    cb.like(cb.lower(root.get("contenido")), busqueda)
            ));
        }

        Sort sort = Sort.unsorted();
        if (orden != null && !orden.trim().isEmpty()) {
            sort = switch (orden.toLowerCase()) {
                case "asc" -> Sort.by(Sort.Direction.ASC, "id");
                case "desc" -> Sort.by(Sort.Direction.DESC, "id");
                default -> sort;
            };
        }

        List<Publicacion> publicaciones = publicacionRepository.findAll(spec, sort);

        return publicaciones.stream()
                .map(Publicacion::mapToElementoListaDto)
                .toList();
    }

    public ResponsePublicacion getPublicacionById(Long id) {
        log.info("Buscando publicación con ID: {}", id);
        
        if (id == null || id <= 0) {
            throw new PublicacionValidacionException("El ID de la publicación debe ser un número positivo");
        }
        
        Optional<Publicacion> publicacion = publicacionRepository.findById(id);

        if (publicacion.isEmpty()) {
            log.warn("No se encontró publicación con ID: {}", id);
            throw new PublicacionNoEncontradaException(id);
        }

        ResponsePublicacion response = new ResponsePublicacion();
        response.setPublicacion(publicacion.get().mapToDto());
        
        log.info("Publicación encontrada exitosamente: {}", publicacion.get().getTitulo());
        return response;
    }

    public String modificarPublicacion(Long id, PublicacionDto publicacionDto) {
        String response = null;

        Publicacion aux = publicacionRepository.findById(id).orElseThrow(() -> new RuntimeException(Constants.ERROR_PUBLICACION_NO_ENCONTRADA));

        aux.setTitulo(publicacionDto.getTitulo());
        aux.setContenido(publicacionDto.getContenido());
        aux.setFechaCreacion(publicacionDto.getFechaCreacion());
        aux.setComentarios(
                publicacionDto.getComentarios()
                        .stream()
                        .map(ComentarioDto::mapToEntity)
                        .collect(Collectors.toList())
        );

        publicacionRepository.save(aux);

        response = "Publicación modificada con éxito";

        return response;
    }

    public Page<PublicacionDto> listadoPublicacionPage(Integer pagina, Integer cantidad) {
        PageRequest pageRequest = PageRequest.of(pagina, cantidad);

        Sort sort = Sort.by(Sort.Direction.DESC, "id");

        return publicacionRepository.findAll(pageRequest.withSort(sort)).map(Publicacion::mapToDto);
    }

    public Page<PublicacionDto> listadoPublicacionPageByFechaDESC(Integer pagina, Integer cantidad) {
        PageRequest pageRequest = PageRequest.of(pagina, cantidad);

        Sort sort = Sort.by(Sort.Direction.DESC, "fechaCreacion");

        return publicacionRepository.findAll(pageRequest.withSort(sort)).map(Publicacion::mapToDto);
    }

    // Métodos de validación
    private void validarPublicacion(PublicacionDto publicacionDto) {
        if (publicacionDto == null) {
            throw new PublicacionValidacionException("Los datos de la publicación no pueden ser nulos");
        }

        if (publicacionDto.getTitulo() == null || publicacionDto.getTitulo().trim().isEmpty()) {
            throw new PublicacionValidacionException(Constants.ERROR_TITULO_VACIO);
        }
        
        if (publicacionDto.getTitulo().trim().length() < Constants.MIN_TITULO_LENGTH || 
            publicacionDto.getTitulo().trim().length() > Constants.MAX_TITULO_LENGTH) {
            throw new PublicacionValidacionException(Constants.ERROR_TITULO_LENGTH);
        }

        if (publicacionDto.getContenido() == null || publicacionDto.getContenido().trim().isEmpty()) {
            throw new PublicacionValidacionException(Constants.ERROR_CONTENIDO_VACIO);
        }
        
        if (publicacionDto.getContenido().trim().length() < Constants.MIN_CONTENIDO_LENGTH || 
            publicacionDto.getContenido().trim().length() > Constants.MAX_CONTENIDO_LENGTH) {
            throw new PublicacionValidacionException(Constants.ERROR_CONTENIDO_LENGTH);
        }
    }
}
