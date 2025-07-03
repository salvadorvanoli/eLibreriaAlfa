package ti.elibreriaalfa.api.responses.impresion;

import lombok.Data;
import ti.elibreriaalfa.dtos.impresion.ImpresionDto;

import java.util.List;

@Data
public class ResponseListadoImpresiones {
    private List<ImpresionDto> impresiones;
}
