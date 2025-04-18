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

    @Column(nullable = false)
    private Boolean simple;

    @Column(nullable = false)
    private Boolean vertical;

    @Column(length = 200)
    private String comentarioAdicional;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public ImpresionDto mapToDto() {
        ImpresionDto impresionDto = new ImpresionDto();

        impresionDto.setId(this.getId());
        impresionDto.setUsuario(this.getUsuario().mapToDtoSimple());
        impresionDto.setColor(this.getColor());
        impresionDto.setVertical(this.getVertical());
        impresionDto.setSimple(this.getSimple());
        impresionDto.setComentarioAdicional(this.getComentarioAdicional());

        return impresionDto;
    }
}
