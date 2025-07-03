import { ImageDto } from "./image";
import { UsuarioSimple } from "./usuario";

export interface PublicacionSimple {
    id: number;
    titulo: string;
    contenido: string;
    fechaCreacion: Date;
    imagenUrl?: string;
}

export interface Publicacion extends PublicacionSimple {
    comentarios?: Comentario[];
}

export interface PublicacionConImagenDto extends PublicacionSimple {
    imagen: ImageDto;
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

export interface PublicacionRequestDto {
    titulo: string;
    contenido: string;
    imagen: File | null;
}