package ti.elibreriaalfa.dtos;

import lombok.Data;

import java.util.List;

@Data
public class EncargueDto {
    private Long id;

    private Integer total;

    private UsuarioDto usuario;

    private List<Producto_EncargueDto> productos;
}
