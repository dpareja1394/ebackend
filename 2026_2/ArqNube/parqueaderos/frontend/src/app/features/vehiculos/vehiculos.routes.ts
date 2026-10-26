import { Routes } from '@angular/router';

export const VEHICULOS_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./vehiculos-list/vehiculos-list.component').then((m) => m.VehiculosListComponent),
  },
  {
    path: 'nuevo',
    loadComponent: () => import('./vehiculo-form/vehiculo-form.component').then((m) => m.VehiculoFormComponent),
  },
  {
    path: ':id/editar',
    loadComponent: () => import('./vehiculo-form/vehiculo-form.component').then((m) => m.VehiculoFormComponent),
  },
  {
    path: ':id/espacio',
    loadComponent: () =>
      import('./asignacion-espacio/asignacion-espacio.component').then((m) => m.AsignacionEspacioComponent),
  },
];
