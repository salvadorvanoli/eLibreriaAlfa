package ti.elibreriaalfa.dtos.categoria;

import lombok.Data;

import java.util.List;

@Data
public class CategoriaNodoDto {
    private Long id;
    private String nombre;
    private List<CategoriaNodoDto> hijos;
    private List<Long> productos;
}
