import { Injectable } from '@angular/core';
import { AccesoUsuario, UsuarioSimple } from '../models/usuario';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class SeguridadService {

  private apiUrl = 'http://localhost:8080/elibreriaalfa/security';

  constructor(private http: HttpClient) { }

  auth(usuario: AccesoUsuario): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/auth`, usuario);
  }
}
