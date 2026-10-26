import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Pagina } from '../../core/models/api.models';
import { FiltrosVehiculo, RegistroVehiculoRequest, Vehiculo, VehiculoRequest } from './vehiculo.model';

@Injectable({ providedIn: 'root' })
export class VehiculoService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiBaseUrl}/vehiculos`;

  listar(filtros: FiltrosVehiculo, pagina = 0, tamano = 20): Observable<Pagina<Vehiculo>> {
    let params = new HttpParams().set('pagina', pagina).set('tamano', tamano);
    if (filtros.parqueaderoId) {
      params = params.set('parqueaderoId', filtros.parqueaderoId);
    }
    if (filtros.placa?.trim()) {
      params = params.set('placa', filtros.placa.trim());
    }
    return this.http.get<Pagina<Vehiculo>>(this.url, { params });
  }

  obtener(id: string): Observable<Vehiculo> {
    return this.http.get<Vehiculo>(`${this.url}/${id}`);
  }

  registrar(req: RegistroVehiculoRequest): Observable<Vehiculo> {
    return this.http.post<Vehiculo>(this.url, req);
  }

  actualizar(id: string, req: VehiculoRequest): Observable<Vehiculo> {
    return this.http.put<Vehiculo>(`${this.url}/${id}`, req);
  }

  eliminar(id: string): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }

  trasladar(id: string, parqueaderoId: string): Observable<Vehiculo> {
    return this.http.post<Vehiculo>(`${this.url}/${id}/traslado`, { parqueaderoId });
  }

  asignar(id: string, espacioId: string): Observable<Vehiculo> {
    return this.http.post<Vehiculo>(`${this.url}/${id}/asignacion`, { espacioId });
  }

  reasignar(id: string, espacioId: string): Observable<Vehiculo> {
    return this.http.put<Vehiculo>(`${this.url}/${id}/asignacion`, { espacioId });
  }

  liberar(id: string): Observable<Vehiculo> {
    return this.http.delete<Vehiculo>(`${this.url}/${id}/asignacion`);
  }
}
