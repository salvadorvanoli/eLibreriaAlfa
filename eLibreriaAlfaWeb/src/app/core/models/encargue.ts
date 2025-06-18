import { ProductoEncargue } from "./producto-encargue";

export interface Encargue {
    id: number;
    total: number;
    productos: ProductoEncargue[];
    IdUsuarioComprado: number;
}