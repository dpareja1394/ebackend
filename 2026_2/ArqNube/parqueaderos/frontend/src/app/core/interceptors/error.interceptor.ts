import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { ErrorResponse } from '../models/api.models';
import { NotificacionService } from '../services/notificacion.service';

/**
 * Convierte cualquier error HTTP en un ErrorResponse y muestra su mensaje (FR-019).
 * Los componentes reciben el ErrorResponse para mostrar los detalles por campo.
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const notificaciones = inject(NotificacionService);
  return next(req).pipe(
    catchError((err: unknown) => {
      const error = aErrorResponse(err);
      notificaciones.error(error.mensaje);
      return throwError(() => error);
    }),
  );
};

function aErrorResponse(err: unknown): ErrorResponse {
  if (!(err instanceof HttpErrorResponse)) {
    return { codigo: 'ERROR_INTERNO', mensaje: 'Ocurrió un error inesperado', detalles: [] };
  }
  if (err.status === 0) {
    return { codigo: 'SIN_CONEXION', mensaje: 'No se pudo conectar con el servidor', detalles: [], status: 0 };
  }
  const cuerpo = err.error as Partial<ErrorResponse> | null;
  if (cuerpo && typeof cuerpo === 'object' && typeof cuerpo.mensaje === 'string') {
    return {
      codigo: cuerpo.codigo ?? 'ERROR_INTERNO',
      mensaje: cuerpo.mensaje,
      detalles: cuerpo.detalles ?? [],
      correlationId: cuerpo.correlationId,
      timestamp: cuerpo.timestamp,
      status: err.status,
    };
  }
  return { codigo: 'ERROR_INTERNO', mensaje: `Error del servidor (${err.status})`, detalles: [], status: err.status };
}
