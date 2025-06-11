import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { BaseHttpService } from './base-http.service';
import { PublicacionSimple, Publicacion, AgregarPublicacion, AgregarComentario } from '../models/publicacion';
import { Comentario } from '../models/publicacion';

@Injectable({
  providedIn: 'root'
})
export class PublicationService extends BaseHttpService<AgregarPublicacion, PublicacionSimple> {

  private apiUrl = '/publication';

  constructor(http: HttpClient) {
    super(http, '/publication');
  }

  createComment(comment: AgregarComentario): Observable<string> {
    return this.http.post<string>(this.baseUrl + this.apiUrl + '/comment', comment, { responseType: 'text' as 'json' }).pipe(
        tap(response => console.log('Respuesta del backend:', response))
    );
  }

  deleteComment(commentId: number): Observable<void> {
    return this.http.delete<void>(this.baseUrl + this.apiUrl + `/comment/${commentId}`, { withCredentials: true }).pipe(
      tap(() => console.log('Comentario eliminado:', commentId))
    );
  }

  getPublicationsPageByDate(pagina: number, cantidad: number): Observable<any[]> {
    return this.http.get<any[]>(this.baseUrl + this.apiUrl + '/page/date' + `?pagina=${pagina}&cantidad=${cantidad}`);
  }

  // Métodos para gestión de publicaciones
  
  createPublication(publicacion: AgregarPublicacion): Observable<string> {
    return this.http.post(this.baseUrl + this.apiUrl, publicacion, { 
      withCredentials: true, 
      responseType: 'text' 
    }).pipe(
      tap(response => console.log('Publicación creada:', response))
    );
  }

  deletePublication(publicacionId: number): Observable<string> {
    return this.http.delete(this.baseUrl + this.apiUrl + `/${publicacionId}`, { 
      withCredentials: true, 
      responseType: 'text' 
    }).pipe(
      tap(response => console.log('Publicación eliminada:', response))
    );
  }

  updatePublication(publicacionId: number, publicacion: AgregarPublicacion): Observable<string> {
    return this.http.put(this.baseUrl + this.apiUrl + `/${publicacionId}`, publicacion, { 
      withCredentials: true, 
      responseType: 'text' 
    }).pipe(
      tap(response => console.log('Publicación actualizada:', response))
    );
  }
}
