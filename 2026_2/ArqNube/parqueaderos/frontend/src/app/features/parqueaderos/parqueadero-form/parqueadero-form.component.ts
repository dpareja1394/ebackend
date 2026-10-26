import { Component, OnInit, computed, inject, input, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import {
  ESTADOS_PARQUEADERO,
  ETIQUETAS_ESTADO_PARQUEADERO,
  ErrorResponse,
  EstadoParqueadero,
  erroresPorCampo,
} from '../../../core/models/api.models';
import { NotificacionService } from '../../../core/services/notificacion.service';
import { Parqueadero, ParqueaderoRequest } from '../parqueadero.model';
import { ParqueaderoService } from '../parqueadero.service';

/** Si no abre 24 h, ambas horas son obligatorias y distintas; cierre < apertura = cierra al día siguiente. */
function horarioValido(grupo: AbstractControl): ValidationErrors | null {
  const { abierto24h, horaApertura, horaCierre } = grupo.value as {
    abierto24h: boolean;
    horaApertura: string;
    horaCierre: string;
  };
  if (abierto24h) {
    return null;
  }
  if (!horaApertura || !horaCierre) {
    return { horario: 'Indique la hora de apertura y la de cierre' };
  }
  if (horaApertura === horaCierre) {
    return { horario: 'La hora de apertura y la de cierre deben ser distintas' };
  }
  return null;
}

@Component({
  selector: 'app-parqueadero-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './parqueadero-form.component.html',
})
export class ParqueaderoFormComponent implements OnInit {
  /** Viene de la ruta `:id/editar`; vacío al crear. */
  readonly id = input<string>();

  private readonly fb = inject(FormBuilder);
  private readonly servicio = inject(ParqueaderoService);
  private readonly notificaciones = inject(NotificacionService);
  private readonly router = inject(Router);

  protected readonly estados = ESTADOS_PARQUEADERO;
  protected readonly etiquetasEstado = ETIQUETAS_ESTADO_PARQUEADERO;
  protected readonly esEdicion = computed(() => !!this.id());
  protected readonly erroresServidor = signal<Record<string, string>>({});
  protected readonly guardando = signal(false);

  protected readonly form = this.fb.nonNullable.group(
    {
      nombre: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      direccion: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(200)]],
      ciudad: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(80)]],
      abierto24h: [false],
      horaApertura: [''],
      horaCierre: [''],
      estado: ['ACTIVO' as EstadoParqueadero],
    },
    { validators: horarioValido },
  );

  protected readonly abierto24h = toSignal(this.form.controls.abierto24h.valueChanges, {
    initialValue: false,
  });

  private readonly valores = toSignal(this.form.valueChanges, { initialValue: this.form.getRawValue() });

  ngOnInit(): void {
    const id = this.id();
    if (id) {
      this.servicio.obtener(id).subscribe((p) => this.cargar(p));
    }
  }

  protected cierraAlDiaSiguiente(): boolean {
    const v = this.valores();
    return !v.abierto24h && !!v.horaApertura && !!v.horaCierre && v.horaCierre < v.horaApertura;
  }

  protected error(campo: string): string | null {
    const servidor = this.erroresServidor()[campo];
    if (servidor) {
      return servidor;
    }
    if (campo === 'horario') {
      const ctrlTocado = this.form.controls.horaApertura.touched || this.form.controls.horaCierre.touched;
      return ctrlTocado ? ((this.form.errors?.['horario'] as string | undefined) ?? null) : null;
    }
    const ctrl = this.form.get(campo);
    if (!ctrl || !ctrl.touched || !ctrl.errors) {
      return null;
    }
    if (ctrl.errors['required']) {
      return 'Es obligatorio';
    }
    if (ctrl.errors['minlength'] || ctrl.errors['maxlength']) {
      const req = ctrl.errors['minlength'] ?? ctrl.errors['maxlength'];
      return ctrl.errors['minlength']
        ? `Debe tener al menos ${req.requiredLength} caracteres`
        : `Debe tener máximo ${req.requiredLength} caracteres`;
    }
    return 'Valor no válido';
  }

  protected guardar(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid) {
      return;
    }
    const v = this.form.getRawValue();
    const req: ParqueaderoRequest = {
      nombre: v.nombre.trim(),
      direccion: v.direccion.trim(),
      ciudad: v.ciudad.trim(),
      abierto24h: v.abierto24h,
      horaApertura: v.abierto24h ? null : v.horaApertura,
      horaCierre: v.abierto24h ? null : v.horaCierre,
    };
    const id = this.id();
    const peticion = id
      ? this.servicio.actualizar(id, { ...req, estado: v.estado })
      : this.servicio.crear(req);

    this.guardando.set(true);
    this.erroresServidor.set({});
    peticion.subscribe({
      next: (p) => {
        this.notificaciones.exito(id ? 'Parqueadero actualizado' : 'Parqueadero creado');
        this.router.navigate(['/parqueaderos', p.id]);
      },
      error: (e: ErrorResponse) => {
        this.guardando.set(false);
        this.erroresServidor.set(erroresPorCampo(e));
      },
    });
  }

  private cargar(p: Parqueadero): void {
    this.form.reset({
      nombre: p.nombre,
      direccion: p.direccion,
      ciudad: p.ciudad,
      abierto24h: p.abierto24h,
      horaApertura: p.horaApertura ?? '',
      horaCierre: p.horaCierre ?? '',
      estado: p.estado,
    });
  }
}
