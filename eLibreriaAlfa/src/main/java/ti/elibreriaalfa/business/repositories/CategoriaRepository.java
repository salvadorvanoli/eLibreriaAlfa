package ti.elibreriaalfa.business.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ti.elibreriaalfa.business.entities.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

}
