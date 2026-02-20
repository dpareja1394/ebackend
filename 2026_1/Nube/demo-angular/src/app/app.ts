import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {ObtenerInvitados} from './component/obtener-invitados/obtener-invitados';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, ObtenerInvitados],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('demo-angular');
}
