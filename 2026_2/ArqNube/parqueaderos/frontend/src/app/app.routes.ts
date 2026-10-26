import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'parqueaderos' },
  {
    path: 'parqueaderos',
    loadChildren: () =>
      import('./features/parqueaderos/parqueaderos.routes').then((m) => m.PARQUEADEROS_ROUTES),
  },
  {
    path: 'vehiculos',
    loadChildren: () => import('./features/vehiculos/vehiculos.routes').then((m) => m.VEHICULOS_ROUTES),
  },
  { path: '**', redirectTo: 'parqueaderos' },
];
