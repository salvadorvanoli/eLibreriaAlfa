package ti.elibreriaalfa.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ti.elibreriaalfa.api.responses.encargue.ResponseListadoEncargues;
import ti.elibreriaalfa.business.entities.*;
import ti.elibreriaalfa.business.repositories.*;
import ti.elibreriaalfa.dtos.encargue.EncargueDto;
import ti.elibreriaalfa.dtos.producto_encargue.Producto_EncargueDto;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class EncargueService {

    private final EncargueRepository encargueRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final Producto_EncargueRepository productoEncargueRepository;

    public EncargueService(EncargueRepository encargueRepository, ProductoRepository productoRepository, UsuarioRepository usuarioRepository, Producto_EncargueRepository productoEncargueRepository) {
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoEncargueRepository = productoEncargueRepository;
        this.encargueRepository = encargueRepository;
    }

    public ResponseListadoEncargues listadoEncargues() {
        ResponseListadoEncargues responseListadoEncargues = new ResponseListadoEncargues();

        responseListadoEncargues.setEncargues(
                encargueRepository.findAll().stream()
                        .filter(e -> e.getEstado() != Encargue_Estado.EN_CREACION)
                        .map(this::mapToDto)
                        .toList()
        );

        return responseListadoEncargues;
    }

    @Transactional
    public String crearEncargue(EncargueDto encargueDto) {
        if (encargueDto.getIdUsuarioComprador() == null) {
            throw new IllegalArgumentException("El ID de usuario es obligatorio");
        }

        if (encargueDto.getProductos() == null || encargueDto.getProductos().isEmpty()) {
            throw new IllegalArgumentException("Debe especificar al menos un producto");
        }

        Usuario usuario = usuarioRepository.findById(encargueDto.getIdUsuarioComprador())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        Encargue nuevoEncargue = new Encargue();
        nuevoEncargue.setUsuario(usuario);
        nuevoEncargue.setProductos(new ArrayList<>());
        nuevoEncargue.setFecha(encargueDto.getFecha());

        float total = 0f;
        Set<Long> productoIds = new HashSet<>();

        for (Producto_EncargueDto productoDto : encargueDto.getProductos()) {
            //Valida producto no duplicado
            if (!productoIds.add(productoDto.getProducto().getId())) {
                throw new IllegalArgumentException("Producto duplicado: " + productoDto.getProducto().getId());
            }

            //Valida cantidad
            if (productoDto.getCantidad() <= 0) {
                throw new IllegalArgumentException("Cantidad inválida para producto: " + productoDto.getProducto().getId());
            }

            Producto producto = productoRepository.findById(productoDto.getProducto().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado: " + productoDto.getProducto().getId()));

            Producto_Encargue pe = new Producto_Encargue();
            pe.setProducto(producto);
            pe.setEncargue(nuevoEncargue);
            pe.setCantidad(productoDto.getCantidad());

            //Calcula subtotal (precio * cantidad)
            float subtotal = producto.getPrecio() * productoDto.getCantidad();
            total += subtotal;

            nuevoEncargue.getProductos().add(pe);
        }

        nuevoEncargue.setTotal(total);
        Encargue encargueGuardado = encargueRepository.save(nuevoEncargue);

        return "Encargue creado con ID: " + encargueGuardado.getId().toString();
    }

    @Transactional
    public String modificarEncargue(Long idEncargue, EncargueDto encargueDto) {
        if (encargueDto.getIdUsuarioComprador() == null) {
            throw new IllegalArgumentException("El ID de usuario es obligatorio");
        }

        if (encargueDto.getProductos() == null || encargueDto.getProductos().isEmpty()) {
            throw new IllegalArgumentException("Debe especificar al menos un producto");
        }

        Encargue encargue = encargueRepository.findById(idEncargue)
                .orElseThrow(() -> new EntityNotFoundException("Encargue no encontrado con ID: " + idEncargue));

        if (!encargue.getUsuario().getId().equals(encargueDto.getIdUsuarioComprador())) {
            throw new IllegalArgumentException("No puede cambiar el usuario del encargue");
        }

        encargue.getProductos().clear();
        encargueRepository.save(encargue);

        float total = 0f;
        Set<Long> productoIds = new HashSet<>();

        for (Producto_EncargueDto productoDto : encargueDto.getProductos()) {
            if (!productoIds.add(productoDto.getProducto().getId())) {
                throw new IllegalArgumentException("Producto duplicado: " + productoDto.getProducto().getId());
            }

            if (productoDto.getCantidad() <= 0) {
                throw new IllegalArgumentException("Cantidad inválida para producto: " + productoDto.getProducto().getId());
            }

            Producto producto = productoRepository.findById(productoDto.getProducto().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado: " + productoDto.getProducto().getId()));

            Producto_Encargue pe = new Producto_Encargue();
            pe.setProducto(producto);
            pe.setEncargue(encargue);
            pe.setCantidad(productoDto.getCantidad());

            float subtotal = producto.getPrecio() * productoDto.getCantidad();
            total += subtotal;

            encargue.getProductos().add(pe);
        }

        encargue.setTotal(total);
        encargueRepository.save(encargue);

        return "Encargue modificado exitosamente. Nuevo total: " + total;
    }

    @Transactional
    public void borrarEncargue(Long idEncargue) {
        Encargue encargue = encargueRepository.findById(idEncargue)
                .orElseThrow(() -> new RuntimeException("Encargue no encontrado"));

        encargueRepository.delete(encargue);
    }

    public Page<EncargueDto> listadoEncarguePage(int pagina, int cantidad) {
        Pageable pageable = PageRequest.of(pagina, cantidad, Sort.by("Id").descending());
        Page<Encargue> encarguesPage = encargueRepository.findAll(pageable);
        return encarguesPage.map(this::mapToDto);
    }

    public void agregarProductoAEncarguePorUsuario(Long usuarioId, Producto_EncargueDto productoDto) {
        Encargue encargue = encargueRepository.findByUsuario_IdAndEstado(usuarioId, Encargue_Estado.EN_CREACION)
                .orElseThrow(() -> new IllegalArgumentException("No hay encargue en proceso actualmente para este usuario"));

        Producto producto = productoRepository.findById(productoDto.getProducto().getId())
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));

        boolean existe = encargue.getProductos().stream()
                .anyMatch(pe -> pe.getProducto().getId().equals(producto.getId()));
        if (existe) {
            throw new IllegalArgumentException("El producto ya está en el encargue");
        }

        Producto_Encargue pe = new Producto_Encargue();
        pe.setProducto(producto);
        pe.setEncargue(encargue);
        pe.setCantidad(productoDto.getCantidad());

        productoEncargueRepository.save(pe);
        encargue.getProductos().add(pe);

        encargue.setTotal(encargue.getTotal() + producto.getPrecio() * productoDto.getCantidad());
        encargueRepository.save(encargue);
    }

    @Transactional
    public void eliminarProductoDeEncargue(Long encargueId, Long productoEncargueId) {
        Encargue encargue = encargueRepository.findById(encargueId)
                .orElseThrow(() -> new EntityNotFoundException("Encargue no encontrado"));

        Producto_Encargue pe = productoEncargueRepository.findById(productoEncargueId)
                .orElseThrow(() -> new EntityNotFoundException("Producto_Encargue no encontrado"));

        if (!pe.getEncargue().getId().equals(encargueId)) {
            throw new IllegalArgumentException("El producto no pertenece a este encargue");
        }

        // Actualiza el total
        encargue.setTotal(encargue.getTotal() - pe.getProducto().getPrecio() * pe.getCantidad());

        encargue.getProductos().removeIf(p -> p.getId().equals(productoEncargueId));
        productoEncargueRepository.delete(pe);
        encargueRepository.save(encargue);
    }

    public EncargueDto obtenerEncarguePorUsuarioYEstado(Long usuarioId, Encargue_Estado estado) {
        Encargue encargue = encargueRepository.findByUsuario_IdAndEstado(usuarioId, estado)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró un encargue para el usuario y estado dados"));
        return new EncargueDto(encargue);
    }

    public Page<Producto_EncargueDto> listarProductosEncarguePorUsuarioYEstado(Long usuarioId, Encargue_Estado estado, int pagina, int cantidad) {
        Encargue encargue = encargueRepository.findByUsuario_IdAndEstado(usuarioId, estado)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró un encargue para el usuario y estado dados"));

        List<Producto_EncargueDto> productosDto = encargue.getProductos().stream()
                .map(Producto_EncargueDto::new)
                .toList();

        int start = Math.min(pagina * cantidad, productosDto.size());
        int end = Math.min(start + cantidad, productosDto.size());

        List<Producto_EncargueDto> paged = productosDto.subList(start, end);

        return new org.springframework.data.domain.PageImpl<>(paged, PageRequest.of(pagina, cantidad), productosDto.size());
    }

    private EncargueDto mapToDto(Encargue encargue) {
        return new EncargueDto(encargue);
    }

    public boolean usuarioTieneEncargueEnCreacion(Long usuarioId) {
        return encargueRepository.findByUsuario_IdAndEstado(usuarioId, Encargue_Estado.EN_CREACION).isPresent();
    }

    @Transactional
    public void marcarEncargueComoEnviado(Long encargueId, LocalDate fechaEnvio) {
        Encargue encargue = encargueRepository.findById(encargueId)
                .orElseThrow(() -> new EntityNotFoundException("Encargue no encontrado con ID: " + encargueId));

        encargue.setFecha(fechaEnvio);
        encargue.setEstado(Encargue_Estado.PENDIENTE);

        encargueRepository.save(encargue);
    }

    @Transactional
    public void cancelarEncargueEnviadoYCrearNuevo(Long usuarioId) {
        // Buscar encargue PENDIENTE del usuario
        Encargue encargue = encargueRepository.findByUsuario_IdAndEstado(usuarioId, Encargue_Estado.PENDIENTE)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró un encargue PENDIENTE para el usuario"));

        // Cambiar estado a CANCELADO
        encargue.setEstado(Encargue_Estado.CANCELADO);
        encargueRepository.save(encargue);

        // Crear nuevo encargue vacío en EN_CREACION
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        Encargue nuevoEncargue = new Encargue();
        nuevoEncargue.setUsuario(usuario);
        nuevoEncargue.setEstado(Encargue_Estado.EN_CREACION);
        nuevoEncargue.setProductos(new ArrayList<>());
        nuevoEncargue.setTotal(0f);
        nuevoEncargue.setFecha(null);

        encargueRepository.save(nuevoEncargue);
    }

    public List<EncargueDto> listarEncarguesPorUsuarioEstados(Long usuarioId, List<Encargue_Estado> estados) {
        return encargueRepository.findAll().stream()
                .filter(e -> e.getUsuario().getId().equals(usuarioId) && estados.contains(e.getEstado()))
                .map(this::mapToDto)
                .toList();
    }

    @Transactional
    public void cambiarEstadoEncargue(Long encargueId, String nuevoEstadoStr) {
        Encargue encargue = encargueRepository.findById(encargueId)
                .orElseThrow(() -> new EntityNotFoundException("Encargue no encontrado"));

        Encargue_Estado nuevoEstado;
        try {
            nuevoEstado = Encargue_Estado.valueOf(nuevoEstadoStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado no válido: " + nuevoEstadoStr);
        }

        encargue.setEstado(nuevoEstado);
        encargueRepository.save(encargue);

        if (nuevoEstado == Encargue_Estado.CANCELADO ||
                nuevoEstado == Encargue_Estado.ENTREGADO ||
                nuevoEstado == Encargue_Estado.COMPLETADO) {

            Usuario usuario = encargue.getUsuario();
            boolean yaExisteEnCreacion = encargueRepository
                    .findByUsuario_IdAndEstado(usuario.getId(), Encargue_Estado.EN_CREACION)
                    .isPresent();
            boolean yaExisteEnPendiente = encargueRepository
                    .findByUsuario_IdAndEstado(usuario.getId(), Encargue_Estado.PENDIENTE)
                    .isPresent();

            if (!yaExisteEnCreacion && !yaExisteEnPendiente) {
                Encargue nuevoEncargue = new Encargue();
                nuevoEncargue.setUsuario(usuario);
                nuevoEncargue.setEstado(Encargue_Estado.EN_CREACION);
                nuevoEncargue.setProductos(new ArrayList<>());
                nuevoEncargue.setTotal(0f);
                nuevoEncargue.setFecha(null);
                encargueRepository.save(nuevoEncargue);
            }
        }
    }
}
