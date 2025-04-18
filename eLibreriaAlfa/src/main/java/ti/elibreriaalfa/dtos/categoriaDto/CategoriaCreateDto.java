package ti.elibreriaalfa.dtos.categoriaDto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ti.elibreriaalfa.business.repositories.CategoriaRepository;

@Data
public class CategoriaCreateDto {
    private String nombre;
    private Long id;

    @Nullable
    private Long padreId;  //Solo recibe el ID del padre (no objeto completo)

    }
