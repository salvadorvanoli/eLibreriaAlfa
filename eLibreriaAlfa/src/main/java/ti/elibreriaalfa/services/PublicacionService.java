package ti.elibreriaalfa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ti.elibreriaalfa.api.responses.publicacion.ResponseListadoPublicaciones;
import ti.elibreriaalfa.api.responses.publicacion.ResponsePublicacion;
import ti.elibreriaalfa.business.entities.Publicacion;
import ti.elibreriaalfa.business.repositories.PublicacionRepository;
import ti.elibreriaalfa.business.repositories.UsuarioRepository;
import ti.elibreriaalfa.dtos.comentario.ComentarioDto;
import ti.elibreriaalfa.dtos.publicacion.PublicacionDto;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PublicacionService {
    @Autowired
    private final PublicacionRepository publicacionRepository;

    public PublicacionService(PublicacionRepository publicacionRepository, UsuarioRepository usuarioRepository) {
        this.publicacionRepository = publicacionRepository;
    }

    public String crearPublicacion(PublicacionDto publicacionDto) {
        String response = null;

        if (publicacionDto.getId() == null) {
            Publicacion publicacion = publicacionDto.mapToEntity();

            response = "Publicación creada nro: " + publicacionRepository.save(publicacion).getId();

        }

        return response;
    }

    public void borrarPublicacion(Long idPublicacion) {
        publicacionRepository.deleteById(idPublicacion);
    }

    public ResponseListadoPublicaciones getAllPublicaciones() {
        ResponseListadoPublicaciones response = new ResponseListadoPublicaciones();
        response.setPublicaciones(publicacionRepository.findAll().stream().map(Publicacion::mapToDto).toList());
        return response;
    }

    public ResponsePublicacion getPublicacionById(Long id) {
        Optional<Publicacion> publicacion = publicacionRepository.findById(id);

        if (publicacion.isEmpty())
            throw new IllegalArgumentException("No existe una publicación con el id especificado");

        ResponsePublicacion response = new ResponsePublicacion();

        response.setPublicacion(publicacion.get().mapToDto());

        return response;
    }

    public String modificarPublicacion(Long id, PublicacionDto publicacionDto) {
        String response = null;

        Publicacion aux = publicacionRepository.findById(id).orElseThrow(() -> new RuntimeException("Impresión no existe"));

        aux.setId(publicacionDto.getId());
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
}
