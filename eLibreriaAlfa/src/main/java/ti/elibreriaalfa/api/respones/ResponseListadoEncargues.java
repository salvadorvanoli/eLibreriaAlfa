package ti.elibreriaalfa.api.respones;

import lombok.Data;
import ti.elibreriaalfa.dtos.EncargueDto;

import java.util.List;

@Data
public class ResponseListadoEncargues {

    private List<EncargueDto> encargues;

}
