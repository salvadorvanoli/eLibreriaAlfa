package ti.elibreriaalfa.dtos;

import lombok.Data;

import java.util.List;

@Data
public class CategoriaDto {
    private Long id;

    private String nombre;

    private List<ProductoDto> productos;

    private List<CategoriaDto> hijos;
}
