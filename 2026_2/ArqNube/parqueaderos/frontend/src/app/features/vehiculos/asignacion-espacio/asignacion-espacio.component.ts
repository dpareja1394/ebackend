import { Component, OnInit, computed, inject, input, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Observable } from 'rxjs';
import { ETIQUETAS_TIPO_VEHICULO, Pagina } from '../../../core/models/api.models';
import { NotificacionService } from '../../../core/services/notificacion.service';
import { PaginacionComponent } from '../../../shared/components/paginacion/paginacion.component';
import { Espacio } from '../../espacios/espacio.model';
import { EspacioService } from '../../espacios/espacio.service';
import { Parqueadero } from '../../parqueaderos/parqueadero.model';
import { ParqueaderoService } from '../../parqueaderos/parqueadero.service';
import { Vehiculo } from '../vehiculo.model';
import { VehiculoService } from '../vehiculo.service';

/** Asignar, reasignar o liberar el espacio de un vehículo, y trasladarlo de parqueadero (FR-013 a FR-016). */
@Component({
  selector: 'app-asignacion-espacio',
  imports: [FormsModule, RouterLink, PaginacionComponent],
  templateUrl: './asignacion-espacio.component.html',
})
export class AsignacionEspacioComponent implements OnInit {
  readonly id = input.required<string>();

  private readonly servicio = inject(VehiculoService);
  private readonly espacioService = inject(EspacioService);
  private readonly parqueaderoService = inject(ParqueaderoService);
  private readonly notificaciones = inject(NotificacionService);

  protected readonly etiquetasTipo = ETIQUETAS_TIPO_VEHICULO;
  protected readonly vehiculo = signal<Vehiculo | null>(null);
  protected readonly parqueadero = signal<Parqueadero | null>(null);
  protected readonly disponibles = signal<Pagina<Espacio> | null>(null);
  protected readonly activos = signal<Parqueadero[]>([]);
  protected readonly destinos = computed(() =>
    this.activos().filter((p) => p.id !== this.vehiculo()?.parqueaderoId),
  );
  protected readonly ocupado = signal(false);
  protected destino = '';

  ngOnInit(): void {
    this.recargar();
    this.parqueaderoService.listar({ estado: 'ACTIVO' }, 0, 100).subscribe((p) => this.activos.set(p.contenido));
  }

  protected cargarDisponibles(numero = 0): void {
    const v = this.vehiculo();
    if (!v) {
      return;
    }
    this.espacioService
      .listar(v.parqueaderoId, { estado: 'DISPONIBLE', tipoVehiculo: v.tipo }, numero)
      .subscribe((p) => this.disponibles.set(p));
  }

  protected ocupar(e: Espacio): void {
    const v = this.vehiculo()!;
    const peticion = v.espacioId ? this.servicio.reasignar(v.id, e.id) : this.servicio.asignar(v.id, e.id);
    this.ejecutar(peticion, `${v.placa} quedó en el espacio ${e.codigo}`);
  }

  protected liberar(): void {
    const v = this.vehiculo()!;
    this.ejecutar(this.servicio.liberar(v.id), `Espacio ${v.espacioCodigo} liberado`);
  }

  protected trasladar(): void {
    const v = this.vehiculo()!;
    const nombre = this.activos().find((p) => p.id === this.destino)?.nombre ?? '';
    this.ejecutar(this.servicio.trasladar(v.id, this.destino), `${v.placa} trasladado a ${nombre}`);
    this.destino = '';
  }

  /** Ejecuta una operación y recarga todo; los errores los muestra el interceptor. */
  private ejecutar(peticion: Observable<Vehiculo>, mensaje: string): void {
    this.ocupado.set(true);
    peticion.subscribe({
      next: () => {
        this.notificaciones.exito(mensaje);
        this.recargar();
      },
      // Un 409 suele significar que el espacio lo tomó otro vehículo: se refresca la lista.
      error: () => this.recargar(),
    });
  }

  private recargar(): void {
    this.servicio.obtener(this.id()).subscribe({
      next: (v) => {
        this.vehiculo.set(v);
        this.ocupado.set(false);
        this.parqueaderoService.obtener(v.parqueaderoId).subscribe((p) => this.parqueadero.set(p));
        this.cargarDisponibles();
      },
      error: () => this.ocupado.set(false),
    });
  }
}
