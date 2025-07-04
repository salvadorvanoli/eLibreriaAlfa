import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseHttpService } from './base-http.service';
import { Encargue } from '../models/encargue';
import { ProductoEncargue } from '../models/producto-encargue';


export enum EncargueEstado {
  EN_CREACION = 'EN_CREACION',
  PENDIENTE = 'PENDIENTE',
  COMPLETADO = 'COMPLETADO',
  CANCELADO = 'CANCELADO',
  ENTREGADO = 'ENTREGADO' 
}


interface PageResponse<T> {
  content: T[];
  pageable: any;
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class OrderService extends BaseHttpService<Encargue, Encargue> {
  
  constructor(http: HttpClient) {
    super(http, '/order');
  }

  usuarioTieneEncargueEnCreacion(usuarioId: number): Observable<boolean> {
    return this.http.get<boolean>(
      `${this.baseUrl}${this.end}/usuario/${usuarioId}/tiene-en-creacion`,
      { withCredentials: true }
    );
  }

  listarProductosEncarguePorUsuarioYEstado(
    usuarioId: number, 
    estado: EncargueEstado, 
    pagina: number = 0,
    cantidad: number = 10
  ): Observable<PageResponse<ProductoEncargue>> {
    const params = new HttpParams()
      .set('pagina', pagina.toString())
      .set('cantidad', cantidad.toString());

    return this.http.get<PageResponse<ProductoEncargue>>(
      `${this.baseUrl}${this.end}/usuario/${usuarioId}/estado/${estado}/productos`, 
      { params, withCredentials: true }
    );
  }


  eliminarProductoDeEncargue(encargueId: number, productoEncargueId: number): Observable<void> {
    
    return this.http.delete<void>(
      `${this.baseUrl}${this.end}/${encargueId}/producto/${productoEncargueId}`,
      { withCredentials: true }
    );
  }

  agregarProductoAEncargue(usuarioId: number, producto: any): Observable<void> {
    return this.http.post<void>(
      `${this.baseUrl}${this.end}/usuario/${usuarioId}/producto`,
      producto,
      { withCredentials: true }
    );
  }

  obtenerEncargueEnCreacion(): Observable<Encargue> {
    return this.http.get<Encargue>(
      `${this.baseUrl}${this.end}/en-creacion`,
      { withCredentials: true }
    );
  }

  marcarEncargueComoEnviado(encargueId: number, fecha: string): Observable<void> {
    return this.http.post<void>(
      `${this.baseUrl}${this.end}/${encargueId}/enviar`,
      { fecha },
      { withCredentials: true }
    );
  }

  cancelarEncargueEnviado(usuarioId: number): Observable<void> {
    return this.http.post<void>(
      `${this.baseUrl}${this.end}/usuario/${usuarioId}/cancelar-enviado`,
      {},
      { withCredentials: true }
    );
  }

  listarEncarguesFinalizadosPorUsuario(usuarioId: number): Observable<Encargue[]> {
    const url = `${this.baseUrl}${this.end}/user/${usuarioId}/history`;
    return this.http.get<Encargue[]>(
      url,
      { withCredentials: true }
    );
  }

  getEncargues(): Observable<any> {
    return this.http.get<any>(
      `${this.baseUrl}${this.end}`,
      { withCredentials: true }
    );
  }

  cambiarEstadoEncargue(idEncargue: number, nuevoEstado: string): Observable<string> {
    return this.http.patch<string>(
      `${this.baseUrl}${this.end}/${idEncargue}/estado`,
      { estado: nuevoEstado },
      { withCredentials: true, responseType: 'text' as 'json' }
    );
  }
}