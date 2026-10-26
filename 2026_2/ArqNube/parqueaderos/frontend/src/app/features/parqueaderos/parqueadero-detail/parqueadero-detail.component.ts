import { Component, OnInit, inject, input, signal, viewChild } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ETIQUETAS_ESTADO_PARQUEADERO } from '../../../core/models/api.models';
import { EspaciosPanelComponent } from '../../espacios/espacios-panel/espacios-panel.component';
import { ResumenEspaciosComponent } from '../../espacios/resumen-espacios/resumen-espacios.component';
import { VehiculosParqueaderoComponent } from '../../vehiculos/vehiculos-parqueadero/vehiculos-parqueadero.component';
import { Parqueadero, formatearHorario } from '../parqueadero.model';
import { ParqueaderoService } from '../parqueadero.service';

@Component({
  selector: 'app-parqueadero-detail',
  imports: [RouterLink, ResumenEspaciosComponent, EspaciosPanelComponent, VehiculosParqueaderoComponent],
  templateUrl: './parqueadero-detail.component.html',
})
export class ParqueaderoDetailComponent implements OnInit {
  readonly id = input.required<string>();

  private readonly servicio = inject(ParqueaderoService);

  protected readonly etiquetasEstado = ETIQUETAS_ESTADO_PARQUEADERO;
  protected readonly formatearHorario = formatearHorario;
  protected readonly parqueadero = signal<Parqueadero | null>(null);
  private readonly resumen = viewChild(ResumenEspaciosComponent);

  ngOnInit(): void {
    this.recargar();
  }

  protected recargar(): void {
    this.servicio.obtener(this.id()).subscribe((p) => this.parqueadero.set(p));
  }

  /** Tras cambiar espacios (o vehículos), se actualizan el total y el resumen de disponibilidad. */
  protected alCambiarEspacios(): void {
    this.recargar();
    this.resumen()?.recargar();
  }
}
