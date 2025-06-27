package ti.elibreriaalfa.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;
import ti.elibreriaalfa.api.responses.publicacion.ResponseListadoPublicaciones;
import ti.elibreriaalfa.api.responses.publicacion.ResponsePublicacion;
import ti.elibreriaalfa.business.entities.Publicacion;
import ti.elibreriaalfa.business.repositories.PublicacionRepository;
import ti.elibreriaalfa.business.repositories.UsuarioRepository;
import ti.elibreriaalfa.dtos.image.ImageDto;
import ti.elibreriaalfa.dtos.publicacion.PublicacionConImagenDto;
import ti.elibreriaalfa.dtos.publicacion.PublicacionDto;
import ti.elibreriaalfa.dtos.publicacion.PublicacionRequestDto;
import ti.elibreriaalfa.dtos.publicacion.PublicacionSimpleDto;
import ti.elibreriaalfa.exceptions.publicacion.PublicacionException;
import ti.elibreriaalfa.exceptions.publicacion.PublicacionNoEncontradaException;
import ti.elibreriaalfa.exceptions.publicacion.PublicacionValidacionException;
import ti.elibreriaalfa.utils.Constants;

import java.util.List;

@Service
@Slf4j
public class PublicacionService {
    @Autowired
    private final PublicacionRepository publicacionRepository;
    private final ImageService imageService;

    public PublicacionService(PublicacionRepository publicacionRepository, UsuarioRepository usuarioRepository, ImageService imageService) {
        this.publicacionRepository = publicacionRepository;
        this.imageService = imageService;
    }

    public PublicacionSimpleDto createPublicacion(PublicacionRequestDto publicacionDto) {
        publicacionDto.validatePublicacionDto();

        Publicacion publicacion = publicacionDto.mapToEntity();

        /*
        Publicacion publicacionRepetida = publicacionRepository.existsByTitulo(publicacion.getTitulo());
        if (publicacionRepetida != null) {
            throw new PublicacionYaExisteException("Ya existe una publicación con el título especificado");
        }
        */

        String imagen = imageService.saveImage(publicacionDto.getImagen(), Constants.IMAGEN_PUBLICACION_CARPETA);
        publicacion.setImagen(imagen);

        publicacionRepository.save(publicacion);

        return publicacion.mapToDtoSimple();
    }

    public void deletePublicacion(Long idPublicacion) {
        log.info("Iniciando eliminación de publicación con ID: {}", idPublicacion);
        
        if (idPublicacion == null || idPublicacion <= 0) {
            throw new PublicacionValidacionException("El ID de la publicación debe ser un número positivo");
        }
        
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

    public List<PublicacionDto> getElements() {
        return publicacionRepository.findAll().stream()
                .map(Publicacion::mapToDto)
                .toList();
    }

    public List<PublicacionDto> getElementsFiltrados(String textoBusqueda, String orden) {
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
                .map(Publicacion::mapToDto)
                .toList();
    }

    public ResponsePublicacion getPublicacionById(Long id) {
        log.info("Buscando publicación con ID: {}", id);
        
        if (id == null || id <= 0) {
            throw new PublicacionValidacionException("El ID de la publicación debe ser un número positivo");
        }
        
        Publicacion publicacion = getPublicacionEntityById(id);

        ResponsePublicacion response = new ResponsePublicacion();
        response.setPublicacion(publicacion.mapToDto());
        
        log.info("Publicación encontrada exitosamente: {}", publicacion.getTitulo());
        return response;
    }

    public PublicacionConImagenDto getPublicacionConImagenById(Long id) {
        Publicacion publicacion = getPublicacionEntityById(id);
        PublicacionConImagenDto publicacionConImagen = publicacion.mapToDtoConImagen();
        return addImageToDtoConImagen(publicacionConImagen, publicacion.getImagen());
    }

    public PublicacionSimpleDto modifyPublicacion(Long id, PublicacionRequestDto publicacionDto) {
        publicacionDto.validatePublicacionDto();

        Publicacion aux = getPublicacionEntityById(id);
        aux.setDatosPublicacion(publicacionDto);

        String productoImagen = aux.getImagen() == null ? null : aux.getImagen();

        String imagenAEliminar = publicacionDto.getImagenAEliminar();
        if (imagenAEliminar != null && !imagenAEliminar.isBlank())
            imageService.deleteImage(imagenAEliminar);

        MultipartFile imagenNueva = publicacionDto.getImagen();
        if (imagenNueva != null && !imagenNueva.isEmpty()) {
            String imagenPath = imageService.saveImage(imagenNueva, Constants.IMAGEN_PUBLICACION_CARPETA);
            aux.setImagen(imagenPath);
        }

        publicacionRepository.save(aux);

        return aux.mapToDtoSimple();
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

    private Publicacion getPublicacionEntityById(Long id) {
        return publicacionRepository.findById(id)
                .orElseThrow(() -> new PublicacionNoEncontradaException(Constants.ERROR_PUBLICACION_NO_ENCONTRADA_ID));
    }

    private PublicacionConImagenDto addImageToDtoConImagen(PublicacionConImagenDto publicacion, String imagen) {
        if (imagen != null) {
            ImageDto imagenInfo = imageService.getImageInfo(imagen);
            publicacion.setImagen(imagenInfo);
        } else {
            publicacion.setImagen(null);
        }
        return publicacion;
    }
}
