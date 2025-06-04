package ti.elibreriaalfa.dtos.impresion;

import lombok.Data;
import ti.elibreriaalfa.business.entities.Impresion;
import ti.elibreriaalfa.dtos.usuario.UsuarioSimpleDto;

@Data
public class ImpresionDto {

    private Long id;

    private Boolean color;

    private Boolean simple;

    private Boolean vertical;

    private String comentarioAdicional;

    private UsuarioSimpleDto usuario;

    public Impresion mapToEntity() {
        Impresion impresion = new Impresion();

        impresion.setId(this.getId());
        impresion.setColor(this.getColor());
        impresion.setUsuario(this.getUsuario().mapToEntity());
        impresion.setSimple(this.getSimple());
        impresion.setVertical(this.getVertical());
        impresion.setComentarioAdicional(this.getComentarioAdicional());

        return impresion;
    }
}
