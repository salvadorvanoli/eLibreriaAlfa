import { ProductoSimple } from './producto';

export interface ProductoEncargue {
  id: number;  
  cantidad: number;  
  producto: ProductoSimple;  
  encargueId: number;
}