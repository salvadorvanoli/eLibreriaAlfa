package ti.elibreriaalfa.services;

import jakarta.persistence.criteria.JoinType;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ti.elibreriaalfa.business.entities.Categoria;
import ti.elibreriaalfa.business.entities.Producto;
import ti.elibreriaalfa.business.repositories.CategoriaRepository;
import ti.elibreriaalfa.business.repositories.ProductoRepository;
import ti.elibreriaalfa.dtos.image.ImageDto;
import ti.elibreriaalfa.dtos.producto.ProductoConImagenesDto;
import ti.elibreriaalfa.dtos.producto.ProductoDto;
import ti.elibreriaalfa.dtos.producto.ProductoRequestDto;
import ti.elibreriaalfa.dtos.producto.ProductoSimpleDto;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Join;
import ti.elibreriaalfa.exceptions.producto.ProductoYaExisteException;
import ti.elibreriaalfa.utils.Constants;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductoService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final ImageService imageService;

    public ProductoService(CategoriaRepository categoriaRepository, ProductoRepository productoRepository, ImageService imageService) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
        this.imageService = imageService;
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

    public List<ProductoDto> getElements() {
        return productoRepository.findAll().stream()
                .map(Producto::mapToDto)
                .toList();
    }

    public List<ProductoDto> getElementsFiltrados(String textoBusqueda, String orden) {
        Specification<Producto> spec = Specification.where(null);

        if (textoBusqueda != null && !textoBusqueda.trim().isEmpty()) {
            String busqueda = "%" + textoBusqueda.toLowerCase().trim() + "%";
            spec = spec.and((root, query, cb) -> {
                Join<Producto, Categoria> categoriaJoin = root.join("categorias", JoinType.LEFT);
                return cb.or(
                        cb.like(cb.lower(root.get("nombre")), busqueda),
                        cb.like(cb.lower(root.get("descripcion")), busqueda),
                        cb.like(cb.lower(categoriaJoin.get("nombre")), busqueda)
                );
            });
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
                .map(Producto::mapToDto)
                .toList();
    }

    public ProductoDto obtenerProductoPorId(Long id) {
        Producto producto = getProductoEntityById(id);
        return producto.mapToDto();
    }

    public ProductoConImagenesDto obtenerProductoConImagenesPorId(Long id) {
        Producto producto = getProductoEntityById(id);
        ProductoConImagenesDto productoDto = producto.mapToDtoConImagenes();
        return addImagesToDtoConImagenes(productoDto, producto.getImagenes());
    }

    @Transactional
    public ProductoDto createProducto(ProductoRequestDto productoDto) {
        productoDto.validateProductoDto();

        Producto productoRepetido = productoRepository.findByNombre(productoDto.getNombre());
        if (productoRepetido != null) {
            throw new ProductoYaExisteException(Constants.ERROR_NOMBRE_PRODUCTO_YA_EXISTE);
        }

        Producto nuevoProducto = productoDto.mapToEntity();

        if (productoDto.getCategoriasIds() != null && !productoDto.getCategoriasIds().isEmpty()) {
            List<Long> categoriasIds = productoDto.getCategoriasIds();
            List<Categoria> categorias = categoriaRepository.findAllById(categoriasIds);

            if (categorias.size() != categoriasIds.size()) {
                throw new RuntimeException("Una o más categorías no fueron encontradas");
            }

            nuevoProducto.setCategorias(categorias);

            for (Categoria cat : categorias) {
                cat.getProductos().add(nuevoProducto);
                categoriaRepository.save(cat);
            }
        }

        String[] imagenes = productoDto.getImagenes().stream()
                .map(imagen -> imageService.saveImage(imagen, Constants.IMAGEN_PRODUCTO_CARPETA))
                .toArray(String[]::new);

        nuevoProducto.setImagenes(imagenes);

        productoRepository.save(nuevoProducto);
        return nuevoProducto.mapToDto();
    }

    @Transactional
    public void deleteProducto(Long idProducto) {
        Producto producto = getProductoEntityById(idProducto);

        for (Categoria categoria : producto.getCategorias()) {
            categoria.getProductos().remove(producto);
        }

        producto.getCategorias().clear();
        productoRepository.delete(producto);
    }

    @Transactional
    public ProductoDto modifyProducto(Long id, ProductoRequestDto productoDto) {
        productoDto.validateProductoDto();

        Producto producto = getProductoEntityById(id);

        Producto productoRepetido = productoRepository.findByNombre(productoDto.getNombre());
        if (productoRepetido != null && !productoRepetido.getId().equals(producto.getId())) {
            throw new ProductoYaExisteException(Constants.ERROR_NOMBRE_PRODUCTO_YA_EXISTE);
        }

        producto.setDatosProducto(productoDto);

        String[] productoImagenes = producto.getImagenes() == null ? new String[0] : producto.getImagenes();

        List<String> imagenesAEliminar = productoDto.getImagenesAEliminar();
        if (imagenesAEliminar != null && !imagenesAEliminar.isEmpty()) {
            for (String filename : productoDto.getImagenesAEliminar()) {
                imageService.deleteImage(filename);
            }

            productoImagenes = Arrays.stream(productoImagenes)
                    .filter(imagen -> !imagenesAEliminar.contains(imagen))
                    .toArray(String[]::new);
        }

        List<MultipartFile> imagenesNuevas = productoDto.getImagenes();
        if (imagenesNuevas != null && !imagenesNuevas.isEmpty()) {
            for (MultipartFile file : imagenesNuevas) {
                String imagenPath = imageService.saveImage(file, Constants.IMAGEN_PRODUCTO_CARPETA);
                productoImagenes = Arrays.copyOf(productoImagenes, productoImagenes.length + 1);
                productoImagenes[productoImagenes.length - 1] = imagenPath;
            }
        }

        producto.setImagenes(productoImagenes);

        if (productoDto.getCategoriasIds() != null && !productoDto.getCategoriasIds().isEmpty()) {
            List<Long> categoriaIds = productoDto.getCategoriasIds();

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
                    .toList();

            for (Categoria categoria : categoriasAAgregar) {
                producto.getCategorias().add(categoria);
                categoria.getProductos().add(producto);
                categoriaRepository.save(categoria);
            }
        }

        productoRepository.save(producto);

        return producto.mapToDto();
    }

    public Page<ProductoSimpleDto> listadoProductoPage(int pagina, int cantidad) {
        Pageable pageable = PageRequest.of(pagina, cantidad, Sort.by("Id").descending());
        Page<Producto> productosPage = productoRepository.findAll(pageable);
        return productosPage.map(Producto::mapToDtoSimple);
    }

    private Producto getProductoEntityById(Long idProducto) {
        return productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + idProducto));
    }

    private ProductoConImagenesDto addImagesToDtoConImagenes(ProductoConImagenesDto producto, String[] imagenes) {
        if (imagenes != null && imagenes.length > 0) {
            List<ImageDto> imagenesInfo = Arrays.stream(imagenes)
                    .map(imageService::getImageInfo)
                    .collect(Collectors.toList());
            producto.setImagenes(imagenesInfo);
        } else {
            producto.setImagenes(Collections.emptyList());
        }
        return producto;
    }

    private List<Long> getCategoriasHijas(Long idCategoria) {
        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + idCategoria));
        return categoria.getIdsCategoriasHijas();
    }
}
