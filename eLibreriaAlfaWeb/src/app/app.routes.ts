import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { RegisterComponent } from './pages/register/register.component';
import { LoginComponent } from './pages/login/login.component';

export const routes: Routes = [
    { path: 'inicio', component: HomeComponent },
    { path: 'registro', component: RegisterComponent },
    { path: 'inicio-sesion', component: LoginComponent },
    { path: '', redirectTo: '/inicio', pathMatch: 'full' },
    { path: '**', redirectTo: '/inicio' }
];
