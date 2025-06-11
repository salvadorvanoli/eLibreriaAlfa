package ti.elibreriaalfa.utils;

public class Constants {

    // Constantes de usuario
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    public static final String TELEFONO_REGEX = "^(09[0-9]{7}|9[0-9]{7})$";
    public static final int MIN_CONTRASENIA_LENGTH = 6;
    public static final int MIN_NOMBRE_LENGTH = 1;
    public static final int MAX_NOMBRE_LENGTH = 40;
    public static final float ENCARGUE_INICIAL_TOTAL = 0f;

    // Mensajes de error para validaciones de usuario
    public static final String ERROR_EMAIL_VACIO = "El correo electrónico no puede estar vacío";
    public static final String ERROR_EMAIL_FORMATO = "El formato del correo electrónico no es válido";
    public static final String ERROR_EMAIL_YA_EXISTE = "El correo electrónico ya está registrado";
    public static final String ERROR_USUARIO_NO_ENCONTRADO_EMAIL = "No existe un usuario con ese correo electrónico";
    public static final String ERROR_USUARIO_NO_ENCONTRADO_ID = "No existe un usuario con ese ID";
    public static final String ERROR_CONTRASENIA_LENGTH = "La contraseña debe tener al menos 6 caracteres";
    public static final String ERROR_TELEFONO_VACIO = "El número de teléfono no puede estar vacío";
    public static final String ERROR_TELEFONO_FORMATO = "El formato del teléfono no es válido";
    public static final String ERROR_ROL_NULO = "El rol no puede ser nulo";

    // Constantes de publicación
    public static final int MIN_TITULO_LENGTH = 1;
    public static final int MAX_TITULO_LENGTH = 200;
    public static final int MIN_CONTENIDO_LENGTH = 1;
    public static final int MAX_CONTENIDO_LENGTH = 200;
    public static final int MIN_COMENTARIO_LENGTH = 1;
    public static final int MAX_COMENTARIO_LENGTH = 200;

    // Mensajes de error para validaciones de publicación
    public static final String ERROR_PUBLICACION_NO_ENCONTRADA = "No se encontró la publicación especificada";
    public static final String ERROR_TITULO_VACIO = "El título de la publicación no puede estar vacío";
    public static final String ERROR_TITULO_LENGTH = "El título debe tener entre 1 y 200 caracteres";
    public static final String ERROR_CONTENIDO_VACIO = "El contenido de la publicación no puede estar vacío";
    public static final String ERROR_CONTENIDO_LENGTH = "El contenido debe tener entre 1 y 200 caracteres";
    public static final String ERROR_IMAGEN_URL_FORMATO = "La URL de la imagen no tiene un formato válido";
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

    private Constants() {}
}
