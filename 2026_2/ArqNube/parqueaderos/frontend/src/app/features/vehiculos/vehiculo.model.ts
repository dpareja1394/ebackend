import { TipoVehiculo } from '../../core/models/api.models';

export interface Vehiculo {
  id: string;
  placa: string;
  tipo: TipoVehiculo;
  marca: string | null;
  color: string;
  propietarioNombre: string;
  propietarioDocumento: string;
  parqueaderoId: string;
  espacioId: string | null;
  espacioCodigo: string | null;
  creadoEn: string;
  actualizadoEn: string;
}

export interface VehiculoRequest {
  placa: string;
  tipo: TipoVehiculo;
  marca: string | null;
  color: string;
  propietarioNombre: string;
  propietarioDocumento: string;
}

export interface RegistroVehiculoRequest extends VehiculoRequest {
  parqueaderoId: string;
}

/** La API exige al menos uno de los dos filtros. */
export interface FiltrosVehiculo {
  parqueaderoId?: string;
  placa?: string;
}

/** Misma normalización que el servidor: mayúsculas, sin espacios ni guiones. */
export function normalizarPlaca(placa: string): string {
  return placa.replace(/[\s-]/g, '').toUpperCase();
}
