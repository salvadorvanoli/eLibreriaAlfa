import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { RegisterComponent } from './pages/register/register.component';
import { LoginComponent } from './pages/login/login.component';
import { ControlPanelComponent } from './pages/control-panel/control-panel.component';
import { CatalogComponent } from './pages/catalog/catalog.component';
import { ViewProductComponent } from './pages/view-product/view-product.component';
import { ContactUsComponent } from './pages/contact-us/contact-us.component';
import { ViewProfileComponent } from './pages/view-profile/view-profile.component';
import { ForumComponent } from './pages/forum/forum.component';
import { PublicationComponent } from './pages/publication/publication.component';
import { NotFoundComponent } from './pages/not-found/not-found.component';
import { PrintsComponent } from './pages/prints/prints.component';
import { RoleBasedGuard } from './core/guards/role-based.guard';
import { Rol } from './core/models/rol';

export const routes: Routes = [
    { path: 'inicio', component: HomeComponent },
    { path: 'registro', component: RegisterComponent },
    { path: 'inicio-sesion', component: LoginComponent },
    { 
        path: 'panel', 
        component: ControlPanelComponent, 
        canActivate: [RoleBasedGuard],
        data: {
            roleConfig: {
                blockedRoles: [Rol.CLIENTE],
                requireAuth: true
            }
        }
    },
    { path: 'catalogo', component: CatalogComponent },
    { path: 'producto/:id', component: ViewProductComponent },
    { 
        path: 'contacto', 
        component: ContactUsComponent, 
        canActivate: [RoleBasedGuard],
        data: {
            roleConfig: {
                blockedRoles: [Rol.ADMINISTRADOR, Rol.EMPLEADO],
                requireAuth: false
            }
        }
    },
    { 
        path: 'perfil', 
        component: ViewProfileComponent, 
        canActivate: [RoleBasedGuard],
        data: {
            roleConfig: {
                requireAuth: true
            }
        }
    },
    { path: 'foro', component: ForumComponent },
    { path: 'publicacion/:id', component: PublicationComponent },
    { 
        path: 'impresion', 
        component: PrintsComponent, 
        canActivate: [RoleBasedGuard],
        data: {
            roleConfig: {
                blockedRoles: [Rol.ADMINISTRADOR, Rol.EMPLEADO],
                requireAuth: true
            }
        }
    },
    { path: '', redirectTo: '/inicio', pathMatch: 'full' },
    { path: '**', component: NotFoundComponent }
];
