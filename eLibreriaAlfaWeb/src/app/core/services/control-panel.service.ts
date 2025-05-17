import { Injectable } from '@angular/core';
import { UserService } from './user.service';
import { CategoryService } from './category.service';
import { ProductService } from './product.service';
import { PublicationService } from './publication.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ControlPanelService {

  constructor(
    private userService: UserService,
    private categoryService: CategoryService,
    private productService: ProductService,
    private publicationService: PublicationService,
    //private orderService: OrderService
  ) {}

  getDataByType(type: string): Observable<any[]> | null {
    switch(type) {
      case 'Usuario':
        return this.userService.getAll();
      case 'Categoria':
        return this.categoryService.getAll();
      case 'Producto':
        return this.productService.getAll();
      case 'Publicacion':
        return this.publicationService.getAll();
      //case 'Pedido':
        //return this.orderService.getAll();
      default:
        return null;
    }
  }
}
