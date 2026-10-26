import { Component, inject } from '@angular/core';
import { NotificacionService } from '../../../core/services/notificacion.service';

@Component({
  selector: 'app-notificaciones',
  template: `
    <div class="notificaciones" aria-live="polite">
      @for (n of servicio.notificaciones(); track n.id) {
        <div class="notificacion" [class.error]="n.tipo === 'error'" role="status">
          <span>{{ n.mensaje }}</span>
          <button type="button" class="cerrar" (click)="servicio.cerrar(n.id)" aria-label="Cerrar">×</button>
        </div>
      }
    </div>
  `,
  styles: `
    .notificaciones {
      position: fixed;
      right: 1rem;
      bottom: 1rem;
      display: flex;
      flex-direction: column;
      gap: 0.5rem;
      z-index: 1000;
      max-width: min(26rem, calc(100vw - 2rem));
    }
    .notificacion {
      display: flex;
      align-items: flex-start;
      gap: 0.75rem;
      padding: 0.75rem 1rem;
      border-radius: 0.5rem;
      background: var(--exito-fondo);
      color: var(--exito-texto);
      box-shadow: 0 4px 12px rgb(0 0 0 / 0.15);
    }
    .notificacion.error {
      background: var(--error-fondo);
      color: var(--error-texto);
    }
    .cerrar {
      margin-left: auto;
      background: none;
      border: none;
      color: inherit;
      font-size: 1.25rem;
      line-height: 1;
      cursor: pointer;
      padding: 0;
    }
  `,
})
export class NotificacionesComponent {
  protected readonly servicio = inject(NotificacionService);
}
