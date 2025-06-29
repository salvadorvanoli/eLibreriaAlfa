import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BaseHttpService } from './base-http.service';
import { ProductoSimpleDto } from '../models/producto';

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

  enable(id: number): Observable<ProductoSimpleDto> {
    return this.http.patch<ProductoSimpleDto>(
      `${this.baseUrl}${this.end}/enable/${id}`, 
      {}, 
      { withCredentials: true }
    );
  }

  disable(id: number): Observable<ProductoSimpleDto> {
    return this.http.patch<ProductoSimpleDto>(
      `${this.baseUrl}${this.end}/disable/${id}`, 
      {}, 
      { withCredentials: true }
    );
  }

}
