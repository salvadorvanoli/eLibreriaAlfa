package ti.elibreriaalfa.services;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ti.elibreriaalfa.business.entities.Categoria;
import ti.elibreriaalfa.business.entities.Producto;
import ti.elibreriaalfa.business.repositories.CategoriaRepository;
import ti.elibreriaalfa.business.repositories.ProductoRepository;
import ti.elibreriaalfa.dtos.categoria.CategoriaSimpleDto;
import ti.elibreriaalfa.dtos.modelos.ElementoListaDto;
import ti.elibreriaalfa.dtos.producto.ProductoDto;
import ti.elibreriaalfa.dtos.producto.ProductoSimpleDto;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Join;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductoService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public ProductoService(CategoriaRepository categoriaRepository, ProductoRepository productoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    public List<ProductoSimpleDto> getAllProductos() {
        return productoRepository.findAll().stream().map(Producto::mapToDtoSimple).collect(Collectors.toList());
    }

    public List<ProductoSimpleDto> getProductosFiltrados(Long idCategoria, String textoBusqueda, String orden) {
        Specification<Producto> spec = Specification.where(null);

        if (idCategoria != null && idCategoria > 0) {
            try {
                List<Long> categoriaIds = getCategoriasHijas(idCategoria);
                spec = spec.and((root, query, cb) -> {
                    Join<Producto, Categoria> categoriaJoin = root.join("categorias");
                    return categoriaJoin.get("id").in(categoriaIds);
                });
            } catch (RuntimeException e) {
                log.warn("Categoría no encontrada con ID: {}", idCategoria);
            }
        }

        if (textoBusqueda != null && !textoBusqueda.trim().isEmpty()) {
            String busqueda = "%" + textoBusqueda.toLowerCase().trim() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("nombre")), busqueda),
                    cb.like(cb.lower(root.get("descripcion")), busqueda)
            ));
        }

        Sort sort = Sort.unsorted();
        if (orden != null && !orden.trim().isEmpty()) {
            sort = switch (orden.toLowerCase()) {
                case "asc" -> Sort.by(Sort.Direction.ASC, "precio");
                case "desc" -> Sort.by(Sort.Direction.DESC, "precio");
                default -> sort;
            };
        }

        List<Producto> productos = productoRepository.findAll(spec, sort);

        return productos.stream()
                .map(Producto::mapToDtoSimple)
                .collect(Collectors.toList());
    }

    public List<ElementoListaDto> getElements() {
        return productoRepository.findAll().stream()
                .map(Producto::mapToElementoListaDto)
                .toList();
    }

    public List<ElementoListaDto> getElementsFiltrados(String textoBusqueda, String orden) {
        Specification<Producto> spec = Specification.where(null);

        if (textoBusqueda != null && !textoBusqueda.trim().isEmpty()) {
            String busqueda = "%" + textoBusqueda.toLowerCase().trim() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("nombre")), busqueda),
                    cb.like(cb.lower(root.get("descripcion")), busqueda)
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

        List<Producto> productos = productoRepository.findAll(spec, sort);

        return productos.stream()
                .map(Producto::mapToElementoListaDto)
                .toList();
    }

    public ProductoDto obtenerProductoPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        return new ProductoDto(producto);
    }

    @Transactional
    public String crearProducto(ProductoDto productoDto) {
        if (productoDto.getNombre() == null || productoDto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio");
        }

        Producto nuevoProducto = new Producto();
        nuevoProducto.setNombre(productoDto.getNombre());
        nuevoProducto.setPrecio(productoDto.getPrecio());
        nuevoProducto.setDescripcion(productoDto.getDescripcion());
        nuevoProducto.setImagenes(productoDto.getImagenes()); // Agregada esta línea para incluir las imágenes

        if (productoDto.getCategorias() != null && !productoDto.getCategorias().isEmpty()) {
            List<Long> categoriaIds = productoDto.getCategorias().stream()
                    .map(CategoriaSimpleDto::getId)
                    .collect(Collectors.toList());

            List<Categoria> categorias = categoriaRepository.findAllById(categoriaIds);

            if (categorias.size() != categoriaIds.size()) {
                throw new RuntimeException("Una o más categorías no fueron encontradas");
            }

            nuevoProducto.setCategorias(categorias);

            for (Categoria cat : categorias) {
                cat.getProductos().add(nuevoProducto);
            }
        }

        productoRepository.save(nuevoProducto);
        return "Producto creada con ID: " + nuevoProducto.getId();
    }

    @Transactional
    public void borrarProducto(Long idProducto) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        for (Categoria categoria : producto.getCategorias()) {
            categoria.getProductos().remove(producto);
        }

        producto.getCategorias().clear(); // También desde el lado del producto
        productoRepository.delete(producto);
    }

    @Transactional
    public String modificarProducto(Long id, ProductoDto productoDto) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

        producto.setNombre(productoDto.getNombre());
        producto.setPrecio(productoDto.getPrecio());
        producto.setDescripcion(productoDto.getDescripcion());
        producto.setImagenes(productoDto.getImagenes()); // Agregada esta línea para incluir las imágenes

        if (productoDto.getCategorias() != null) {
            Set<Long> categoriaIds = productoDto.getCategorias().stream()
                    .map(CategoriaSimpleDto::getId)
                    .collect(Collectors.toSet());

            List<Categoria> categoriasActuales = new ArrayList<>(producto.getCategorias());
            for (Categoria categoria : categoriasActuales) {
                if (!categoriaIds.contains(categoria.getId())) {
                    categoria.getProductos().remove(producto);
                    producto.getCategorias().remove(categoria);
                    categoriaRepository.save(categoria);
                }
            }

            List<Categoria> categoriasAAgregar = categoriaRepository.findAllById(categoriaIds).stream()
                    .filter(categoria -> !producto.getCategorias().contains(categoria))
                    .collect(Collectors.toList());

            for (Categoria categoria : categoriasAAgregar) {
                producto.getCategorias().add(categoria);
                categoria.getProductos().add(producto);
                categoriaRepository.save(categoria);
            }
        } else {
            for (Categoria categoria : new ArrayList<>(producto.getCategorias())) {
                categoria.getProductos().remove(producto);
                categoriaRepository.save(categoria);
            }
            producto.getCategorias().clear();
        }

        productoRepository.save(producto);

        return "Producto modificado exitosamente";
    }

    public Page<ProductoDto> listadoProductoPage(int pagina, int cantidad) {
        Pageable pageable = PageRequest.of(pagina, cantidad, Sort.by("Id").descending());
        Page<Producto> productosPage = productoRepository.findAll(pageable);
        return productosPage.map(this::mapToDto);
    }

    private ProductoDto mapToDto(Producto producto) {
        return new ProductoDto(producto);
    }

    private List<Long> getCategoriasHijas(Long idCategoria) {
        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + idCategoria));
        return categoria.getIdsCategoriasHijas();
    }

}

