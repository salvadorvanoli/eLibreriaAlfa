package ti.elibreriaalfa.dtos.modelos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ElementoListaDto {
    private Long id;
    private String imagen;
    private String etiqueta;
    private String texto1;
    private String texto2;
    private String texto3;
    private String texto4;
}
