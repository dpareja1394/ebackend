export interface CrearInvitadoRequest {
  nombre: string;
  email: string;
  telefono: string;
}

export interface CrearInvitadoResponse {
  idInvitado: number;
  nombre: string;
  email: string;
  telefono: string;
  fechaRegistro: string;
}

export interface InvitadoResponse {
  idInvitado: number;
  nombre: string;
  email: string;
  telefono: string;
  fechaRegistro: string;
}
