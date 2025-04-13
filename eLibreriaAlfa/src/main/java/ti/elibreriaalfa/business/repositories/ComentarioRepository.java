package ti.elibreriaalfa.business.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ti.elibreriaalfa.business.entities.Comentario;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
}
