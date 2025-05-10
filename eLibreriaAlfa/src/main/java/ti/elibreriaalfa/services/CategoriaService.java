package ti.elibreriaalfa.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ti.elibreriaalfa.api.responses.categoria.ResponseListadoCategorias;
import ti.elibreriaalfa.business.repositories.CategoriaRepository;
import ti.elibreriaalfa.business.repositories.ProductoRepository;
import ti.elibreriaalfa.business.entities.Categoria;
import ti.elibreriaalfa.business.entities.Producto;
import ti.elibreriaalfa.dtos.categoria.CategoriaDto;
import ti.elibreriaalfa.dtos.categoria.CategoriaCreateDto;
import ti.elibreriaalfa.dtos.producto.ProductoSimpleDto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Slf4j
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public CategoriaService(CategoriaRepository categoriaRepository, ProductoRepository productoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    public ResponseListadoCategorias listadoCategorias() {
        ResponseListadoCategorias responseListadoCategorias = new ResponseListadoCategorias();

        responseListadoCategorias.setCategorias(categoriaRepository.findAll().stream()
                .map(this::mapToDto).toList());

        return responseListadoCategorias;
    }

    public CategoriaDto obtenerCategoriaPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));
        return new CategoriaDto(categoria);
    }

    @Transactional
    public String crearCategoria(CategoriaCreateDto categoriaDto) {
        // Validación básica
        if (categoriaDto.getNombre() == null || categoriaDto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio");
        }

        Categoria nuevaCategoria = new Categoria();
        nuevaCategoria.setNombre(categoriaDto.getNombre());

        if (categoriaDto.getPadreId() != null) {
            Categoria padre = categoriaRepository.findById(categoriaDto.getPadreId())
                    .orElseThrow(() -> new RuntimeException("Categoría padre no encontrada"));
            nuevaCategoria.setPadre(padre);
            padre.getHijos().add(nuevaCategoria); // Bidireccionalidad
        }

        categoriaRepository.save(nuevaCategoria);
        return "Categoría creada con ID: " + nuevaCategoria.getId();
    }

    @Transactional
    public String modificarCategoria(Long id, CategoriaDto categoriaDto) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));

        categoria.setNombre(categoriaDto.getNombre());

        if (categoriaDto.getPadre() != null) {
            Categoria padre = categoriaRepository.findById(categoriaDto.getPadre().getId())
                    .orElseThrow(() -> new RuntimeException("Categoría padre no encontrada"));
            categoria.setPadre(padre);
        } else {
            categoria.setPadre(null);
        }

        if (categoriaDto.getProductos() != null) {
            Set<Long> nuevosIds = categoriaDto.getProductos().stream()
                    .map(ProductoSimpleDto::getId)
                    .collect(Collectors.toSet());

            Set<Producto> actuales = new HashSet<>(categoria.getProductos());

            for (Producto producto : actuales) {
                if (!nuevosIds.contains(producto.getId())) {
                    producto.getCategorias().remove(categoria);
                    categoria.getProductos().remove(producto);
                    productoRepository.save(producto);
                }
            }

            List<Producto> nuevosProductos = productoRepository.findAllById(nuevosIds);
            for (Producto producto : nuevosProductos) {
                if (!categoria.getProductos().contains(producto)) {
                    producto.getCategorias().add(categoria);
                    categoria.getProductos().add(producto);
                    productoRepository.save(producto);
                }
            }
        }

        categoriaRepository.save(categoria);
        return "Categoría modificada exitosamente";
    }


    @Transactional
    public void borrarCategoria(Long idCategoria) {
        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada"));

        List<Producto> productos = new ArrayList<>(categoria.getProductos());
        for (Producto producto : productos) {
            producto.getCategorias().remove(categoria);
            productoRepository.save(producto);
        }

        categoria.getProductos().clear();

        categoriaRepository.delete(categoria);
    }

    public Page<CategoriaDto> listadoCategoriaPage(int pagina, int cantidad) {
        Pageable pageable = PageRequest.of(pagina, cantidad, Sort.by("Id").descending());
        Page<Categoria> categoriasPage = categoriaRepository.findAll(pageable);
        return categoriasPage.map(this::mapToDto);
    }

    private CategoriaDto mapToDto(Categoria categoria) {
        return new CategoriaDto(categoria);
    }
}

