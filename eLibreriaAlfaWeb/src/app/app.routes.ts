import { Routes } from '@angular/router';
import { RegisterComponent } from './pages/register/register.component';
import { HomeComponent } from './pages/home/home.component';

export const routes: Routes = [
    { path: 'inicio', component: HomeComponent },
    { path: 'registro', component: RegisterComponent },
    { path: '', redirectTo: '/inicio', pathMatch: 'full' }, // si querés que sea la ruta principal
    { path: '**', redirectTo: '/inicio' } // ruta por defecto
];
