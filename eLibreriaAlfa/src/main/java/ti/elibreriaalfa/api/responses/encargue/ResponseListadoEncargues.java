package ti.elibreriaalfa.api.responses.encargue;

import lombok.Data;
import ti.elibreriaalfa.dtos.encargue.EncargueDto;

import java.util.List;

@Data
public class ResponseListadoEncargues {

    private List<EncargueDto> encargues;

}
