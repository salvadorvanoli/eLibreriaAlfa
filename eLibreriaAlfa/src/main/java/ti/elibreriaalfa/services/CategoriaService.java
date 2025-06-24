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
import ti.elibreriaalfa.business.entities.Usuario;
import ti.elibreriaalfa.business.repositories.CategoriaRepository;
import ti.elibreriaalfa.business.repositories.ProductoRepository;
import ti.elibreriaalfa.business.entities.Categoria;
import ti.elibreriaalfa.business.entities.Producto;
import ti.elibreriaalfa.dtos.categoria.CategoriaDto;
import ti.elibreriaalfa.dtos.categoria.CategoriaRequestDto;
import ti.elibreriaalfa.dtos.categoria.CategoriaNodoDto;
import ti.elibreriaalfa.dtos.categoria.CategoriaSimpleDto;
import ti.elibreriaalfa.dtos.producto.ProductoSimpleDto;
import ti.elibreriaalfa.exceptions.usuario.UsuarioNoEncontradoException;
import ti.elibreriaalfa.utils.Constants;

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

    public ResponseListadoCategorias listadoCategorias() { // Modificar esta función para que, por cada categoría, también agregue a los hijos y productos
        ResponseListadoCategorias responseListadoCategorias = new ResponseListadoCategorias();

        responseListadoCategorias.setCategorias(categoriaRepository.findAll().stream()
                .map(Categoria::mapToDto).toList());

        return responseListadoCategorias;
    }

    public List<CategoriaNodoDto> getAllCategoriasTree() {
        List<Categoria> categoriasRoot = categoriaRepository.findByPadreIsNull();
        List<CategoriaNodoDto> nodos = new ArrayList<>();
        categoriasRoot.forEach(categoriaRoot -> {
            nodos.add(categoriaRoot.mapToNodoDto());
        });
        return nodos;
    }

    public CategoriaDto obtenerCategoriaPorId(Long id) {
        Categoria categoria = getCategoriaEntityById(id);
        return categoria.mapToDto();
    }

    @Transactional
    public CategoriaSimpleDto crearCategoria(CategoriaRequestDto categoriaDto) {
        categoriaDto.validateCategoriaRequestDto();

        Categoria nuevaCategoria = categoriaDto.mapToEntity();

        if (categoriaDto.getPadreId() != null) {
            Categoria padre = getCategoriaEntityById(categoriaDto.getPadreId());
            nuevaCategoria.setPadre(padre);
            padre.getHijos().add(nuevaCategoria);
            categoriaRepository.save(padre);
        }

        categoriaRepository.save(nuevaCategoria);
        return nuevaCategoria.mapToSimpleDto();
    }

    @Transactional
    public CategoriaSimpleDto modificarCategoria(Long id, CategoriaRequestDto categoriaDto) {
        categoriaDto.validateCategoriaRequestDto();

        Categoria categoria = getCategoriaEntityById(id);

        categoria.setNombre(categoriaDto.getNombre());

        if (categoriaDto.getPadreId() != null) {
            Categoria padre = getCategoriaEntityById(categoriaDto.getPadreId());
            padre.getHijos().removeIf(hijo -> hijo.getId().equals(categoria.getId()));
            padre.agregarHijo(categoria);
            categoriaRepository.save(padre);
            categoria.setPadre(padre);
        } else {
            categoria.setPadre(null);
        }

        categoriaRepository.save(categoria);
        return categoria.mapToSimpleDto();
    }


    @Transactional
    public void borrarCategoria(Long idCategoria) {
        Categoria categoria = getCategoriaEntityById(idCategoria);

        List<Producto> productos = categoria.getProductos();
        for (Producto producto : productos) {
            producto.getCategorias().remove(categoria);
            if (producto.getCategorias().isEmpty())
                    productoRepository.delete(producto);
                else
                    productoRepository.save(producto);
        }

        categoria.getProductos().clear();

        categoriaRepository.delete(categoria);
    }

    public Page<CategoriaDto> listadoCategoriaPage(int pagina, int cantidad) {
        Pageable pageable = PageRequest.of(pagina, cantidad, Sort.by("Id").descending());
        Page<Categoria> categoriasPage = categoriaRepository.findAll(pageable);
        return categoriasPage.map(Categoria::mapToDto);
    }

    private Categoria getCategoriaEntityById(Long idCategoria) throws UsuarioNoEncontradoException {
        return categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new UsuarioNoEncontradoException(Constants.ERROR_CATEGORIA_NO_ENCONTRADA));
    }
}

