package ti.elibreriaalfa.api.responses.producto;

import lombok.Data;
import ti.elibreriaalfa.dtos.productoDto.ProductoDto;
import java.util.List;

@Data
public class ResponseListadoProductos {

    private List<ProductoDto> productos;

}
