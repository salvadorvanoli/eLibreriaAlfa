import { UsuarioSimple } from './usuario';

export interface Impresion {
    id: number;
    color: boolean;
    comentarioAdicional: string;
    usuarioSimple: UsuarioSimple;
    estado: string; 
    nombreArchivo: string;
}

export interface ImpresionRequest {
    color: boolean;
    comentarioAdicional: string;
    usuario: UsuarioSimple; 
    nombreArchivo: string;
}