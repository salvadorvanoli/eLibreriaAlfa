package ti.elibreriaalfa.dtos;

import lombok.Data;

import java.util.List;

@Data
public class ProductoDto {
    private Long id;

    private String nombre;

    private Float precio;

    private String descripcion;

    private String[] imagenes;

    private List<Producto_EncargueDto> encargues;

    private List<CategoriaDto> categorias;
}
