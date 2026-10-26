import { Component, OnInit, computed, inject, input, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import {
  ETIQUETAS_TIPO_VEHICULO,
  ErrorResponse,
  TIPOS_VEHICULO,
  TipoVehiculo,
  erroresPorCampo,
} from '../../../core/models/api.models';
import { NotificacionService } from '../../../core/services/notificacion.service';
import { Parqueadero } from '../../parqueaderos/parqueadero.model';
import { ParqueaderoService } from '../../parqueaderos/parqueadero.service';
import { Vehiculo, VehiculoRequest, normalizarPlaca } from '../vehiculo.model';
import { VehiculoService } from '../vehiculo.service';

/** Igual que el servidor: tras normalizar, de 3 a 10 letras o números. */
function placaValida(ctrl: AbstractControl): ValidationErrors | null {
  const placa = normalizarPlaca((ctrl.value as string) ?? '');
  return !placa || /^[A-Z0-9]{3,10}$/.test(placa) ? null : { placa: true };
}

/** La marca es obligatoria salvo para bicicletas. */
function marcaSegunTipo(grupo: AbstractControl): ValidationErrors | null {
  const { tipo, marca } = grupo.getRawValue() as { tipo: TipoVehiculo; marca: string };
  return tipo !== 'BICICLETA' && !marca?.trim() ? { marca: true } : null;
}

@Component({
  selector: 'app-vehiculo-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './vehiculo-form.component.html',
})
export class VehiculoFormComponent implements OnInit {
  /** Viene de la ruta `:id/editar`; vacío al registrar. */
  readonly id = input<string>();
  /** Query param opcional al registrar desde un parqueadero. */
  readonly parqueaderoId = input<string>();

  private readonly fb = inject(FormBuilder);
  private readonly servicio = inject(VehiculoService);
  private readonly parqueaderoService = inject(ParqueaderoService);
  private readonly notificaciones = inject(NotificacionService);
  private readonly router = inject(Router);

  protected readonly tipos = TIPOS_VEHICULO;
  protected readonly etiquetasTipo = ETIQUETAS_TIPO_VEHICULO;
  protected readonly esEdicion = computed(() => !!this.id());
  protected readonly parqueaderos = signal<Parqueadero[]>([]);
  protected readonly vehiculo = signal<Vehiculo | null>(null);
  protected readonly erroresServidor = signal<Record<string, string>>({});
  protected readonly guardando = signal(false);

  protected readonly form = this.fb.nonNullable.group(
    {
      parqueaderoId: [''],
      placa: ['', [Validators.required, placaValida]],
      tipo: ['AUTOMOVIL' as TipoVehiculo],
      marca: ['', Validators.maxLength(50)],
      color: ['', [Validators.required, Validators.maxLength(30)]],
      propietarioNombre: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      propietarioDocumento: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(20)]],
    },
    { validators: marcaSegunTipo },
  );

  protected readonly tipo = toSignal(this.form.controls.tipo.valueChanges, { initialValue: 'AUTOMOVIL' });

  ngOnInit(): void {
    const id = this.id();
    if (id) {
      this.servicio.obtener(id).subscribe((v) => this.cargar(v));
      return;
    }
    // Solo se puede registrar en parqueaderos activos (FR-013a).
    this.parqueaderoService.listar({ estado: 'ACTIVO' }, 0, 100).subscribe((p) => this.parqueaderos.set(p.contenido));
    this.form.controls.parqueaderoId.addValidators(Validators.required);
    this.form.controls.parqueaderoId.setValue(this.parqueaderoId() ?? '');
  }

  protected error(campo: string): string | null {
    const servidor = this.erroresServidor()[campo];
    if (servidor) {
      return servidor;
    }
    const ctrl = this.form.get(campo);
    if (!ctrl?.touched) {
      return null;
    }
    if (campo === 'marca' && this.form.errors?.['marca']) {
      return 'Es obligatoria salvo para bicicletas';
    }
    const e = ctrl.errors;
    if (!e) {
      return null;
    }
    if (e['required']) {
      return 'Es obligatorio';
    }
    if (e['placa']) {
      return 'Debe tener entre 3 y 10 letras o números (sin contar espacios ni guiones)';
    }
    if (e['minlength']) {
      return `Debe tener al menos ${e['minlength'].requiredLength} caracteres`;
    }
    if (e['maxlength']) {
      return `Debe tener máximo ${e['maxlength'].requiredLength} caracteres`;
    }
    return 'Valor no válido';
  }

  protected guardar(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid) {
      return;
    }
    const v = this.form.getRawValue();
    const req: VehiculoRequest = {
      placa: v.placa.trim(),
      tipo: v.tipo,
      marca: v.marca.trim() || null,
      color: v.color.trim(),
      propietarioNombre: v.propietarioNombre.trim(),
      propietarioDocumento: v.propietarioDocumento.trim(),
    };
    const id = this.id();
    const peticion = id
      ? this.servicio.actualizar(id, req)
      : this.servicio.registrar({ ...req, parqueaderoId: v.parqueaderoId });

    this.guardando.set(true);
    this.erroresServidor.set({});
    peticion.subscribe({
      next: (veh) => {
        this.notificaciones.exito(id ? `Vehículo ${veh.placa} actualizado` : `Vehículo ${veh.placa} registrado`);
        // Tras registrar se sugiere asignarle un espacio.
        this.router.navigate(id ? ['/vehiculos'] : ['/vehiculos', veh.id, 'espacio'], {
          queryParams: id ? { parqueaderoId: veh.parqueaderoId } : {},
        });
      },
      error: (e: ErrorResponse) => {
        this.guardando.set(false);
        this.erroresServidor.set(erroresPorCampo(e));
      },
    });
  }

  private cargar(v: Vehiculo): void {
    this.vehiculo.set(v);
    this.form.reset({
      parqueaderoId: v.parqueaderoId,
      placa: v.placa,
      tipo: v.tipo,
      marca: v.marca ?? '',
      color: v.color,
      propietarioNombre: v.propietarioNombre,
      propietarioDocumento: v.propietarioDocumento,
    });
    // Con espacio asignado no se puede cambiar el tipo (el espacio es de ese tipo).
    if (v.espacioId) {
      this.form.controls.tipo.disable();
    }
  }
}
