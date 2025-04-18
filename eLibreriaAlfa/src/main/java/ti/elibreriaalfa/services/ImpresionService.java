package ti.elibreriaalfa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ti.elibreriaalfa.api.responses.usuario.ResponseImpresion;
import ti.elibreriaalfa.api.responses.usuario.ResponseListadoImpresiones;
import ti.elibreriaalfa.business.entities.Impresion;
import ti.elibreriaalfa.business.entities.Usuario;
import ti.elibreriaalfa.business.repositories.ImpresionRepository;
import ti.elibreriaalfa.business.repositories.UsuarioRepository;
import ti.elibreriaalfa.dtos.impresion.ImpresionDto;

import java.util.Optional;

@Service
public class ImpresionService {
    @Autowired
    private final ImpresionRepository impresionRepository;
    private final UsuarioRepository usuarioRepository;

    public ImpresionService(ImpresionRepository impresionRepository, UsuarioRepository usuarioRepository) {
        this.impresionRepository = impresionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public String crearImpresion(ImpresionDto impresionDto) {
        String response = null;

        if(impresionDto.getId() == null) {
            Optional<Usuario> usuario = usuarioRepository.findById(impresionDto.getUsuario().getId());

            if(usuario.isPresent()) {
                Impresion impresion = impresionDto.maptoEntity();

                impresion.setUsuario(usuario.get());

                response = "Impresión creada nro: " + impresionRepository.save(impresion).getId();
            }

        }

        return response;
    }

    public void borrarImpresion(Long idImpresion) {
        impresionRepository.deleteById(idImpresion);
    }

    public ResponseListadoImpresiones getAllImpresiones() {
        ResponseListadoImpresiones response = new ResponseListadoImpresiones();
        response.setImpresiones(impresionRepository.findAll().stream().map(Impresion::mapToDto).toList());
        return response;
    }

    public ResponseImpresion getImpresionById(Long id) {
        Optional<Impresion> impresion = impresionRepository.findById(id);

        if(impresion.isEmpty()) throw new IllegalArgumentException("No existe una impresión con el id especificado");

        ResponseImpresion response = new ResponseImpresion();

        response.setImpresionDto(impresion.get().mapToDto());

        return response;
    }

    public String modificarImpresion(Long id, ImpresionDto impresionDto) {
        String response = null;

        Impresion aux = impresionRepository.findById(id).orElseThrow(() -> new RuntimeException("Impresión no existe"));

        Optional<Usuario> usuario = usuarioRepository.findById(impresionDto.getUsuario().getId());

        if(usuario.isPresent()) {
            aux.setUsuario(usuario.get());
        } else {
            throw new RuntimeException("El usuario de la impresión no existe");
        }

        aux.setId(impresionDto.getId());
        aux.setSimple(impresionDto.getSimple());
        aux.setVertical(impresionDto.getVertical());
        aux.setColor(impresionDto.getColor());
        aux.setComentarioAdicional(impresionDto.getComentarioAdicional());

        impresionRepository.save(aux);

        response = "Impresión modificada con éxito";

        return response;
    }

    public Page<ImpresionDto> listadoImpresionPage(Integer pagina, Integer cantidad) {
        PageRequest pageRequest = PageRequest.of(pagina, cantidad);

        Sort sort = Sort.by(Sort.Direction.DESC, "id");

        return impresionRepository.findAll(pageRequest.withSort(sort)).map(Impresion::mapToDto);
    }
}
