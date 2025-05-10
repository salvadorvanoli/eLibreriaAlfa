import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BaseHttpService } from './base-http.service';
import { UsuarioSimple, AccesoUsuario, ModificarPerfilUsuario } from '../models/usuario';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService extends BaseHttpService<AccesoUsuario, UsuarioSimple> {

  private apiUrl = '/user';

  constructor(http: HttpClient) {
    super(http, '/user');
  }

  override getAll(): Observable<UsuarioSimple[]> {
    return this.http.get<UsuarioSimple[]>(`${this.baseUrl}${this.apiUrl}`, { withCredentials: true });
  }

  override getById(id: number): Observable<UsuarioSimple> {
    return this.http.get<UsuarioSimple>(`${this.baseUrl}${this.apiUrl}/${id}`, { withCredentials: true });
  }

  override post(user: AccesoUsuario): Observable<UsuarioSimple> {
    return this.http.post<UsuarioSimple>(`${this.baseUrl}${this.apiUrl}`, user);
  }
}
