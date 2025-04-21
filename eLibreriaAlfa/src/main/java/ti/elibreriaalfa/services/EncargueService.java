package ti.elibreriaalfa.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ti.elibreriaalfa.api.respones.ResponseListadoEncargues;
import ti.elibreriaalfa.business.entities.*;
import ti.elibreriaalfa.business.repositories.*;
import ti.elibreriaalfa.dtos.EncargueDto;
import ti.elibreriaalfa.dtos.Producto_EncargueDto;
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

        responseListadoEncargues.setEncargues(encargueRepository.findAll().stream()
                .map(this::mapToDto).toList());

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

    private EncargueDto mapToDto(Encargue encargue) {
        return new EncargueDto(encargue);
    }

}
