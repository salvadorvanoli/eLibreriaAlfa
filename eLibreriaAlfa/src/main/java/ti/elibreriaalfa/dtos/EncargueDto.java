package ti.elibreriaalfa.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import ti.elibreriaalfa.business.entities.Encargue;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class EncargueDto {
    private Long id;

    private Float total;

    private Long IdUsuarioComprador;

    private List<Producto_EncargueDto> productos;

    public EncargueDto(Encargue encargue) {
        this.id = encargue.getId();
        this.total = encargue.getTotal();
        this.IdUsuarioComprador = encargue.getUsuario().getId();

        if (encargue.getProductos() != null) {
            this.productos = encargue.getProductos().stream()
                    .map(Producto_EncargueDto::new)
                    .collect(Collectors.toList());
        }
    }
}
