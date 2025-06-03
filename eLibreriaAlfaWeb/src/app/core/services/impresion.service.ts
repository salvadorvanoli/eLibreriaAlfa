import { Injectable } from '@angular/core';
import { BaseHttpService } from './base-http.service';
import { HttpClient } from '@angular/common/http';
import { Impresion, ImpresionRequest } from '../models/impresion';
import { Observable, Subject, tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ImpresionService extends BaseHttpService<ImpresionRequest, Impresion> {
  
  private impresionCreatedSubject = new Subject<void>();
  public impresionCreated$ = this.impresionCreatedSubject.asObservable();

  constructor(http: HttpClient) {
    super(http, '/print');
  }

  createPrintRequest(request: ImpresionRequest): Observable<string> {
    return this.http.post(`${this.baseUrl}${this.end}`, request, { 
      responseType: 'text'
    }).pipe(
      tap(() => {
        // Notificar que se creó una nueva impresión
        this.impresionCreatedSubject.next();
      })
    );
  }

  getImpresionesByUsuario(usuarioId: number): Observable<Impresion[]> {
    return this.http.get<Impresion[]>(`${this.baseUrl}${this.end}/user/${usuarioId}`);
  }
}
