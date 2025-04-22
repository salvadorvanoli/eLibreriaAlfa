package ti.elibreriaalfa.dtos.categoria;

import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoriaCreateDto {
    private String nombre;
    private Long id;

    @Nullable
    private Long padreId;  //Solo recibe el ID del padre (no objeto completo)

    }
