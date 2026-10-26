import { EstadoParqueadero } from '../../core/models/api.models';

export interface Parqueadero {
  id: string;
  nombre: string;
  direccion: string;
  ciudad: string;
  abierto24h: boolean;
  horaApertura: string | null;
  horaCierre: string | null;
  estado: EstadoParqueadero;
  totalEspacios: number;
  creadoEn: string;
  actualizadoEn: string;
}

export interface ParqueaderoRequest {
  nombre: string;
  direccion: string;
  ciudad: string;
  abierto24h: boolean;
  horaApertura: string | null;
  horaCierre: string | null;
}

export interface ParqueaderoUpdateRequest extends ParqueaderoRequest {
  estado: EstadoParqueadero;
}

export interface FiltrosParqueadero {
  ciudad?: string;
  estado?: EstadoParqueadero | '';
}

/** "24 h", "06:00–22:00" o "18:00–02:00 (+1 día)" si cierra al día siguiente. */
export function formatearHorario(p: Pick<Parqueadero, 'abierto24h' | 'horaApertura' | 'horaCierre'>): string {
  if (p.abierto24h || !p.horaApertura || !p.horaCierre) {
    return '24 h';
  }
  const rango = `${p.horaApertura}–${p.horaCierre}`;
  return p.horaCierre < p.horaApertura ? `${rango} (+1 día)` : rango;
}
