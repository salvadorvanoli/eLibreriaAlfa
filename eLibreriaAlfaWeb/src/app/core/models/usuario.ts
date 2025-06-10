import { Rol } from "./rol";

export interface Usuario {
    id?: string;
    email: string;
    contrasenia: string;
    rol: Rol;
    telefono: string;
    nombre: string;
    apellido: string;
}

export interface UsuarioSimple {
    id: number;
    email: string;
    rol: Rol;
    nombre: string;
    apellido: string;
    telefono: string;
}

export interface AccesoUsuario {
    email: string;
    contrasenia: string;
}

export interface ModificarPerfilUsuario {
    nombre: string;
    apellido: string;
    telefono: string;
}
