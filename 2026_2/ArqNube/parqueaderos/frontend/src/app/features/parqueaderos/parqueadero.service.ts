import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Pagina } from '../../core/models/api.models';
import {
  FiltrosParqueadero,
  Parqueadero,
  ParqueaderoRequest,
  ParqueaderoUpdateRequest,
} from './parqueadero.model';

@Injectable({ providedIn: 'root' })
export class ParqueaderoService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiBaseUrl}/parqueaderos`;

  listar(filtros: FiltrosParqueadero = {}, pagina = 0, tamano = 20): Observable<Pagina<Parqueadero>> {
    let params = new HttpParams().set('pagina', pagina).set('tamano', tamano);
    if (filtros.ciudad?.trim()) {
      params = params.set('ciudad', filtros.ciudad.trim());
    }
    if (filtros.estado) {
      params = params.set('estado', filtros.estado);
    }
    return this.http.get<Pagina<Parqueadero>>(this.url, { params });
  }

  obtener(id: string): Observable<Parqueadero> {
    return this.http.get<Parqueadero>(`${this.url}/${id}`);
  }

  crear(req: ParqueaderoRequest): Observable<Parqueadero> {
    return this.http.post<Parqueadero>(this.url, req);
  }

  actualizar(id: string, req: ParqueaderoUpdateRequest): Observable<Parqueadero> {
    return this.http.put<Parqueadero>(`${this.url}/${id}`, req);
  }

  eliminar(id: string): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
