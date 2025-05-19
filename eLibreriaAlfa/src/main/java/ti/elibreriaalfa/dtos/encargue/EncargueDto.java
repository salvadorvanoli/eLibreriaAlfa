package ti.elibreriaalfa.dtos.encargue;

import lombok.Data;
import lombok.NoArgsConstructor;
import ti.elibreriaalfa.business.entities.Encargue;
import ti.elibreriaalfa.business.entities.Encargue_Estado;
import ti.elibreriaalfa.dtos.producto_encargue.Producto_EncargueDto;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class EncargueDto {
    private Long id;

    private Float total;

    private Long IdUsuarioComprador;

    private List<Producto_EncargueDto> productos;

    private Encargue_Estado estado;

    private LocalDate fecha;

    public EncargueDto(Encargue encargue) {
        this.id = encargue.getId();
        this.total = encargue.getTotal();
        this.IdUsuarioComprador = encargue.getUsuario().getId();
        this.estado = encargue.getEstado();
        if (encargue.getProductos() != null) {
            this.productos = encargue.getProductos().stream()
                    .map(Producto_EncargueDto::new)
                    .collect(Collectors.toList());
        }
        this.fecha = encargue.getFecha();
    }

}
