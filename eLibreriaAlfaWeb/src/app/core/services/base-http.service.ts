import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export abstract class BaseHttpService<TRequest, TResponse> {

  protected baseUrl = 'http://localhost:8080/elibreriaalfa';

  constructor(
    protected http: HttpClient,
    protected end: string
  ) {}

  getAll(): Observable<TResponse[]> {
    return this.http.get<TResponse[]>(`${this.baseUrl}${this.end}`);
  }

  getElements(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}${this.end}/elements`, { withCredentials: true });
  }

  getFiltered(searchText: string, order: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}${this.end}/filteredElements?textoBusqueda=${searchText}&orden=${order}`, { withCredentials: true });
  }

  getPaginated(page: number, size: number): Observable<TResponse[]> {
    return this.http.get<TResponse[]>(`${this.baseUrl}${this.end}/paginado?pagina=${page}&cantidad=${size}`);
  }

  getById(id: number): Observable<TResponse> {
    return this.http.get<TResponse>(`${this.baseUrl}${this.end}/${id}`);
  }

  getByIdWithImages(id: number): Observable<TResponse> {
    return this.http.get<TResponse>(`${this.baseUrl}${this.end}/${id}/with-images`);
  }

  post(item: TRequest): Observable<TResponse> {
    return this.http.post<TResponse>(`${this.baseUrl}${this.end}`, item, { withCredentials: true });
  }

  put(id: any, item: TRequest): Observable<TResponse> {
    return this.http.put<TResponse>(`${this.baseUrl}${this.end}/${id}`, item, { withCredentials: true });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}${this.end}/${id}`, { withCredentials: true });
  }
}
