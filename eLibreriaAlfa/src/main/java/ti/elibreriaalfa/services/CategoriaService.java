package ti.elibreriaalfa.services;

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
import ti.elibreriaalfa.dtos.categoria.CategoriaRequestDto;
import ti.elibreriaalfa.dtos.categoria.CategoriaNodoDto;
import ti.elibreriaalfa.dtos.categoria.CategoriaSimpleDto;
import ti.elibreriaalfa.exceptions.categoria.CategoriaNoEncontradaException;
import ti.elibreriaalfa.exceptions.categoria.CategoriaYaExisteException;
import ti.elibreriaalfa.utils.Constants;

import java.util.ArrayList;
import java.util.List;


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
        List<Categoria> categoriasRoot = categoriaRepository.findByPadreIsNullOrderByIdDesc();
        List<CategoriaNodoDto> nodos = new ArrayList<>();
        categoriasRoot.forEach(categoriaRoot -> {
            nodos.add(categoriaRoot.mapToNodoDto());
        });
        return nodos;
    }

    public CategoriaSimpleDto obtenerCategoriaPorId(Long id) {
        Categoria categoria = getCategoriaEntityById(id);
        return categoria.mapToSimpleDto();
    }

    @Transactional
    public CategoriaSimpleDto createCategoria(CategoriaRequestDto categoriaDto) {
        categoriaDto.validateCategoriaRequestDto();

        Categoria categoriaExistente = categoriaRepository.findByNombre(categoriaDto.getNombre());
        if (categoriaExistente != null)
            throw new CategoriaYaExisteException(Constants.ERROR_NOMBRE_CATEGORIA_YA_EXISTE);

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
    public CategoriaSimpleDto modifyCategoria(Long id, CategoriaRequestDto categoriaDto) {
        categoriaDto.validateCategoriaRequestDto();

        Categoria categoria = getCategoriaEntityById(id);

        Categoria categoriaExistente = categoriaRepository.findByNombre(categoriaDto.getNombre());
        if (categoriaExistente != null && !categoriaExistente.getId().equals(id))
            throw new CategoriaYaExisteException(Constants.ERROR_NOMBRE_CATEGORIA_YA_EXISTE);

        categoria.setDatosCategoria(categoriaDto);

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
    public void deleteCategoria(Long idCategoria) {
        Categoria categoria = getCategoriaEntityById(idCategoria);

        List<Producto> productos = categoria.getProductos();
        for (Producto producto : productos) {
            producto.getCategorias().remove(categoria);
            /*
            if (producto.getCategorias().isEmpty())
                producto.setOculto(true);

             */
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

    private Categoria getCategoriaEntityById(Long idCategoria) throws CategoriaNoEncontradaException {
        return categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new CategoriaNoEncontradaException(Constants.ERROR_CATEGORIA_NO_ENCONTRADA));
    }
}

