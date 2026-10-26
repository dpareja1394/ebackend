import { Component, OnInit, inject, input, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ETIQUETAS_TIPO_VEHICULO, Pagina } from '../../../core/models/api.models';
import { PaginacionComponent } from '../../../shared/components/paginacion/paginacion.component';
import { Vehiculo } from '../vehiculo.model';
import { VehiculoService } from '../vehiculo.service';

/** Sección "Vehículos" del detalle de un parqueadero. */
@Component({
  selector: 'app-vehiculos-parqueadero',
  imports: [RouterLink, PaginacionComponent],
  template: `
    <section class="tarjeta">
      <div class="encabezado">
        <h2>Vehículos</h2>
        @if (activo()) {
          <a class="btn btn-sm btn-primario" routerLink="/vehiculos/nuevo" [queryParams]="{ parqueaderoId: parqueaderoId() }">
            Registrar vehículo
          </a>
        }
      </div>
      @if (pagina(); as p) {
        @if (p.contenido.length === 0) {
          <p class="vacio">Este parqueadero no tiene vehículos registrados.</p>
        } @else {
          <div class="tabla-contenedor">
            <table>
              <thead>
                <tr>
                  <th>Placa</th>
                  <th>Tipo</th>
                  <th>Propietario</th>
                  <th>Espacio</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                @for (v of p.contenido; track v.id) {
                  <tr>
                    <td><strong>{{ v.placa }}</strong></td>
                    <td>{{ etiquetasTipo[v.tipo] }}</td>
                    <td>{{ v.propietarioNombre }}</td>
                    <td>
                      @if (v.espacioCodigo) {
                        <span class="chip chip-aviso">{{ v.espacioCodigo }}</span>
                      } @else {
                        Sin espacio
                      }
                    </td>
                    <td><a class="btn btn-sm" [routerLink]="['/vehiculos', v.id, 'espacio']">Espacio / traslado</a></td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
          <app-paginacion
            [pagina]="p.pagina"
            [totalPaginas]="p.totalPaginas"
            [totalElementos]="p.totalElementos"
            (cambio)="cargar($event)"
          />
        }
      }
    </section>
  `,
})
export class VehiculosParqueaderoComponent implements OnInit {
  readonly parqueaderoId = input.required<string>();
  readonly activo = input(true);

  private readonly servicio = inject(VehiculoService);

  protected readonly etiquetasTipo = ETIQUETAS_TIPO_VEHICULO;
  protected readonly pagina = signal<Pagina<Vehiculo> | null>(null);

  ngOnInit(): void {
    this.cargar(0);
  }

  recargar(): void {
    this.cargar(this.pagina()?.pagina ?? 0);
  }

  protected cargar(numero: number): void {
    this.servicio.listar({ parqueaderoId: this.parqueaderoId() }, numero).subscribe((p) => this.pagina.set(p));
  }
}
