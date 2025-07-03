package ti.elibreriaalfa.business.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "ENCARGUES")
public class Encargue {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private Float total;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonManagedReference
    private Usuario usuario;

    @OneToMany(mappedBy = "encargue", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Producto_Encargue> productos;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Encargue_Estado estado = Encargue_Estado.EN_CREACION;

    @Column
    private LocalDate fecha = null;

}
