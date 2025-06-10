import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BaseHttpService } from './base-http.service';
import { Producto, ProductoSimple } from '../models/producto';

@Injectable({
  providedIn: 'root'
})
export class ProductService extends BaseHttpService<Producto, Producto> {

  constructor(http: HttpClient) {
    super(http, '/product');
  }

  getCatalogFiltered(category: number, searchText: string | null, order: string | null): Observable<Producto[]> {
    return this.http.get<ProductoSimple[]>(`${this.baseUrl}${this.end}/filtered?categoria=${category}&textoBusqueda=${searchText}&orden=${order}`);
  }

}
