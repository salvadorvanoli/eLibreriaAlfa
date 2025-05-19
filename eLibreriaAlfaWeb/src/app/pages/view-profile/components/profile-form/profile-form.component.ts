import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SecurityService } from '../../../../core/services/security.service';
import { UserService } from '../../../../core/services/user.service';
import { ModificarPerfilUsuario } from '../../../../core/models/usuario';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';

@Component({
  selector: 'app-profile-form',
  standalone: true,
  imports: [CommonModule, FormsModule, ToastModule],
  providers: [MessageService],
  templateUrl: './profile-form.component.html',
  styleUrls: ['./profile-form.component.scss']
})
export class ProfileFormComponent implements OnInit {
  nombre: string = '';
  apellido: string = '';
  telefono: string = '';
  
  userEmail: string | null = null;
  loading: boolean = false;
  
  constructor(
    private securityService: SecurityService,
    private userService: UserService,
    private messageService: MessageService
  ) {}
  
  ngOnInit(): void {
    this.securityService.getActualUser().subscribe({
      next: (user) => {
        if (user) {
          this.nombre = user.nombre || '';
          this.apellido = user.apellido || '';
          this.telefono = user.telefono || '';
          this.userEmail = user.email;
        }
      },
      error: (error) => {
        console.error('Error al cargar datos del usuario:', error);
      }
    });
  }
  
  updateProfile(): void {
    if (!this.userEmail) {
      console.error('No se puede actualizar el perfil: email de usuario no disponible');
      return;
    }
    
    const profileData: ModificarPerfilUsuario = {
      nombre: this.nombre,
      apellido: this.apellido,
      telefono: this.telefono
    };
    
    this.loading = true;
    
    this.userService.updateProfile(this.userEmail, profileData).subscribe({
      next: (updatedUser) => {
        console.log('Perfil actualizado correctamente');
        // Mostrar notificación de éxito
        this.messageService.add({
          severity: 'success',
          summary: 'Éxito',
          detail: 'Perfil actualizado correctamente',
          life: 3000
        });
        
        // Actualizar el usuario en el security service
        this.securityService.getActualUser().subscribe();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error al actualizar el perfil:', error);
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'No se pudo actualizar el perfil',
          life: 3000
        });
        this.loading = false;
      }
    });
  }
}