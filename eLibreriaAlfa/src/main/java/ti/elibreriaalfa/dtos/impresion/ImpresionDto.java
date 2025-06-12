package ti.elibreriaalfa.dtos.impresion;

import lombok.Data;
import ti.elibreriaalfa.business.entities.Impresion;
import ti.elibreriaalfa.dtos.usuario.UsuarioSimpleDto;

@Data
public class ImpresionDto {

    private Long id;

    private Boolean color;

    private String formato;

    private String tipoPapel;

    private Boolean dobleCara;

    private String orientacion;

    private String comentarioAdicional;

    private UsuarioSimpleDto usuario;

    private String nombreArchivo;

    private String estado;

   
    public Impresion mapToEntity() {

        Impresion impresion = new Impresion();

        impresion.setNombreArchivo(this.getNombreArchivo());
        impresion.setId(this.getId());
        impresion.setColor(this.getColor());
        impresion.setUsuario(this.getUsuario().mapToEntity());
        impresion.setFormato(this.getFormato());
        impresion.setTipoPapel(this.getTipoPapel());
        impresion.setDobleCara(this.getDobleCara());
        impresion.setOrientacion(this.getOrientacion());
        impresion.setComentarioAdicional(this.getComentarioAdicional());

        return impresion;
    }
}
