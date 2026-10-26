import { Routes } from '@angular/router';

export const PARQUEADEROS_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./parqueaderos-list/parqueaderos-list.component').then((m) => m.ParqueaderosListComponent),
  },
  {
    path: 'nuevo',
    loadComponent: () =>
      import('./parqueadero-form/parqueadero-form.component').then((m) => m.ParqueaderoFormComponent),
  },
  {
    path: ':id',
    loadComponent: () =>
      import('./parqueadero-detail/parqueadero-detail.component').then((m) => m.ParqueaderoDetailComponent),
  },
  {
    path: ':id/editar',
    loadComponent: () =>
      import('./parqueadero-form/parqueadero-form.component').then((m) => m.ParqueaderoFormComponent),
  },
];
