package ti.elibreriaalfa.dtos.producto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import ti.elibreriaalfa.business.entities.Producto;
import ti.elibreriaalfa.exceptions.producto.ProductoException;
import ti.elibreriaalfa.utils.Constants;

import java.util.List;

@Data
public class ProductoRequestDto {
    private String nombre;
    private Float precio;
    private String descripcion;
    private List<MultipartFile> imagenes;
    private List<String> imagenesAEliminar;
    private List<Long> categoriasIds;

    public Producto mapToEntity() {
        Producto producto = new Producto();

        producto.setNombre(nombre);
        producto.setPrecio(precio);
        producto.setDescripcion(descripcion);

        return producto;
    }

    public void validateProductoDto() {
        if (this.nombre == null || this.nombre.isBlank() || this.nombre.length() > Constants.MAX_NOMBRE_PRODUCTO_LENGTH) {
            throw new ProductoException(Constants.ERROR_NOMBRE_PRODUCTO_INVALIDO);
        }

        if (this.precio == null || this.precio < Constants.MIN_PRECIO_PRODUCTO) {
            throw new ProductoException(Constants.ERROR_PRECIO_PRODUCTO_INVALIDO);
        }

        if (this.descripcion == null || this.descripcion.isBlank() || this.descripcion.length() > Constants.MAX_DESCRIPCION_PRODUCTO_LENGTH) {
            throw new ProductoException(Constants.ERROR_DESCRIPCION_PRODUCTO_INVALIDA);
        }

        if (this.categoriasIds == null || this.categoriasIds.isEmpty()) {
            throw new ProductoException(Constants.ERROR_CATEGORIAS_PRODUCTO_INVALIDAS);
        }
    }
}
