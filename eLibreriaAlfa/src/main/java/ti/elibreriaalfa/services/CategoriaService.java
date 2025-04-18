package ti.elibreriaalfa.services;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import ti.elibreriaalfa.api.respones.ResponseListadoCategorias;
import ti.elibreriaalfa.business.repositories.CategoriaRepository;
import ti.elibreriaalfa.business.repositories.ProductoRepository;
import ti.elibreriaalfa.business.entities.Categoria;
import ti.elibreriaalfa.business.entities.Producto;
import ti.elibreriaalfa.dtos.categoriaDto.CategoriaDto;
import ti.elibreriaalfa.dtos.categoriaDto.CategoriaCreateDto;
import ti.elibreriaalfa.dtos.productoDto.ProductoSimpleDto;

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
            Set<Long> productoIds = categoriaDto.getProductos().stream()
                    .map(ProductoSimpleDto::getId)
                    .collect(Collectors.toSet());

            categoria.getProductos().removeIf(producto ->
                    !productoIds.contains(producto.getId()));

            List<Producto> productosAAgregar = productoRepository.findAllById(productoIds).stream()
                    .filter(producto -> !categoria.getProductos().contains(producto))
                    .collect(Collectors.toList());

            productosAAgregar.forEach(producto -> {
                producto.getCategorias().add(categoria);
                productoRepository.save(producto);
            });
        }

        categoriaRepository.save(categoria);
        return "Categoría modificada exitosamente";
    }

    @Transactional
    public void borrarCategoria(Long idCategoria) {
        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        for (Producto producto : categoria.getProductos()) {
            producto.getCategorias().remove(categoria);
        }

        categoria.getProductos().clear(); // También vacía desde el otro lado
        categoriaRepository.delete(categoria);
    }

    private CategoriaDto mapToDto(Categoria categoria) {
        return new CategoriaDto(categoria); // ¡Solo una línea!
    }
}

