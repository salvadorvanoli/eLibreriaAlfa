package ti.elibreriaalfa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ti.elibreriaalfa.business.entities.Comentario;
import ti.elibreriaalfa.business.repositories.ComentarioRepository;
import ti.elibreriaalfa.business.repositories.PublicacionRepository;
import ti.elibreriaalfa.dtos.comentario.ComentarioDto;

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
