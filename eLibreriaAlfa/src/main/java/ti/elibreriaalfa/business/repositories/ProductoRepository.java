package ti.elibreriaalfa.business.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ti.elibreriaalfa.business.entities.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
