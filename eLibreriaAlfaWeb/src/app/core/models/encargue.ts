import { ProductoEncargue } from "./producto-encargue";
import { Producto } from "./producto";

export interface Encargue {
    id: number;
    total: number;
    productos: ProductoEncargue[];
    IdUsuarioComprado: number;
}