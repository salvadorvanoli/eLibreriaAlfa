package ti.elibreriaalfa.business.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ti.elibreriaalfa.business.entities.Comentario;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    Page<Comentario> findByPublicacionId(Long publicacionId, Pageable pageable);
}