package ti.elibreriaalfa.business.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import ti.elibreriaalfa.dtos.categoria.CategoriaNodoDto;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name="CATEGORIAS")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @ManyToMany(mappedBy = "categorias")
    private List<Producto> productos;

    @ManyToOne
    @JoinColumn(name = "padre_id", nullable = true)
    private Categoria padre;

    @OneToMany(mappedBy = "padre", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Categoria> hijos;

    public void agregarHijo(Categoria hijo) {
        this.hijos.add(hijo);
        hijo.setPadre(this);
    }

    public CategoriaNodoDto mapToNodoDto() {
        CategoriaNodoDto categoriaNodoDto = new CategoriaNodoDto();
        categoriaNodoDto.setId(this.id);
        categoriaNodoDto.setNombre(this.nombre);
        List<CategoriaNodoDto> hijos = this.hijos.stream().map(Categoria::mapToNodoDto).toList();
        categoriaNodoDto.setHijos(hijos);
        List<Long> productos = this.productos.stream().map(Producto::getId).toList();
        categoriaNodoDto.setProductos(productos);
        return categoriaNodoDto;
    }
}
