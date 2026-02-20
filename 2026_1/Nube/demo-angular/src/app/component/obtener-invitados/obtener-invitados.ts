import {ChangeDetectorRef, Component} from '@angular/core';
import {InvitadoResponse} from '../../model/invitado.dto';
import {Invitado} from '../../service/invitado';

@Component({
  selector: 'app-obtener-invitados',
  imports: [],
  templateUrl: './obtener-invitados.html',
  styleUrl: './obtener-invitados.css',
})
export class ObtenerInvitados {
  invitados: InvitadoResponse[] | undefined;
  invitadoString : string | undefined;

  constructor(
    private invitadoService: Invitado,
    private cdr: ChangeDetectorRef
  ) {
    this.cargarInvitados();
  }

  cargarInvitados(): void {
    this.invitadoService.obtenerTodos().subscribe(
      response => {
        this.invitados = response;
        this.cdr.detectChanges();
      }
    );
  }

  protected buscarPorId(idInvitado: number) {
    this.invitadoService.buscarPorId(idInvitado).subscribe(
      response => {
        this.invitadoString = JSON.stringify(response);
        this.cdr.detectChanges();
      }
    );
  }
}
