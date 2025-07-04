package ti.elibreriaalfa.business.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ti.elibreriaalfa.dtos.categoria.CategoriaSimpleDto;
import ti.elibreriaalfa.dtos.producto.ProductoConImagenesDto;
import ti.elibreriaalfa.dtos.producto.ProductoDto;
import ti.elibreriaalfa.dtos.producto.ProductoRequestDto;
import ti.elibreriaalfa.dtos.producto.ProductoSimpleDto;
import ti.elibreriaalfa.utils.Constants;

import java.util.Collections;
import java.util.List;

@Data
@Entity
@Table(name = "PRODUCTOS")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private boolean habilitado = true;

    @Column(unique = true, nullable = false)
    @Size(min = Constants.MIN_NOMBRE_PRODUCTO_LENGTH, max = Constants.MAX_NOMBRE_PRODUCTO_LENGTH, message = Constants.ERROR_NOMBRE_PRODUCTO_INVALIDO)
    private String nombre;

    @Column(nullable = false)
    @Min(value = Constants.MIN_PRECIO_PRODUCTO, message = Constants.ERROR_PRECIO_PRODUCTO_INVALIDO)
    private Float precio;

    @Column(nullable = false)
    @Size(min = Constants.MIN_DESCRIPCION_PRODUCTO_LENGTH, max = Constants.MAX_DESCRIPCION_PRODUCTO_LENGTH, message = Constants.ERROR_DESCRIPCION_PRODUCTO_INVALIDA)
    private String descripcion;

    private String[] imagenes;

    @ManyToMany
    @JoinTable(
            name = "PRODUCTO_CATEGORIA",
            joinColumns = @JoinColumn(name = "producto_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    @JsonBackReference
    @Size(min = Constants.MIN_CATEGORIAS_PRODUCTO, message = "Debe seleccionar al menos una categoría")
    private List<Categoria> categorias;

    public ProductoDto mapToDto() {
        ProductoDto producto = new ProductoDto();

        producto.setId(this.id);
        producto.setHabilitado(this.habilitado);
        producto.setNombre(this.nombre);
        producto.setPrecio(this.precio);
        producto.setDescripcion(this.descripcion);
        producto.setImagenes(this.imagenes);
        List<CategoriaSimpleDto> categorias = (this.categorias != null)
                ? this.categorias.stream()
                .map(Categoria::mapToSimpleDto)
                .toList()
                : Collections.emptyList();
        producto.setCategorias(categorias);

        return producto;
    }

    public ProductoSimpleDto mapToDtoSimple() {
        ProductoSimpleDto producto = new ProductoSimpleDto();
        producto.setHabilitado(this.habilitado);
        producto.setId(this.id);
        producto.setNombre(this.nombre);
        producto.setPrecio(this.precio);
        producto.setDescripcion(this.descripcion);

        // Si no hay imágenes, enviar null directamente
        if (this.imagenes == null || this.imagenes.length == 0) {
            producto.setImagenes(null);
        } else {
            producto.setImagenes(this.imagenes);
        }

        return producto;
    }

    public ProductoConImagenesDto mapToDtoConImagenes() {
        ProductoConImagenesDto producto = new ProductoConImagenesDto();

        producto.setHabilitado(this.habilitado);
        producto.setId(this.id);
        producto.setNombre(this.nombre);
        producto.setPrecio(this.precio);
        producto.setDescripcion(this.descripcion);

        List<CategoriaSimpleDto> categorias = (this.categorias != null)
                ? this.categorias.stream()
                .map(Categoria::mapToSimpleDto)
                .toList()
                : Collections.emptyList();
        producto.setCategorias(categorias);

        return producto;
    }

    public void setDatosProducto(ProductoRequestDto productoDto) {
        this.nombre = productoDto.getNombre();
        this.precio = productoDto.getPrecio();
        this.descripcion = productoDto.getDescripcion();
        this.habilitado = productoDto.isHabilitado();
    }

}
