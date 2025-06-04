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
import ti.elibreriaalfa.exceptions.usuario.UsuarioException;
import ti.elibreriaalfa.exceptions.usuario.UsuarioNoEncontradoException;
import ti.elibreriaalfa.exceptions.usuario.UsuarioYaExisteException;
import ti.elibreriaalfa.security.SeguridadService;

import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioService {
    @Autowired
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final EncargueRepository encargueRepository;
    private final SeguridadService seguridadService;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, EncargueRepository encargueRepository, SeguridadService seguridadService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.encargueRepository = encargueRepository;
        this.seguridadService = seguridadService;
    }

    public List<UsuarioSimpleDto> getAllUsuarios() {
        return usuarioRepository.findAll().stream().map(Usuario::mapToDtoSimple).toList();
    }

    public List<ElementoListaDto> getElements() {
        return usuarioRepository.findAll().stream()
                .map(Usuario::mapToElementoListaDto)
                .toList();
    }

    public List<ElementoListaDto> getElementsFiltrados(String textoBusqueda, String orden) {
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
                .map(Usuario::mapToElementoListaDto)
                .toList();
    }

    public Page<UsuarioSimpleDto> getUsuariosPage(Integer pagina, Integer cantidad) {
        PageRequest pageRequest = PageRequest.of(pagina, cantidad);
        Sort sort = Sort.by(Sort.Direction.DESC, "email");

        return usuarioRepository.findAll(pageRequest.withSort(sort)).map(Usuario::mapToDtoSimple);
    }

    public Object getUsuarioByEmail(String usuarioEmail) {
        Usuario usuario = getUsuarioEntityByEmail(usuarioEmail);
        return this.seguridadService.isCurrentUserAdmin()
                ? usuario.mapToDto()
                : usuario.mapToDtoSimple();
    }

    public UsuarioSimpleDto registerUsuario(AccesoUsuarioDto usuario) {
        validateRegistroUsuarioDto(usuario);

        Usuario nuevoUsuario = usuario.mapToEntity();

        if (usuarioRepository.existsByEmail(usuario.getEmail()))
            throw new UsuarioYaExisteException("El correo electrónico ya está registrado");

        String contraseniaEncriptada = passwordEncoder.encode(nuevoUsuario.getContrasenia());
        nuevoUsuario.setContrasenia(contraseniaEncriptada);

        usuarioRepository.save(nuevoUsuario);

        Encargue nuevoEncargue = new Encargue();
        nuevoEncargue.setUsuario(nuevoUsuario);
        nuevoEncargue.setEstado(Encargue_Estado.EN_CREACION);
        nuevoEncargue.setProductos(new ArrayList<>());
        nuevoEncargue.setTotal(0f);
        nuevoEncargue.setFecha(null);

        encargueRepository.save(nuevoEncargue);

        return nuevoUsuario.mapToDtoSimple();
    }

    public UsuarioSimpleDto createUsuario(UsuarioDto usuarioDto) {
        validateUsuarioCompletoDto(usuarioDto);

        if (usuarioRepository.existsByEmail(usuarioDto.getEmail()))
            throw new UsuarioYaExisteException("El correo electrónico ya está registrado");

        Usuario nuevoUsuario = usuarioDto.mapToEntity();
        String contraseniaEncriptada = passwordEncoder.encode(nuevoUsuario.getContrasenia());
        nuevoUsuario.setContrasenia(contraseniaEncriptada);

        usuarioRepository.save(nuevoUsuario);

        Encargue nuevoEncargue = new Encargue();
        nuevoEncargue.setUsuario(nuevoUsuario);
        nuevoEncargue.setEstado(Encargue_Estado.EN_CREACION);
        nuevoEncargue.setProductos(new ArrayList<>());
        nuevoEncargue.setTotal(0f);
        nuevoEncargue.setFecha(null);

        encargueRepository.save(nuevoEncargue);

        return nuevoUsuario.mapToDtoSimple();
    }

    public UsuarioSimpleDto patchPerfilUsuario(String usuarioEmail, ModificarPerfilUsuarioDto perfilUsuario) {
        Usuario usuario = getUsuarioEntityByEmail(usuarioEmail);

        usuario.setDatosPerfil(perfilUsuario);
        usuarioRepository.save(usuario);

        return usuario.mapToDtoSimple();
    }

    private Usuario getUsuarioEntityById(Long usuarioId) throws UsuarioNoEncontradoException {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("No existe un usuario con el ID especificado"));
    }

    private Usuario getUsuarioEntityByEmail(String email) throws UsuarioNoEncontradoException {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario != null)
            return usuario;
        else
            throw new UsuarioNoEncontradoException("No existe un usuario con ese correo electrónico");
    }

    private void validateRegistroUsuarioDto(AccesoUsuarioDto usuario) {
        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new UsuarioException("El correo electrónico no puede estar vacío");
        }
        if (usuario.getContrasenia() == null || usuario.getContrasenia().length() < 6) {
            throw new UsuarioException("La contraseña debe tener al menos 6 caracteres");
        }
    }

    private void validateUsuarioCompletoDto(UsuarioDto usuarioDto) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        String telefonoRegex = "^(09[0-9]{7}|9[0-9]{7})$";

        if (usuarioDto.getEmail() == null || usuarioDto.getEmail().isBlank()) {
            throw new UsuarioException("El correo electrónico no puede estar vacío");
        } else if (!usuarioDto.getEmail().matches(emailRegex)) {
            throw new UsuarioException("El formato del correo electrónico no es válido");
        }

        if (usuarioDto.getContrasenia() == null || usuarioDto.getContrasenia().isBlank() || usuarioDto.getContrasenia().length() < 6) {
            throw new UsuarioException("La contraseña debe tener al menos 6 caracteres");
        }

        if (usuarioDto.getRol() == null) {
            throw new UsuarioException("El rol no puede ser nulo");
        }

        if (usuarioDto.getTelefono() == null || usuarioDto.getTelefono().isBlank()) {
            throw new UsuarioException("El número de teléfono no puede estar vacío");
        } else if (!usuarioDto.getTelefono().matches(telefonoRegex)) {
            throw new UsuarioException("El formato del teléfono no es válido. Debe ser de 9 dígitos y empezar por 09, o de 8 dígitos y empezar por 9");
        }
    }

}
