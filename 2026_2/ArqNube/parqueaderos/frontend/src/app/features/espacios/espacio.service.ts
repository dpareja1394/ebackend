import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Pagina } from '../../core/models/api.models';
import { Espacio, EspacioRequest, EspacioUpdateRequest, FiltrosEspacio, Resumen } from './espacio.model';

@Injectable({ providedIn: 'root' })
export class EspacioService {
  private readonly http = inject(HttpClient);
  private readonly api = environment.apiBaseUrl;

  listar(parqueaderoId: string, filtros: FiltrosEspacio = {}, pagina = 0, tamano = 20): Observable<Pagina<Espacio>> {
    let params = new HttpParams().set('pagina', pagina).set('tamano', tamano);
    if (filtros.estado) {
      params = params.set('estado', filtros.estado);
    }
    if (filtros.tipoVehiculo) {
      params = params.set('tipoVehiculo', filtros.tipoVehiculo);
    }
    return this.http.get<Pagina<Espacio>>(`${this.api}/parqueaderos/${parqueaderoId}/espacios`, { params });
  }

  crear(parqueaderoId: string, req: EspacioRequest): Observable<Espacio> {
    return this.http.post<Espacio>(`${this.api}/parqueaderos/${parqueaderoId}/espacios`, req);
  }

  actualizar(id: string, req: EspacioUpdateRequest): Observable<Espacio> {
    return this.http.put<Espacio>(`${this.api}/espacios/${id}`, req);
  }

  eliminar(id: string): Observable<void> {
    return this.http.delete<void>(`${this.api}/espacios/${id}`);
  }

  /** Libera un espacio huérfano; el servidor responde 409 si algún vehículo sí lo tiene asignado. */
  forzarLiberacion(id: string): Observable<Espacio> {
    return this.http.delete<Espacio>(`${this.api}/espacios/${id}/ocupacion`);
  }

  resumen(parqueaderoId: string): Observable<Resumen> {
    return this.http.get<Resumen>(`${this.api}/parqueaderos/${parqueaderoId}/resumen`);
  }
}
