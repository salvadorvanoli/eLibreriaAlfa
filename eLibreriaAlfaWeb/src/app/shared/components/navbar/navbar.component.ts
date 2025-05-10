import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Component } from '@angular/core';
import { MenubarModule } from 'primeng/menubar';
import { AvatarModule } from 'primeng/avatar';
import { InputTextModule } from 'primeng/inputtext';
import { BadgeModule } from 'primeng/badge';
import { SeguridadService } from '../../../core/services/seguridad.service';
import { Observable } from 'rxjs';
import { UsuarioSimple } from '../../../core/models/usuario';
import { NavbarItem } from '../../../core/models/navbar-item';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MenubarModule,
    AvatarModule,
    InputTextModule,
    BadgeModule
  ],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent {

  user!: Observable<UsuarioSimple | null>;
  items!: NavbarItem[];

  constructor(private seguridadService: SeguridadService) {}

  ngOnInit() {
    this.user = this.seguridadService.user;

    this.user.subscribe(userEntity => {

      this.items = [
        { label: 'Inicio', icon: 'pi pi-home', routerLink: '/' },
        { label: 'Catálogo', icon: 'pi pi-shop', routerLink: '/catalogo' },
        { label: 'Foro', icon: 'pi pi-book', routerLink: '/foro' },
        { label: 'Impresiones', icon: 'pi pi-print', routerLink: '/impresion' },
        { label: 'Contáctanos', icon: 'pi pi-envelope', routerLink: '/contacto' }
      ];

      if (userEntity) {
        this.items.push(
          { label: 'Mi cuenta', icon: 'pi pi-user', routerLink: '/perfil' },
          { label: 'Cerrar sesión', icon: 'pi pi-sign-out', routerLink: '', command: () => this.logout() }
        );

        if (userEntity.rol === "ADMINISTRADOR") {
          this.items.push({ label: 'Panel de control', icon: 'pi pi-cog', routerLink: '/panel-de-control' });
        }
      } else {
        this.items.push(
          { label: 'Iniciar sesión', icon: 'pi pi-sign-in', routerLink: '/inicio-sesion', classes: 'bg-blue-200 hover:bg-blue-300 rounded-md' },
          { label: 'Registrarse', icon: 'pi pi-user-plus', routerLink: '/registro', classes: 'bg-pink-200 hover:bg-pink-300 rounded-md' }
        );
      }
      
    });
  }

  logout() {
    this.seguridadService.logout().subscribe();
  }
}
