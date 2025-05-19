import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BaseHttpService } from './base-http.service';
import { UsuarioSimple, AccesoUsuario, ModificarPerfilUsuario } from '../models/usuario';

@Injectable({
  providedIn: 'root'
})
export class UserService extends BaseHttpService<AccesoUsuario, UsuarioSimple> {

  constructor(http: HttpClient) {
    super(http, '/user');
  }

  override getAll(): Observable<UsuarioSimple[]> {
    return this.http.get<UsuarioSimple[]>(`${this.baseUrl}${this.end}`, { withCredentials: true });
  }

  override getById(id: number): Observable<UsuarioSimple> {
    return this.http.get<UsuarioSimple>(`${this.baseUrl}${this.end}/${id}`, { withCredentials: true });
  }

  override post(user: AccesoUsuario): Observable<UsuarioSimple> {
    return this.http.post<UsuarioSimple>(`${this.baseUrl}${this.end}`, user);
  }
  
  // Método para actualizar el perfil del usuario
  updateProfile(email: string, profileData: ModificarPerfilUsuario): Observable<UsuarioSimple> {
    return this.http.patch<UsuarioSimple>(
      `${this.baseUrl}${this.end}/${email}/profile`, 
      profileData, 
      { withCredentials: true }
    );
  }
}
