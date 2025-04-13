package ti.elibreriaalfa.business.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ti.elibreriaalfa.business.entities.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
