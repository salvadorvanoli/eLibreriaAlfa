import { ProductoSimpleDto } from './producto';

export interface ProductoEncargue {
  id: number;  
  cantidad: number;  
  producto: ProductoSimpleDto;  
  encargueId: number;
}