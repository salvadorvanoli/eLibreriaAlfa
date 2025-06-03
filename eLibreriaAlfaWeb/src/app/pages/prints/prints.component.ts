import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EspecificacionesComponent } from './components/especificaciones/especificaciones.component';
import { TitleAndDescriptionComponent } from '../../shared/components/title-and-description/title-and-description.component';
import { UserPrintsComponent } from './components/user-prints/user-prints.component';
import { SecurityService } from '../../core/services/security.service';

@Component({
  selector: 'app-prints',
  standalone: true,
  imports: [CommonModule, EspecificacionesComponent, TitleAndDescriptionComponent, UserPrintsComponent],
  templateUrl: './prints.component.html',
  styleUrl: './prints.component.scss'
})
export class PrintsComponent implements OnInit {
  isUserAuthenticated: boolean = false;

  constructor(private securityService: SecurityService) {}

  ngOnInit(): void {
    this.checkUserAuthentication();
  }

  checkUserAuthentication(): void {
    this.securityService.getActualUser().subscribe({
      next: (currentUser) => {
        this.isUserAuthenticated = !!(currentUser && currentUser.id);
      },
      error: (error) => {
        console.error('Error al verificar autenticación:', error);
        this.isUserAuthenticated = false;
      }
    });
  }
}
