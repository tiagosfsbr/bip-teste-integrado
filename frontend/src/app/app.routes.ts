import { Routes } from '@angular/router';
import { ListaBeneficiariosComponent } from './components/lista-beneficiarios/lista-beneficiarios.component';
import { TransferenciaComponent } from './components/transferencia/transferencia.component';

export const appRoutes: Routes = [
  {
    path: '',
    component: ListaBeneficiariosComponent
  },
  {
    path: 'transferencia',
    component: TransferenciaComponent
  },
  {
    path: '**',
    redirectTo: ''
  }
];
