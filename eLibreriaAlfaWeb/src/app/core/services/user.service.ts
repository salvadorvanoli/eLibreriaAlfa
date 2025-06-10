import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BaseHttpService } from './base-http.service';
import { UsuarioSimple, AccesoUsuario, ModificarPerfilUsuario, Usuario } from '../models/usuario';

@Injectable({
  providedIn: 'root'
})
export class UserService extends BaseHttpService<any, any> {

  constructor(http: HttpClient) {
    super(http, '/user');
  }

  override getAll(): Observable<UsuarioSimple[]> {
    return this.http.get<UsuarioSimple[]>(`${this.baseUrl}${this.end}`, { withCredentials: true });
  }

  override post(user: AccesoUsuario): Observable<UsuarioSimple> {
    return this.http.post<UsuarioSimple>(`${this.baseUrl}${this.end}/register`, user);
  }
  
  getUserByEmail(email: string): Observable<UsuarioSimple | Usuario> {
    return this.http.get<UsuarioSimple | Usuario>(`${this.baseUrl}${this.end}/${email}`, { withCredentials: true });
  }

  create(user: Usuario): Observable<Usuario> {
    return this.http.post<Usuario>(`${this.baseUrl}${this.end}`, user, { withCredentials: true });
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
