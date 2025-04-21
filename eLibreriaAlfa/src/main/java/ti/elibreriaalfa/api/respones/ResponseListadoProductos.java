package ti.elibreriaalfa.api.respones;

import lombok.Data;
import ti.elibreriaalfa.dtos.productoDto.ProductoDto;
import java.util.List;

@Data
public class ResponseListadoProductos {

    private List<ProductoDto> productos;

}
