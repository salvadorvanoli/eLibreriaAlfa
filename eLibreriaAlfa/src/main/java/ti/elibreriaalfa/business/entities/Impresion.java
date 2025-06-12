package ti.elibreriaalfa.business.entities;

import jakarta.persistence.*;
import lombok.Data;
import ti.elibreriaalfa.dtos.impresion.ImpresionDto;

@Data
@Entity
@Table(name = "IMPRESIONES")
public class Impresion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private Boolean color;

    private String formato;

    private String tipoPapel;

    private Boolean dobleCara;

    private String orientacion;

    private String estado = "Pendiente";

    private String nombreArchivo;

    @Column(length = 200)
    private String comentarioAdicional;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public ImpresionDto mapToDto() {
        ImpresionDto impresionDto = new ImpresionDto();
        impresionDto.setNombreArchivo(this.getNombreArchivo());
        impresionDto.setId(this.getId());
        impresionDto.setEstado(this.getEstado());
        impresionDto.setUsuario(this.getUsuario().mapToDtoSimple());
        impresionDto.setColor(this.getColor());
        impresionDto.setComentarioAdicional(this.getComentarioAdicional());
        impresionDto.setFormato(this.getFormato());
        impresionDto.setTipoPapel(this.getTipoPapel());
        impresionDto.setDobleCara(this.getDobleCara());
        impresionDto.setOrientacion(this.getOrientacion());
        return impresionDto;
    }
}
