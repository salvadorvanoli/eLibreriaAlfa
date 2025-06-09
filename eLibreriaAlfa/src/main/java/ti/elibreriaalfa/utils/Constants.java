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

    private Constants() {}
}
