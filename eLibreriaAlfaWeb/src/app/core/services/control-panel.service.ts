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
        return this.userService.getElements();
      case 'Categoría':
        return this.categoryService.getElements();
      case 'Producto':
        return this.productService.getElements();
      case 'Publicación':
        return this.publicationService.getElements();
      //case 'Pedido':
        //return this.orderService.getElements();
      default:
        return null;
    }
  }

  getDataByTypeFiltered(type: string, searchText: string, order: string): Observable<any[]> | null {
    switch(type) {
      case 'Usuario':
        return this.userService.getFiltered(searchText, order);
      case 'Categoría':
        return this.categoryService.getFiltered(searchText, order);
      case 'Producto':
        return this.productService.getFiltered(searchText, order);
      case 'Publicación':
        return this.publicationService.getFiltered(searchText, order);
      //case 'Pedido':
        //return this.orderService.getFiltered(searchText, order);
      default:
        return null;
    }
  }

  getElementByTypeAndId(type: string, id: any): Observable<any> | null {
    switch(type) {
      case 'Usuario':
        return this.userService.getUserByEmail(id);
      case 'Categoría':
        return this.categoryService.getById(id);
      case 'Producto':
        return this.productService.getProductoConImagenesById(id);
      case 'Publicación':
        return this.publicationService.getById(id);
      //case 'Pedido':
        //return this.orderService.getById(id);
      default:
        return null;
    }
  }
}
