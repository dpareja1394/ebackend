import { EstadoEspacio, TipoVehiculo } from '../../core/models/api.models';

export interface Espacio {
  id: string;
  parqueaderoId: string;
  codigo: string;
  tipoVehiculo: TipoVehiculo;
  estado: EstadoEspacio;
  vehiculoId: string | null;
}

export interface EspacioRequest {
  codigo: string;
  tipoVehiculo: TipoVehiculo;
}

export interface EspacioUpdateRequest extends EspacioRequest {
  /** El administrador solo puede alternar entre estos dos; OCUPADO lo gestiona el sistema. */
  estado: Exclude<EstadoEspacio, 'OCUPADO'>;
}

export interface FiltrosEspacio {
  estado?: EstadoEspacio | '';
  tipoVehiculo?: TipoVehiculo | '';
}

export interface ConteoEspacios {
  total: number;
  disponibles: number;
  ocupados: number;
  fueraDeServicio: number;
}

export interface Resumen {
  parqueaderoId: string;
  general: ConteoEspacios;
  porTipo: Record<TipoVehiculo, ConteoEspacios>;
}
