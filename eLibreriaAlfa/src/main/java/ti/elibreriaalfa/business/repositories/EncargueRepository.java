package ti.elibreriaalfa.business.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ti.elibreriaalfa.business.entities.Encargue;
import ti.elibreriaalfa.business.entities.Encargue_Estado;

import java.util.Optional;

public interface EncargueRepository extends JpaRepository<Encargue, Long> {
    Optional<Encargue> findByUsuario_IdAndEstado(Long usuarioId, Encargue_Estado estado);

}
