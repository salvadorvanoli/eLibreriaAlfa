import { UsuarioSimple } from './usuario';

export interface Impresion {
    id: number;
    color: boolean;
    comentarioAdicional: string;
    usuarioSimple?: UsuarioSimple;
    usuario?: {                   
        id: number;
        email: string;
        nombre: string;
        apellido: string;
    };
    estado: string; 
    nombreArchivo: string;
    formato: string;
    tipoPapel: string;
    dobleCara: boolean;
    orientacion: string;
}

export interface ImpresionRequest {
    color: boolean;
    comentarioAdicional: string;
    usuario: UsuarioSimple;
    nombreArchivo: string;
    formato: string; 
    tipoPapel: string; 
    dobleCara: boolean;
    orientacion: string; 
}