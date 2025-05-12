package ti.elibreriaalfa.business.entities;

import java.util.Arrays;

public enum Rol {
    ADMINISTRADOR,
    EMPLEADO,
    CLIENTE;

    public static Rol rolFromString(String rol) {
        if (rol == null) return null;
        try {
            return Rol.valueOf(rol);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static String rolToString(Rol rol) {
        return (rol != null) ? rol.name() : null;
    }

    public static Boolean isValidRole(String rol) {
        return Arrays.stream(Rol.values()).anyMatch(rolValue -> rolValue.name().equalsIgnoreCase(rol));
    }

    public static String toSpringSecurityRol(Rol rol) {
        return (rol != null) ? "ROLE_" + rol.name() : null;
    }
}
