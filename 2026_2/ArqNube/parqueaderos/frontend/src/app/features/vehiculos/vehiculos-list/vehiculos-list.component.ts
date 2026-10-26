import { Component, OnInit, computed, inject, input, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ETIQUETAS_TIPO_VEHICULO, Pagina } from '../../../core/models/api.models';
import { NotificacionService } from '../../../core/services/notificacion.service';
import { PaginacionComponent } from '../../../shared/components/paginacion/paginacion.component';
import { Parqueadero } from '../../parqueaderos/parqueadero.model';
import { ParqueaderoService } from '../../parqueaderos/parqueadero.service';
import { Vehiculo, normalizarPlaca } from '../vehiculo.model';
import { VehiculoService } from '../vehiculo.service';

@Component({
  selector: 'app-vehiculos-list',
  imports: [FormsModule, RouterLink, PaginacionComponent],
  templateUrl: './vehiculos-list.component.html',
  styleUrl: './vehiculos-list.component.scss',
})
export class VehiculosListComponent implements OnInit {
  /** Query param opcional `?parqueaderoId=` para llegar ya filtrado. */
  readonly parqueaderoId = input<string>();

  private readonly servicio = inject(VehiculoService);
  private readonly parqueaderoService = inject(ParqueaderoService);
  private readonly notificaciones = inject(NotificacionService);

  protected readonly etiquetasTipo = ETIQUETAS_TIPO_VEHICULO;

  protected placa = '';
  protected parqueadero = '';
  protected readonly parqueaderos = signal<Parqueadero[]>([]);
  protected readonly nombres = computed(() => new Map(this.parqueaderos().map((p) => [p.id, p.nombre])));
  protected readonly pagina = signal<Pagina<Vehiculo> | null>(null);
  protected readonly aviso = signal<string | null>('Busque por placa o elija un parqueadero.');
  protected readonly porEliminar = signal<string | null>(null);

  ngOnInit(): void {
    // Para el select basta con la primera página grande; el sistema maneja decenas de parqueaderos.
    this.parqueaderoService.listar({}, 0, 100).subscribe((p) => this.parqueaderos.set(p.contenido));
    const id = this.parqueaderoId();
    if (id) {
      this.parqueadero = id;
      this.cargar(0);
    }
  }

  protected cargar(numero: number): void {
    const placa = normalizarPlaca(this.placa);
    if (!placa && !this.parqueadero) {
      this.pagina.set(null);
      this.aviso.set('Busque por placa o elija un parqueadero.');
      return;
    }
    if (placa && placa.length < 2) {
      this.pagina.set(null);
      this.aviso.set('Escriba al menos 2 caracteres de la placa.');
      return;
    }
    this.aviso.set(null);
    this.servicio
      .listar({ placa, parqueaderoId: this.parqueadero || undefined }, numero)
      .subscribe((p) => this.pagina.set(p));
  }

  protected eliminar(v: Vehiculo): void {
    this.servicio.eliminar(v.id).subscribe({
      next: () => {
        this.porEliminar.set(null);
        this.notificaciones.exito(`Vehículo ${v.placa} eliminado`);
        this.cargar(this.pagina()?.pagina ?? 0);
      },
      error: () => this.porEliminar.set(null),
    });
  }
}
