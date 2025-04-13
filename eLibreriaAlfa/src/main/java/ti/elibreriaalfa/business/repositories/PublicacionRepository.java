package ti.elibreriaalfa.business.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ti.elibreriaalfa.business.entities.Publicacion;

public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {
}
