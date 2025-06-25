import { Injectable } from '@angular/core';
import { BaseHttpService } from './base-http.service';
import { HttpClient } from '@angular/common/http';
import { Impresion, ImpresionRequest } from '../models/impresion';
import { Observable, Subject, tap, switchMap, catchError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ImpresionService extends BaseHttpService<ImpresionRequest, Impresion> {
  
  private impresionCreatedSubject = new Subject<void>();
  public impresionCreated$ = this.impresionCreatedSubject.asObservable();

  constructor(http: HttpClient) {
    super(http, '/print');
  }

  createPrintRequest(request: ImpresionRequest, archivo: File): Observable<string> {
    
    return this.uploadFile(archivo).pipe(
      tap(uploadResponse => {

      }),
      switchMap(uploadResponse => {
        
        const requestConArchivo = {
          ...request,
          nombreArchivo: uploadResponse.filename
        };
        
        return this.http.post(`${this.baseUrl}${this.end}`, requestConArchivo, { responseType: 'text' }).pipe(
          tap(response => {
            this.impresionCreatedSubject.next();
          })
        );
      }),
      catchError(error => {
        if (error.url && error.url.includes('/upload')) {
          console.error('❌ Falló en PASO 1 (subir archivo)');
        } else {
          console.error('❌ Falló en PASO 2 (crear impresión)');
        }
        throw error;
      })
    );
  }

  getImpresionesByUsuario(usuarioId: number): Observable<Impresion[]> {
    return this.http.get<Impresion[]>(`${this.baseUrl}${this.end}/user/${usuarioId}`);
  }

  cambiarEstadoImpresion(idImpresion: number, nuevoEstado: string): Observable<string> {
    const body = { estado: nuevoEstado };
    return this.http.patch(`${this.baseUrl}${this.end}/${idImpresion}/estado`, body, {
      responseType: 'text'
    }).pipe(
      tap(() => {
        this.impresionCreatedSubject.next();
      })
    );
  }

  getAllImpresiones(): Observable<any> {
    return this.http.get(`${this.baseUrl}${this.end}`);
  }
  
  uploadFile(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
        
    return this.http.post(`${this.baseUrl}/print/upload`, formData).pipe(
      tap(response => {
      }),
      catchError(error => {
        console.error('❌ Error al subir archivo:', error);
        throw error;
      })
    );
  }
  
  downloadFile(filename: string): Observable<Blob> {
    
    const url = `${this.baseUrl}/print/download/${filename}`;
    
    return this.http.get(url, { responseType: 'blob' }).pipe(
      tap(blob => {
      }),
      catchError(error => {
        console.error('❌ Error al descargar archivo:', error);
        throw error;
      })
    );
  }
}
