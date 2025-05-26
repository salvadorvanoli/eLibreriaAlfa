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

  getPublicationsPageByDate(pagina: number, cantidad: number): Observable<any[]> {
    return this.http.get<any[]>(this.baseUrl + this.apiUrl + '/page/date' + `?pagina=${pagina}&cantidad=${cantidad}`);
  }
}
