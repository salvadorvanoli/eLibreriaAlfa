import { Routes } from '@angular/router';
import { RegisterComponent } from './pages/register/register.component';
import { HomeComponent } from './pages/home/home.component';
import { ViewProductComponent } from './pages/view-product/view-product.component';

export const routes: Routes = [
    { path: 'inicio', component: HomeComponent },
    { path: 'registro', component: RegisterComponent },
    { path: 'product/:id', component: ViewProductComponent },
    { path: '', redirectTo: '/inicio', pathMatch: 'full' }, // si querés que sea la ruta principal
    { path: '**', redirectTo: '/inicio' } // ruta por defecto
];
