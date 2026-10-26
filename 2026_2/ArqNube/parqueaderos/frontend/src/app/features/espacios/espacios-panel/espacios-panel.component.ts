import { Component, OnInit, inject, input, output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  ESTADOS_ESPACIO,
  ETIQUETAS_ESTADO_ESPACIO,
  ETIQUETAS_TIPO_VEHICULO,
  ErrorResponse,
  EstadoEspacio,
  Pagina,
  TIPOS_VEHICULO,
  TipoVehiculo,
  erroresPorCampo,
} from '../../../core/models/api.models';
import { NotificacionService } from '../../../core/services/notificacion.service';
import { PaginacionComponent } from '../../../shared/components/paginacion/paginacion.component';
import { Espacio } from '../espacio.model';
import { EspacioService } from '../espacio.service';

@Component({
  selector: 'app-espacios-panel',
  imports: [FormsModule, PaginacionComponent],
  templateUrl: './espacios-panel.component.html',
  styleUrl: './espacios-panel.component.scss',
})
export class EspaciosPanelComponent implements OnInit {
  readonly parqueaderoId = input.required<string>();
  /** Se emite después de cada operación que cambia los espacios, para recargar el resumen. */
  readonly cambio = output<void>();

  private readonly servicio = inject(EspacioService);
  private readonly notificaciones = inject(NotificacionService);

  protected readonly tipos = TIPOS_VEHICULO;
  protected readonly estados = ESTADOS_ESPACIO;
  protected readonly etiquetasTipo = ETIQUETAS_TIPO_VEHICULO;
  protected readonly etiquetasEstado = ETIQUETAS_ESTADO_ESPACIO;

  protected filtroEstado: EstadoEspacio | '' = '';
  protected filtroTipo: TipoVehiculo | '' = '';
  protected nuevoCodigo = '';
  protected nuevoTipo: TipoVehiculo = 'AUTOMOVIL';

  protected readonly pagina = signal<Pagina<Espacio> | null>(null);
  protected readonly erroresNuevo = signal<Record<string, string>>({});
  protected readonly porEliminar = signal<string | null>(null);
  protected readonly porLiberar = signal<string | null>(null);

  ngOnInit(): void {
    this.cargar(0);
  }

  protected cargar(numero = this.pagina()?.pagina ?? 0): void {
    this.servicio
      .listar(this.parqueaderoId(), { estado: this.filtroEstado, tipoVehiculo: this.filtroTipo }, numero)
      .subscribe((p) => this.pagina.set(p));
  }

  protected crear(): void {
    const codigo = this.nuevoCodigo.trim();
    if (!codigo) {
      this.erroresNuevo.set({ codigo: 'Es obligatorio' });
      return;
    }
    this.servicio.crear(this.parqueaderoId(), { codigo, tipoVehiculo: this.nuevoTipo }).subscribe({
      next: (e) => {
        this.nuevoCodigo = '';
        this.erroresNuevo.set({});
        this.notificaciones.exito(`Espacio ${e.codigo} creado`);
        this.recargarYAvisar();
      },
      error: (e: ErrorResponse) => this.erroresNuevo.set(erroresPorCampo(e)),
    });
  }

  protected alternarServicio(e: Espacio): void {
    const estado = e.estado === 'FUERA_DE_SERVICIO' ? 'DISPONIBLE' : 'FUERA_DE_SERVICIO';
    this.servicio.actualizar(e.id, { codigo: e.codigo, tipoVehiculo: e.tipoVehiculo, estado }).subscribe(() => {
      this.notificaciones.exito(`Espacio ${e.codigo}: ${this.etiquetasEstado[estado].toLowerCase()}`);
      this.recargarYAvisar();
    });
  }

  protected eliminar(e: Espacio): void {
    this.servicio.eliminar(e.id).subscribe({
      next: () => {
        this.porEliminar.set(null);
        this.notificaciones.exito(`Espacio ${e.codigo} eliminado`);
        this.recargarYAvisar();
      },
      error: () => this.porEliminar.set(null),
    });
  }

  protected forzarLiberacion(e: Espacio): void {
    this.servicio.forzarLiberacion(e.id).subscribe({
      next: () => {
        this.porLiberar.set(null);
        this.notificaciones.exito(`Espacio ${e.codigo} liberado`);
        this.recargarYAvisar();
      },
      // El 409 ("está asignado al vehículo …") lo muestra el interceptor.
      error: () => this.porLiberar.set(null),
    });
  }

  private recargarYAvisar(): void {
    this.cargar();
    this.cambio.emit();
  }
}
