import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BaseHttpService } from './base-http.service';
import { CategoriaRequestDto, CategoriaNodoDto } from '../models/categoria';

@Injectable({
  providedIn: 'root'
})
export class CategoryService extends BaseHttpService<CategoriaRequestDto, any> {

  constructor(http: HttpClient) {
    super(http, '/category');
  }

  getAllCategoriesTree(): Observable<CategoriaNodoDto[]> {
    return this.http.get<CategoriaNodoDto[]>(`${this.baseUrl}${this.end}`);
  }
}
