package ti.elibreriaalfa.dtos.producto;

import lombok.Data;
import ti.elibreriaalfa.business.entities.Producto;

import java.util.List;

@Data
public class ProductoRequestDto {
    private String nombre;
    private Float precio;
    private String descripcion;
    private String[] imagenes;
    private List<Long> categoriasIds;

    public Producto mapToEntity() {
        Producto producto = new Producto();

        producto.setNombre(nombre);
        producto.setPrecio(precio);
        producto.setDescripcion(descripcion);

        return producto;
    }

    public void validateProductoDto() {
        /*
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ProductoException(Constants.ERROR_NOMBRE_VACIO);
        }
        if (precio == null || precio <= 0) {
            throw new ProductoException(Constants.ERROR_PRECIO_INVALIDO);
        }
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new ProductoException(Constants.ERROR_DESCRIPCION_VACIA);
        }
        if (categorias == null || categorias.isEmpty()) {
            throw new ProductoException(Constants.ERROR_CATEGORIAS_VACIAS);
        }

         */



    }
}
