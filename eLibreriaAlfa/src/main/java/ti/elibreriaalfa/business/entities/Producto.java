package ti.elibreriaalfa.business.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import ti.elibreriaalfa.dtos.modelos.ElementoListaDto;
import ti.elibreriaalfa.dtos.producto.ProductoSimpleDto;

import java.util.List;

@Data
@Entity
@Table(name = "PRODUCTOS")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Float precio;

    @Column(nullable = false)
    private String descripcion;

    private String[] imagenes;

    @ManyToMany
    @JoinTable(
            name = "PRODUCTO_CATEGORIA",
            joinColumns = @JoinColumn(name = "producto_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    @JsonBackReference
    private List<Categoria> categorias;

    public ProductoSimpleDto mapToDtoSimple() {
        return new ProductoSimpleDto(this);
    }

    public ElementoListaDto mapToElementoListaDto() {
        ElementoListaDto elementoListaDto = new ElementoListaDto();
        elementoListaDto.setId(this.id);
        String imagen = (this.imagenes != null && this.imagenes.length > 0) ? this.imagenes[0] : null;
        elementoListaDto.setImagen(imagen);
        String texto1 = (this.categorias != null && !this.categorias.isEmpty()) ?
                this.categorias.stream().map(Categoria::getNombre).reduce((a, b) -> a + " - " + b).orElse("") : "Sin categorías";
        elementoListaDto.setTexto1(texto1);
        elementoListaDto.setTexto2(this.nombre);
        elementoListaDto.setTexto4(this.precio.toString());
        return elementoListaDto;
    }
}
