export interface Producto {
    id: number;
    nombre: string;
    precio: number;
    imagenes: string[];
    descripcion: string;
}

export interface ProductoSimple {
    id: number; 
    nombre: string;
    precio: number; 
    descripcion: string;
    imagenes: string[]; 
}