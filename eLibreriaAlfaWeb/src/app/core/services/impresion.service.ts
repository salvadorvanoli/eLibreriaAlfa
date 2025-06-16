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
    console.log('=== CREANDO IMPRESIÓN CON ARCHIVO ===');
    console.log('Request data:', request);
    console.log('Archivo:', {
      name: archivo.name,
      size: archivo.size,
      type: archivo.type
    });
    
    console.log('PASO 1: Subiendo archivo...');
    return this.uploadFile(archivo).pipe(
      tap(uploadResponse => {
        console.log('✅ PASO 1 completado - Archivo subido:', uploadResponse);
      }),
      switchMap(uploadResponse => {
        console.log('PASO 2: Creando impresión con archivo...');
        
        const requestConArchivo = {
          ...request,
          nombreArchivo: uploadResponse.filename
        };
        
        console.log('Request con archivo:', requestConArchivo);
        
        return this.http.post(`${this.baseUrl}${this.end}`, requestConArchivo, { responseType: 'text' }).pipe(
          tap(response => {
            console.log('✅ PASO 2 completado - Impresión creada:', response);
            this.impresionCreatedSubject.next();
          })
        );
      }),
      catchError(error => {
        console.error('❌ Error en el proceso:', error);
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
    
    console.log('Enviando POST a:', `${this.baseUrl}/print/upload`);
    
    return this.http.post(`${this.baseUrl}/print/upload`, formData).pipe(
      tap(response => {
        console.log('✅ Archivo subido exitosamente:', response);
      }),
      catchError(error => {
        console.error('❌ Error al subir archivo:', error);
        throw error;
      })
    );
  }
  
  downloadFile(filename: string): Observable<Blob> {
    console.log('=== DESCARGANDO ARCHIVO ===');
    console.log('Filename:', filename);
    
    const url = `${this.baseUrl}/print/download/${filename}`;
    console.log('URL:', url);
    
    return this.http.get(url, { responseType: 'blob' }).pipe(
      tap(blob => {
        console.log('✅ Archivo descargado exitosamente');
        console.log('Tamaño:', blob.size, 'bytes');
      }),
      catchError(error => {
        console.error('❌ Error al descargar archivo:', error);
        throw error;
      })
    );
  }
}
