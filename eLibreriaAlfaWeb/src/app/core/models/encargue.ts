import { ProductoEncargue } from "./producto-encargue";

export enum EncargueEstado {
    EN_CREACION = 'EN_CREACION',
    ENVIADO = 'ENVIADO',
    CANCELADO = 'CANCELADO',
    CONCRETADO = 'CONCRETADO'
}

export interface Encargue {
    id: number;
    total: number;
    productos: ProductoEncargue[];
    IdUsuarioComprado: number;
    estado: EncargueEstado;
    fecha: Date;
}