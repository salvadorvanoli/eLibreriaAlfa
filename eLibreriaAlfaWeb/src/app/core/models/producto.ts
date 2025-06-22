import { CategoriaSimpleDto } from './categoria';
import { ImageDto } from './image';

export interface ProductoDto {
    id: number;
    nombre: string;
    precio: number;
    descripcion: string;
    imagenes: string[];
    categorias: CategoriaSimpleDto[];
}

export interface ProductoSimpleDto {
    id: number;
    nombre: string;
    precio: number;
    descripcion: string;
    imagenes: string[];
}

export interface ProductoConImagenesDto {
    id: number;
    nombre: string;
    precio: number;
    descripcion: string;
    imagenes: ImageDto[];
    categorias: CategoriaSimpleDto[];
}

export interface ProductoRequestDto {
    nombre: string;
    precio: number;
    descripcion: string;
    imagenes: File[];
    imagenesAEliminar: string[] | null;
    categoriasIds: number[];
}
