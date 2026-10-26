import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import {
  ESTADOS_PARQUEADERO,
  ETIQUETAS_ESTADO_PARQUEADERO,
  EstadoParqueadero,
  Pagina,
} from '../../../core/models/api.models';
import { NotificacionService } from '../../../core/services/notificacion.service';
import { PaginacionComponent } from '../../../shared/components/paginacion/paginacion.component';
import { Parqueadero, formatearHorario } from '../parqueadero.model';
import { ParqueaderoService } from '../parqueadero.service';

@Component({
  selector: 'app-parqueaderos-list',
  imports: [FormsModule, RouterLink, PaginacionComponent],
  templateUrl: './parqueaderos-list.component.html',
  styleUrl: './parqueaderos-list.component.scss',
})
export class ParqueaderosListComponent implements OnInit {
  private readonly servicio = inject(ParqueaderoService);
  private readonly notificaciones = inject(NotificacionService);

  protected readonly estados = ESTADOS_PARQUEADERO;
  protected readonly etiquetasEstado = ETIQUETAS_ESTADO_PARQUEADERO;
  protected readonly formatearHorario = formatearHorario;

  protected ciudad = '';
  protected estado: EstadoParqueadero | '' = '';
  protected readonly pagina = signal<Pagina<Parqueadero> | null>(null);
  protected readonly cargando = signal(false);
  protected readonly porEliminar = signal<string | null>(null);

  ngOnInit(): void {
    this.cargar(0);
  }

  protected buscar(): void {
    this.cargar(0);
  }

  protected cargar(numero: number): void {
    this.cargando.set(true);
    this.servicio.listar({ ciudad: this.ciudad, estado: this.estado }, numero).subscribe({
      next: (p) => {
        this.pagina.set(p);
        this.cargando.set(false);
      },
      error: () => this.cargando.set(false),
    });
  }

  protected eliminar(p: Parqueadero): void {
    this.servicio.eliminar(p.id).subscribe({
      next: () => {
        this.porEliminar.set(null);
        this.notificaciones.exito(`Parqueadero "${p.nombre}" eliminado`);
        this.cargar(this.pagina()?.pagina ?? 0);
      },
      error: () => this.porEliminar.set(null),
    });
  }
}
