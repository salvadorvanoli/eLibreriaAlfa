package ti.elibreriaalfa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ti.elibreriaalfa.business.entities.Encargue;
import ti.elibreriaalfa.business.entities.Encargue_Estado;
import ti.elibreriaalfa.business.entities.Usuario;
import ti.elibreriaalfa.business.repositories.UsuarioRepository;
import ti.elibreriaalfa.business.repositories.EncargueRepository;
import ti.elibreriaalfa.dtos.modelos.ElementoListaDto;
import ti.elibreriaalfa.dtos.usuario.AccesoUsuarioDto;
import ti.elibreriaalfa.dtos.usuario.ModificarPerfilUsuarioDto;
import ti.elibreriaalfa.dtos.usuario.UsuarioDto;
import ti.elibreriaalfa.dtos.usuario.UsuarioSimpleDto;
import ti.elibreriaalfa.exceptions.usuario.UsuarioNoEncontradoException;
import ti.elibreriaalfa.exceptions.usuario.UsuarioYaExisteException;
import ti.elibreriaalfa.utils.Constants;

import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioService {
    @Autowired
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final EncargueRepository encargueRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, EncargueRepository encargueRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.encargueRepository = encargueRepository;
    }

    public List<UsuarioSimpleDto> getAllUsuarios() {
        return usuarioRepository.findAll().stream().map(Usuario::mapToDtoSimple).toList();
    }

    public List<UsuarioSimpleDto> getElements() {
        return usuarioRepository.findAll().stream()
                .map(Usuario::mapToDtoSimple)
                .toList();
    }

    public List<UsuarioSimpleDto> getElementsFiltrados(String textoBusqueda, String orden) {
        Specification<Usuario> spec = Specification.where(null);

        if (textoBusqueda != null && !textoBusqueda.trim().isEmpty()) {
            String busqueda = "%" + textoBusqueda.toLowerCase().trim() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("email")), busqueda),
                    cb.like(cb.lower(root.get("nombre")), busqueda),
                    cb.like(cb.lower(root.get("apellido")), busqueda),
                    cb.like(cb.lower(root.get("telefono")), busqueda),
                    cb.like(cb.lower(root.get("rol").as(String.class)), busqueda)
            ));
        }

        Sort sort = Sort.unsorted();
        if (orden != null && !orden.trim().isEmpty()) {
            sort = switch (orden.toLowerCase()) {
                case "asc" -> Sort.by(Sort.Direction.ASC, "id");
                case "desc" -> Sort.by(Sort.Direction.DESC, "id");
                default -> sort;
            };
        }

        List<Usuario> usuarios = usuarioRepository.findAll(spec, sort);

        return usuarios.stream()
                .map(Usuario::mapToDtoSimple)
                .toList();
    }

    public Page<UsuarioSimpleDto> getUsuariosPage(Integer pagina, Integer cantidad) {
        PageRequest pageRequest = PageRequest.of(pagina, cantidad);
        Sort sort = Sort.by(Sort.Direction.DESC, "email");

        return usuarioRepository.findAll(pageRequest.withSort(sort)).map(Usuario::mapToDtoSimple);
    }

    public UsuarioSimpleDto getUsuarioByEmail(String usuarioEmail) {
        Usuario usuario = getUsuarioEntityByEmail(usuarioEmail);
        return usuario.mapToDtoSimple();
    }

    public UsuarioSimpleDto registerUsuario(AccesoUsuarioDto usuario) {
        usuario.validateAccesoUsuarioDto();

        Usuario nuevoUsuario = usuario.mapToEntity();

        if (usuarioRepository.existsByEmail(usuario.getEmail()))
            throw new UsuarioYaExisteException(Constants.ERROR_EMAIL_USUARIO_YA_EXISTE);

        String contraseniaEncriptada = passwordEncoder.encode(nuevoUsuario.getContrasenia());
        nuevoUsuario.setContrasenia(contraseniaEncriptada);

        usuarioRepository.save(nuevoUsuario);

        Encargue nuevoEncargue = new Encargue();
        nuevoEncargue.setUsuario(nuevoUsuario);
        nuevoEncargue.setEstado(Encargue_Estado.EN_CREACION);
        nuevoEncargue.setProductos(new ArrayList<>());
        nuevoEncargue.setTotal(Constants.ENCARGUE_INICIAL_TOTAL);
        nuevoEncargue.setFecha(null);

        encargueRepository.save(nuevoEncargue);

        return nuevoUsuario.mapToDtoSimple();
    }

    public UsuarioSimpleDto createUsuario(UsuarioDto usuarioDto) {
        usuarioDto.validateUsuarioDto();

        if (usuarioRepository.existsByEmail(usuarioDto.getEmail()))
            throw new UsuarioYaExisteException(Constants.ERROR_EMAIL_USUARIO_YA_EXISTE);

        Usuario nuevoUsuario = usuarioDto.mapToEntity();
        String contraseniaEncriptada = passwordEncoder.encode(nuevoUsuario.getContrasenia());
        nuevoUsuario.setContrasenia(contraseniaEncriptada);

        usuarioRepository.save(nuevoUsuario);

        Encargue nuevoEncargue = new Encargue();
        nuevoEncargue.setUsuario(nuevoUsuario);
        nuevoEncargue.setEstado(Encargue_Estado.EN_CREACION);
        nuevoEncargue.setProductos(new ArrayList<>());
        nuevoEncargue.setTotal(Constants.ENCARGUE_INICIAL_TOTAL);
        nuevoEncargue.setFecha(null);

        encargueRepository.save(nuevoEncargue);

        return nuevoUsuario.mapToDtoSimple();
    }

    public UsuarioSimpleDto modifyUsuario(Long id, UsuarioDto usuarioDto) {
        usuarioDto.validateUsuarioDto();

        Usuario usuarioExistente = getUsuarioEntityById(id);

        if (!usuarioExistente.getEmail().equals(usuarioDto.getEmail()) && usuarioRepository.existsByEmail(usuarioDto.getEmail())) {
            throw new UsuarioYaExisteException(Constants.ERROR_EMAIL_USUARIO_YA_EXISTE);
        }

        usuarioExistente.setDatosUsuario(usuarioDto);
        String contraseniaEncriptada = passwordEncoder.encode(usuarioDto.getContrasenia());
        usuarioExistente.setContrasenia(contraseniaEncriptada);

        usuarioRepository.save(usuarioExistente);

        return usuarioExistente.mapToDtoSimple();
    }

    public UsuarioSimpleDto patchPerfilUsuario(String usuarioEmail, ModificarPerfilUsuarioDto perfilUsuario) {
        Usuario usuario = getUsuarioEntityByEmail(usuarioEmail);

        usuario.setDatosPerfil(perfilUsuario);
        usuarioRepository.save(usuario);

        return usuario.mapToDtoSimple();
    }

    private Usuario getUsuarioEntityById(Long usuarioId) throws UsuarioNoEncontradoException {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(Constants.ERROR_USUARIO_NO_ENCONTRADO_ID));
    }

    private Usuario getUsuarioEntityByEmail(String email) throws UsuarioNoEncontradoException {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario != null)
            return usuario;
        else
            throw new UsuarioNoEncontradoException(Constants.ERROR_USUARIO_NO_ENCONTRADO_EMAIL);
    }

    public UsuarioSimpleDto getUsuarioById(Long usuarioId) {
        Usuario usuario = getUsuarioEntityById(usuarioId);
        return usuario.mapToDtoSimple();
    }
    //a
}
