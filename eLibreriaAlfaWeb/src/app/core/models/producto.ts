import { CategoriaSimpleDto } from './categoria';

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

export interface ProductoRequestDto {
    nombre: string;
    precio: number;
    descripcion: string;
    imagenes: string[];
    categoriasIds: number[];
}
