import { Component, computed, input, output } from '@angular/core';

/** Controles de "anterior / siguiente" para cualquier listado paginado. */
@Component({
  selector: 'app-paginacion',
  template: `
    @if (totalElementos() > 0) {
      <div class="paginacion">
        <span>{{ totalElementos() }} en total · página {{ pagina() + 1 }} de {{ totalPaginas() }}</span>
        <button type="button" class="btn btn-sm" [disabled]="pagina() === 0" (click)="cambio.emit(pagina() - 1)">
          Anterior
        </button>
        <button type="button" class="btn btn-sm" [disabled]="esUltima()" (click)="cambio.emit(pagina() + 1)">
          Siguiente
        </button>
      </div>
    }
  `,
})
export class PaginacionComponent {
  readonly pagina = input.required<number>();
  readonly totalPaginas = input.required<number>();
  readonly totalElementos = input.required<number>();
  readonly cambio = output<number>();

  protected readonly esUltima = computed(() => this.pagina() + 1 >= this.totalPaginas());
}
