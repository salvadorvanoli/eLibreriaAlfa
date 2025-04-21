package ti.elibreriaalfa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ti.elibreriaalfa.api.responses.usuario.ResponseListadoPublicaciones;
import ti.elibreriaalfa.api.responses.usuario.ResponsePublicacion;
import ti.elibreriaalfa.business.entities.Comentario;
import ti.elibreriaalfa.business.entities.Publicacion;
import ti.elibreriaalfa.business.repositories.ComentarioRepository;
import ti.elibreriaalfa.business.repositories.PublicacionRepository;
import ti.elibreriaalfa.dtos.Comentario.ComentarioDto;
import ti.elibreriaalfa.dtos.Publicacion.PublicacionDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ComentarioService {
    @Autowired
    private final ComentarioRepository comentarioRepository;

    private final PublicacionRepository publicacionRepository;

    public ComentarioService(ComentarioRepository comentarioRepository, PublicacionRepository publicacionRepository) {
        this.comentarioRepository = comentarioRepository;
        this.publicacionRepository = publicacionRepository;
    }

    public String crearComentario(ComentarioDto comentarioDto) {
        String response = null;

        if (comentarioDto.getId() == null) {
            Comentario comentario = comentarioDto.mapToEntity();

            response = "Comentario creado nro: " + comentarioRepository.save(comentario).getId();

        }

        return response;
    }

    public void borrarComentario(Long idComentario) {
        comentarioRepository.deleteById(idComentario);
    }

    public Page<ComentarioDto> listadoComentariosPageByPublicacion(Integer pagina, Integer cantidad, Long idPublicacion) {
        Pageable pageable = PageRequest.of(pagina, cantidad);
        return comentarioRepository.findByPublicacionId(idPublicacion, pageable).map(Comentario::mapToDto);
    }
}
