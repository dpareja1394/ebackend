/** Respuesta paginada común de la API (research.md R10). */
export interface Pagina<T> {
  contenido: T[];
  pagina: number;
  tamano: number;
  totalElementos: number;
  totalPaginas: number;
}

export interface DetalleError {
  campo: string;
  mensaje: string;
}

/** Formato de error uniforme de todos los servicios (research.md R9). */
export interface ErrorResponse {
  codigo: string;
  mensaje: string;
  detalles: DetalleError[];
  correlationId?: string;
  timestamp?: string;
  /** Estado HTTP, agregado por el interceptor. */
  status?: number;
}

export type TipoVehiculo = 'AUTOMOVIL' | 'MOTOCICLETA' | 'BICICLETA';
export type EstadoParqueadero = 'ACTIVO' | 'INACTIVO';
export type EstadoEspacio = 'DISPONIBLE' | 'OCUPADO' | 'FUERA_DE_SERVICIO';

export const TIPOS_VEHICULO: TipoVehiculo[] = ['AUTOMOVIL', 'MOTOCICLETA', 'BICICLETA'];
export const ESTADOS_PARQUEADERO: EstadoParqueadero[] = ['ACTIVO', 'INACTIVO'];
export const ESTADOS_ESPACIO: EstadoEspacio[] = ['DISPONIBLE', 'OCUPADO', 'FUERA_DE_SERVICIO'];

export const ETIQUETAS_TIPO_VEHICULO: Record<TipoVehiculo, string> = {
  AUTOMOVIL: 'Automóvil',
  MOTOCICLETA: 'Motocicleta',
  BICICLETA: 'Bicicleta',
};

export const ETIQUETAS_ESTADO_PARQUEADERO: Record<EstadoParqueadero, string> = {
  ACTIVO: 'Activo',
  INACTIVO: 'Inactivo',
};

export const ETIQUETAS_ESTADO_ESPACIO: Record<EstadoEspacio, string> = {
  DISPONIBLE: 'Disponible',
  OCUPADO: 'Ocupado',
  FUERA_DE_SERVICIO: 'Fuera de servicio',
};

/** Convierte un ErrorResponse en un mapa campo → mensaje para mostrarlo junto a cada input. */
export function erroresPorCampo(error: ErrorResponse | null | undefined): Record<string, string> {
  const mapa: Record<string, string> = {};
  for (const d of error?.detalles ?? []) {
    mapa[d.campo] = d.mensaje;
  }
  return mapa;
}
