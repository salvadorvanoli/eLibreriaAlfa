import { UsuarioSimple } from "./usuario";

export interface PublicacionSimple {
    id: number;
    titulo: string;
    contenido: string;
    fechaCreacion: Date;
}

export interface Publicacion extends PublicacionSimple {
    comentarios?: Comentario[];
    imagenUrl?: string;
}

export interface Comentario {
    id: number;
    publicacion: number;
    texto: string;
    titulo: string;
    fechaCreacion: Date;
    usuario: UsuarioSimple;
}

export interface AgregarComentario {
    fechaCreacion: string;
    usuario: {
        id: number;
    };
    titulo: string;
    texto: string;
    publicacion: {
        id: number;
    };
}

export interface AgregarPublicacion {
    titulo: string;
    contenido: string;
    fechaCreacion: Date;
    comentarios: any[];
}