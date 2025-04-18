package ti.elibreriaalfa.business.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import ti.elibreriaalfa.dtos.ComentarioDto;
import ti.elibreriaalfa.dtos.EncargueDto;
import ti.elibreriaalfa.dtos.ImpresionDto;
import ti.elibreriaalfa.dtos.usuario.UsuarioDto;

import java.util.List;

@Data
@Entity
@Table(name = "USUARIOS")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @Column(nullable = false)
    private String contrasenia;

    private String nombre;

    private String apellido;

    private String telefono;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    @Column(nullable = false)
    @JsonBackReference
    private List<Comentario> comentarios;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(nullable = false)
    @JsonBackReference
    private List<Impresion> impresiones;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(nullable = false)
    @JsonBackReference
    private List<Encargue> encargues;

    public UsuarioDto mapToDto() {
        UsuarioDto usuarioDto = new UsuarioDto();
        usuarioDto.setId(id);
        usuarioDto.setEmail(email);
        usuarioDto.setRol(rol);
        usuarioDto.setContrasenia(contrasenia);
        usuarioDto.setNombre(nombre);
        usuarioDto.setApellido(apellido);
        usuarioDto.setTelefono(telefono);
        /*
        List<ComentarioDto> comentariosDto = comentarios.forEach(comentario -> comentario.mapToDto());
        usuarioDto.setComentarios(comentariosDto);
        List<ImpresionDto> impresionesDto = impresiones.forEach(impresion -> impresion.mapToDto());
        usuarioDto.setImpresiones(impresionesDto);
        List<EncargueDto> encarguesDto = impresiones.forEach(encargue -> encargue.mapToDto());
        usuarioDto.setEncargues(encarguesDto);
        */
        return usuarioDto;
    }
}
