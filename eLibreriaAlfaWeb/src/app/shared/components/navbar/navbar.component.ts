import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Component } from '@angular/core';
import { MenubarModule } from 'primeng/menubar';
import { AvatarModule } from 'primeng/avatar';
import { InputTextModule } from 'primeng/inputtext';
import { BadgeModule } from 'primeng/badge';

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

  userName = "Juan";
  items = [
    {
      label: 'Inicio',
      icon: 'pi pi-home',
      routerLink: '/'
    },
    {
      label: 'Catálogo',
      icon: 'pi pi-shop',
      routerLink: '/catalogo'
    },
    {
      label: 'Foro',
      icon: 'pi pi-book',
      routerLink: '/foro'
    },
    {
      label: 'Impresiones',
      icon: 'pi pi-print',
      routerLink: '/impresion'
    },
    {
      label: 'Contáctanos',
      icon: 'pi pi-envelope',
      routerLink: '/contacto'
    },
    {
      label: 'Iniciar sesión',
      icon: 'pi pi-sign-in',
      routerLink: '/inicio-sesion',
      classes: 'bg-blue-200 hover:bg-blue-300 rounded-md',
      textColor: 'white'
    },
    {
      label: 'Registrarse',
      icon: 'pi pi-user-plus',
      routerLink: '/registro',
      classes: 'bg-pink-200 hover:bg-pink-300 rounded-md',
      textColor: 'white'
    },
    {
      label: 'Mi cuenta',
      icon: 'pi pi-user',
      routerLink: '/perfil'
    },
    {
      label: 'Cerrar sesión',
      icon: 'pi pi-sign-out',
      command: () => this.logout()
    }
  ];

  ngOnInit() {
    if (this.isAuthenticated()) {
      this.items = this.items.filter(item => item.label !== 'Iniciar sesión' && item.label !== 'Registrarse');
    } else {
      this.items = this.items.filter(item => item.label !== 'Mi cuenta' && item.label !== 'Cerrar sesión');
    }
  }

  isAuthenticated(): boolean {
    // Aquí puedes implementar la lógica para verificar si el usuario está autenticado
    // Por ejemplo, podrías verificar si hay un token de sesión en una cookie.
    // Si el usuario está autenticado, devuelve true; de lo contrario, devuelve false.
    // return this.authService.isAuthenticated();
    return false;
  }

  logout() {
    console.log('Cerrando sesión...');
  }
}
