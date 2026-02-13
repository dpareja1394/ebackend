import { Injectable } from '@angular/core';
import {environment} from '../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {CrearInvitadoRequest, CrearInvitadoResponse, InvitadoResponse} from '../model/invitado.dto';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class Invitado {
  private readonly apiUrl = environment.apiUrl+'/invitados';

  constructor(private http: HttpClient) {
  }

  crearInvitado(request : CrearInvitadoRequest) : Observable<CrearInvitadoResponse> {
    return this.http.post<CrearInvitadoResponse>(this.apiUrl, request);
  }

  buscarPorId(id: number): Observable<InvitadoResponse> {
    return this.http.get<InvitadoResponse>(`${this.apiUrl}/${id}`);
  }

  obtenerTodos(): Observable<InvitadoResponse[]> {
    return this.http.get<InvitadoResponse[]>(`${this.apiUrl}/all`);
  }

}
