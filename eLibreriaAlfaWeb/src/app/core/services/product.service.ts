import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BaseHttpService } from './base-http.service';
import { ProductoRequestDto, ProductoSimpleDto, ProductoConImagenesDto } from '../models/producto';

@Injectable({
  providedIn: 'root'
})
export class ProductService extends BaseHttpService<any, any> {

  constructor(http: HttpClient) {
    super(http, '/product');
  }

  getCatalogFiltered(category: number, searchText: string | null, order: string | null): Observable<ProductoSimpleDto[]> {
    return this.http.get<ProductoSimpleDto[]>(`${this.baseUrl}${this.end}/filtered?categoria=${category}&textoBusqueda=${searchText}&orden=${order}`);
  }

  getProductoConImagenesById(id: number): Observable<ProductoConImagenesDto> {
    return this.http.get<ProductoConImagenesDto>(`${this.baseUrl}${this.end}/${id}/with-images`);
  }

}
