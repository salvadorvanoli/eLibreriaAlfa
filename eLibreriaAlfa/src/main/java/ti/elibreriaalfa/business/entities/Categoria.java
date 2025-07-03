package ti.elibreriaalfa.business.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ti.elibreriaalfa.dtos.categoria.CategoriaDto;
import ti.elibreriaalfa.dtos.categoria.CategoriaNodoDto;
import ti.elibreriaalfa.dtos.categoria.CategoriaRequestDto;
import ti.elibreriaalfa.dtos.categoria.CategoriaSimpleDto;
import ti.elibreriaalfa.dtos.producto.ProductoSimpleDto;
import ti.elibreriaalfa.utils.Constants;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name="CATEGORIAS")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    @Size(min = Constants.MIN_NOMBRE_CATEGORIA_LENGTH, max = Constants.MAX_NOMBRE_CATEGORIA_LENGTH, message = Constants.ERROR_NOMBRE_CATEGORIA_INVALIDO)
    private String nombre;

    @ManyToMany(mappedBy = "categorias")
    private List<Producto> productos = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "padre_id", nullable = true)
    private Categoria padre;

    @OneToMany(mappedBy = "padre", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Categoria> hijos = new ArrayList<>();

    public void agregarHijo(Categoria hijo) {
        this.hijos.add(hijo);
        hijo.setPadre(this);
    }

    public CategoriaDto mapToDto() {
        CategoriaDto categoriaDto = new CategoriaDto();

        categoriaDto.setId(this.id);
        categoriaDto.setNombre(this.nombre);

        CategoriaSimpleDto padreDto = this.padre != null ? this.padre.mapToSimpleDto() : null;
        categoriaDto.setPadre(padreDto);

        List<ProductoSimpleDto> productosDto = (this.productos != null && !this.productos.isEmpty()) ?
                this.productos.stream()
                        .map(Producto::mapToDtoSimple)
                        .toList() :
                new ArrayList<>();
        categoriaDto.setProductos(productosDto);

        return categoriaDto;
    }

    public CategoriaSimpleDto mapToSimpleDto() {
        CategoriaSimpleDto categoriaSimpleDto = new CategoriaSimpleDto();
        categoriaSimpleDto.setId(this.id);
        categoriaSimpleDto.setNombre(this.nombre);
        categoriaSimpleDto.setPadreId(this.padre != null ? this.padre.getId() : null);
        return categoriaSimpleDto;
    }

    public CategoriaNodoDto mapToNodoDto() {
        CategoriaNodoDto categoriaNodoDto = new CategoriaNodoDto();
        categoriaNodoDto.setId(this.id);
        categoriaNodoDto.setNombre(this.nombre);
        List<CategoriaNodoDto> hijos = this.hijos.stream().map(Categoria::mapToNodoDto).toList();
        categoriaNodoDto.setHijos(hijos);
        return categoriaNodoDto;
    }

    public void setDatosCategoria(CategoriaRequestDto categoriaDto) {
        this.nombre = categoriaDto.getNombre();
    }

    public List<Long> getIdsCategoriasHijas() {
        List<Long> idsHijas = new ArrayList<>();
        idsHijas.add(this.id);
        for (Categoria hijo : this.hijos) {
            idsHijas.addAll(hijo.getIdsCategoriasHijas());
        }
        return idsHijas;
    }

    public boolean esHijoDe(Categoria categoria) {
        if (this.id.equals(categoria.getId())) {
            return true;
        }
        if (this.padre != null) {
            return this.padre.esHijoDe(categoria);
        }
        return false;
    }

}
