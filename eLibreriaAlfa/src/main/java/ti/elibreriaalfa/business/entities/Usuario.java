package ti.elibreriaalfa.business.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ti.elibreriaalfa.dtos.modelos.ElementoListaDto;
import ti.elibreriaalfa.dtos.usuario.ModificarPerfilUsuarioDto;
import ti.elibreriaalfa.dtos.usuario.UsuarioSimpleDto;
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

    @Size(min = 1, max = 40, message = "El nombre debe tener entre 1 y 40 caracteres")
    private String nombre;

    @Size(min = 1, max = 40, message = "El apellido debe tener entre 1 y 40 caracteres")
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

    public UsuarioSimpleDto mapToDtoSimple() {
        UsuarioSimpleDto usuarioSimpleDto = new UsuarioSimpleDto();

        usuarioSimpleDto.setId(this.getId());
        usuarioSimpleDto.setEmail(this.getEmail());
        usuarioSimpleDto.setRol(this.getRol());
        usuarioSimpleDto.setNombre(this.getNombre());
        usuarioSimpleDto.setApellido(this.getApellido());
        usuarioSimpleDto.setTelefono(this.getTelefono());

        return usuarioSimpleDto;
    }

    public void setDatosPerfil(ModificarPerfilUsuarioDto perfilUsuario) {
        this.nombre = perfilUsuario.getNombre();
        this.apellido = perfilUsuario.getApellido();
        this.telefono = perfilUsuario.getTelefono();
    }

    public UsuarioDto mapToDto() {
        UsuarioDto usuarioDto = new UsuarioDto();
        usuarioDto.setId(this.id);
        usuarioDto.setEmail(this.email);
        usuarioDto.setContrasenia(this.contrasenia);
        usuarioDto.setRol(this.rol);
        usuarioDto.setNombre(this.nombre);
        usuarioDto.setApellido(this.apellido);
        usuarioDto.setTelefono(this.telefono);
        return usuarioDto;
    }

    public ElementoListaDto mapToElementoListaDto() {
        ElementoListaDto elementoListaDto = new ElementoListaDto();
        elementoListaDto.setId(this.id);
        elementoListaDto.setTexto1(this.rol.toString());
        elementoListaDto.setTexto2(this.email);
        String texto3 = (this.nombre != null && this.apellido != null) ? this.nombre + " " + this.apellido : "Sin nombre completo";
        elementoListaDto.setTexto3(texto3);
        elementoListaDto.setTexto4(this.telefono != null ? this.telefono : "Sin teléfono");
        return elementoListaDto;
    }
}
