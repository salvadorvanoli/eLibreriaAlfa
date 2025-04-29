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
    }
  ];

  logout() {
    console.log('Cerrando sesión...');
  }
}
