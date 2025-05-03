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
}
