import { ProductoSimpleDto } from "./producto";

export interface CategoriaDto {
    id: number;
    nombre: string;
    padre: CategoriaSimpleDto;
    productos: ProductoSimpleDto[];
}

export interface CategoriaSimpleDto {
    id: number;
    nombre: string;
    padreId?: number;
}

export interface CategoriaNodoDto {
    id: number;
    nombre: string;
    hijos: CategoriaNodoDto[];
}

export interface CategoriaRequestDto {
    nombre: string;
    padreId?: number;
}
