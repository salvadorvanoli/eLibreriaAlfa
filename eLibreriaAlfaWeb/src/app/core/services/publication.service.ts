import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { BaseHttpService } from './base-http.service';
import { PublicacionSimple, Publicacion, PublicacionRequestDto, AgregarComentario } from '../models/publicacion';
import { Comentario } from '../models/publicacion';

@Injectable({
  providedIn: 'root'
})
export class PublicationService extends BaseHttpService<any, any> {

  private apiUrl = '/publication';

  constructor(http: HttpClient) {
    super(http, '/publication');
  }

  createComment(comment: AgregarComentario): Observable<string> {
    return this.http.post<string>(this.baseUrl + this.apiUrl + '/comment', comment, { responseType: 'text' as 'json' }).pipe(
        tap()
    );
  }

  deleteComment(commentId: number): Observable<void> {
    return this.http.delete<void>(this.baseUrl + this.apiUrl + `/comment/${commentId}`, { withCredentials: true }).pipe(
      tap()
    );
  }

  getPublicationsPageByDate(pagina: number, cantidad: number): Observable<any[]> {
    return this.http.get<any[]>(this.baseUrl + this.apiUrl + '/page/date' + `?pagina=${pagina}&cantidad=${cantidad}`);
  }


  deletePublication(publicacionId: number): Observable<string> {
    return this.http.delete(this.baseUrl + this.apiUrl + `/${publicacionId}`, { 
      withCredentials: true, 
      responseType: 'text' 
    }).pipe(
      tap(response => console.log('Publicación eliminada:', response))
    );
  }
}
