package ti.elibreriaalfa.dtos;

import lombok.Data;

@Data
public class Producto_EncargueDto {
    private Long id;

    private Integer cantidad;

    private EncargueDto encargue;


}
