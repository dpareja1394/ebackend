import { Component, OnInit, inject, input, signal } from '@angular/core';
import { ETIQUETAS_TIPO_VEHICULO, TIPOS_VEHICULO } from '../../../core/models/api.models';
import { Resumen } from '../espacio.model';
import { EspacioService } from '../espacio.service';

/** Disponibilidad del parqueadero por tipo de vehículo (FR-009). */
@Component({
  selector: 'app-resumen-espacios',
  template: `
    <section class="tarjeta">
      <h2>Disponibilidad</h2>
      @if (resumen(); as r) {
        <div class="tabla-contenedor">
          <table>
            <thead>
              <tr>
                <th>Tipo</th>
                <th class="num">Total</th>
                <th class="num">Disponibles</th>
                <th class="num">Ocupados</th>
                <th class="num">Fuera de servicio</th>
              </tr>
            </thead>
            <tbody>
              @for (t of tipos; track t) {
                <tr>
                  <td>{{ etiquetas[t] }}</td>
                  <td class="num">{{ r.porTipo[t].total }}</td>
                  <td class="num">{{ r.porTipo[t].disponibles }}</td>
                  <td class="num">{{ r.porTipo[t].ocupados }}</td>
                  <td class="num">{{ r.porTipo[t].fueraDeServicio }}</td>
                </tr>
              }
            </tbody>
            <tfoot>
              <tr class="total">
                <td>Total</td>
                <td class="num">{{ r.general.total }}</td>
                <td class="num">{{ r.general.disponibles }}</td>
                <td class="num">{{ r.general.ocupados }}</td>
                <td class="num">{{ r.general.fueraDeServicio }}</td>
              </tr>
            </tfoot>
          </table>
        </div>
      }
    </section>
  `,
  styles: `
    .total td {
      font-weight: 700;
      border-bottom: none;
    }
  `,
})
export class ResumenEspaciosComponent implements OnInit {
  readonly parqueaderoId = input.required<string>();

  private readonly servicio = inject(EspacioService);

  protected readonly tipos = TIPOS_VEHICULO;
  protected readonly etiquetas = ETIQUETAS_TIPO_VEHICULO;
  protected readonly resumen = signal<Resumen | null>(null);

  ngOnInit(): void {
    this.recargar();
  }

  recargar(): void {
    this.servicio.resumen(this.parqueaderoId()).subscribe((r) => this.resumen.set(r));
  }
}
