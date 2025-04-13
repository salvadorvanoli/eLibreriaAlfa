package ti.elibreriaalfa.business.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ti.elibreriaalfa.business.entities.Encargue;

public interface EncargueRepository extends JpaRepository<Encargue, Long> {
}
