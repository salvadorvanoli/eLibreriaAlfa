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

  getById(id: number): Observable<TResponse> {
    return this.http.get<TResponse>(`${this.baseUrl}${this.end}/${id}`);
  }

  post(item: TRequest): Observable<TResponse> {
    return this.http.post<TResponse>(`${this.baseUrl}${this.end}`, item);
  }

  put(id: number, item: TResponse): Observable<TResponse> {
    return this.http.put<TResponse>(`${this.baseUrl}${this.end}/${id}`, item);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}${this.end}/${id}`);
  }
}
