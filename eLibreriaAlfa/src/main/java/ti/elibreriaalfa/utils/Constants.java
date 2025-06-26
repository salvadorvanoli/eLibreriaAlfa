package ti.elibreriaalfa.utils;

public class Constants {

    // Constantes de usuario
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    public static final String TELEFONO_REGEX = "^(09[0-9]{7}|9[0-9]{7})$";
    public static final int MIN_CONTRASENIA_LENGTH = 6;
    public static final int MIN_NOMBRE_USUARIO_LENGTH = 1;
    public static final int MAX_NOMBRE_USUARIO_LENGTH = 40;
    public static final float ENCARGUE_INICIAL_TOTAL = 0f;

    // Mensajes de error para validaciones de usuario
    public static final String ERROR_EMAIL_USUARIO_INVALIDO = "El correo electrónico no es válido";
    public static final String ERROR_EMAIL_USUARIO_YA_EXISTE = "El correo electrónico ya está registrado";
    public static final String ERROR_USUARIO_NO_ENCONTRADO_EMAIL = "No existe un usuario con ese correo electrónico";
    public static final String ERROR_USUARIO_NO_ENCONTRADO_ID = "No existe un usuario con ese ID";
    public static final String ERROR_CONTRASENIA_USUARIO_INVALIDA = "La contraseña debe tener al menos 6 caracteres";
    public static final String ERROR_TELEFONO_USUARIO_INVALIDO = "El número de teléfono no es válido";
    public static final String ERROR_ROL_USUARIO_INVALIDO = "El rol no es válido";

    // Constantes de publicación
    public static final int MIN_TITULO_PUBLICACION_LENGTH = 1;
    public static final int MAX_TITULO_PUBLICACION_LENGTH = 200;
    public static final int MIN_CONTENIDO_PUBLICACION_LENGTH = 1;
    public static final int MAX_CONTENIDO_PUBLICACION_LENGTH = 200;
    public static final int MIN_COMENTARIO_PUBLICACION_LENGTH = 1;
    public static final int MAX_COMENTARIO_PUBLICACION_LENGTH = 200;

    // Mensajes de error para validaciones de publicación
    public static final String ERROR_PUBLICACION_NO_ENCONTRADA_ID = "No se encontró la publicación especificada";
    public static final String ERROR_TITULO_PUBLICACION_INVALIDO = "El título debe tener entre 1 y 200 caracteres";
    public static final String ERROR_CONTENIDO_PUBLICACION_INVALIDO = "El contenido debe tener entre 1 y 200 caracteres";
    public static final String ERROR_IMAGEN_PUBLICACION_NULA = "La publicación debe tener una imagen válida asociada";
    public static final String ERROR_PUBLICACION_SIN_PERMISOS = "No tiene permisos para realizar esta operación en la publicación";
    
    // Mensajes de error para validaciones de comentario
    public static final String ERROR_COMENTARIO_NO_ENCONTRADO = "No se encontró el comentario especificado";
    public static final String ERROR_COMENTARIO_TEXTO_VACIO = "El texto del comentario no puede estar vacío";
    public static final String ERROR_COMENTARIO_LENGTH = "El comentario debe tener entre 1 y 200 caracteres";
    public static final String ERROR_COMENTARIO_SIN_USUARIO = "El comentario debe tener un usuario asociado";
    public static final String ERROR_COMENTARIO_SIN_PUBLICACION = "El comentario debe estar asociado a una publicación";
    public static final String ERROR_COMENTARIO_SIN_PERMISOS = "No tiene permisos para realizar esta operación en el comentario";

    // Mensajes de éxito
    public static final String SUCCESS_PUBLICACION_CREADA = "Publicación creada exitosamente con ID: ";
    public static final String SUCCESS_PUBLICACION_ACTUALIZADA = "Publicación actualizada exitosamente";
    public static final String SUCCESS_PUBLICACION_ELIMINADA = "Publicación eliminada exitosamente";
    public static final String SUCCESS_COMENTARIO_CREADO = "Comentario creado exitosamente con ID: ";
    public static final String SUCCESS_COMENTARIO_ELIMINADO = "Comentario eliminado exitosamente";

    // Constantes de producto
    public static final int MIN_NOMBRE_PRODUCTO_LENGTH = 1;
    public static final int MAX_NOMBRE_PRODUCTO_LENGTH = 50;
    public static final int MIN_PRECIO_PRODUCTO = 1;
    public static final int MIN_DESCRIPCION_PRODUCTO_LENGTH = 1;
    public static final int MAX_DESCRIPCION_PRODUCTO_LENGTH = 500;
    public static final int MIN_CATEGORIAS_PRODUCTO = 1;

    // Mensajes de error para validaciones de producto
    public static final String ERROR_NOMBRE_PRODUCTO_INVALIDO = "El nombre debe tener entre " + MIN_NOMBRE_PRODUCTO_LENGTH + " y " + MAX_NOMBRE_PRODUCTO_LENGTH + " caracteres";
    public static final String ERROR_NOMBRE_PRODUCTO_YA_EXISTE = "Ya existe un producto con ese nombre";
    public static final String ERROR_PRECIO_PRODUCTO_INVALIDO = "El precio debe ser de al menos " + MIN_PRECIO_PRODUCTO;
    public static final String ERROR_DESCRIPCION_PRODUCTO_INVALIDA = "La descripción debe tener entre " + MIN_DESCRIPCION_PRODUCTO_LENGTH + " y " + MAX_DESCRIPCION_PRODUCTO_LENGTH + " caracteres";
    public static final String ERROR_CATEGORIAS_PRODUCTO_INVALIDAS = "El producto debe tener al menos una categoría asociada";
    public static final String ERROR_IMAGENES_PRODUCTO_VACIAS = "El producto debe tener al menos una imagen asociada";
    public static final String ERROR_PRODUCTO_NO_ENCONTRADO_ID = "No existe un producto con ese ID";

    // Constantes de categoría
    public static final int MIN_NOMBRE_CATEGORIA_LENGTH = 1;
    public static final int MAX_NOMBRE_CATEGORIA_LENGTH = 50;

    // Mensajes de error para validaciones de categoría
    public static final String ERROR_NOMBRE_CATEGORIA_INVALIDO = "El nombre debe tener entre " + MIN_NOMBRE_CATEGORIA_LENGTH + " y " + MAX_NOMBRE_CATEGORIA_LENGTH + " caracteres";
    public static final String ERROR_NOMBRE_CATEGORIA_YA_EXISTE = "Ya existe una categoría con ese nombre";
    public static final String ERROR_CATEGORIA_NO_ENCONTRADA = "No existe una categoría con ese ID";

    // Constantes de imagen
    public static final String IMAGEN_PRODUCTO_CARPETA = "productos";
    public static final String IMAGEN_PUBLICACION_CARPETA = "publicaciones";
    public static final Integer MAX_TAMANO_IMAGEN = 5 * 1024 * 1024; // 5 MB

    // Mensajes de error para validaciones de imagen
    public static final String ERROR_IMAGEN_TAMANO_INVALIDO = "El tamaño de la imagen debe ser como máximo de 5 MB";
    public static final String ERROR_IMAGEN_FORMATO_INVALIDO = "El formato de la imagen no es válido. Debe ser PNG, JPG, JPEG o WEBP";



    private Constants() {}
}
