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
        
      }
    });
  }

  onNombreChange(event: any): void {
    let value = event.target.value;
    value = value.replace(/[^a-zA-ZÁÉÍÓÚáéíóúÑñ\s]/g, '');
    if (value.length > 15) {
      value = value.substring(0, 15);
    }
    this.nombre = value;
    event.target.value = value;
  }

  onApellidoChange(event: any): void {
    let value = event.target.value;
    value = value.replace(/[^a-zA-ZÁÉÍÓÚáéíóúÑñ\s]/g, '');
    if (value.length > 15) {
      value = value.substring(0, 15);
    }
    this.apellido = value;
    event.target.value = value;
  }

  onTelefonoChange(event: any): void {
    let value = event.target.value;
    value = value.replace(/[^0-9]/g, '');
    if (value.length > 9) {
      value = value.substring(0, 9);
    }
    this.telefono = value;
    event.target.value = value;
  }

  isFormValid(): boolean {
    const nombreValid = this.nombre.trim().length > 0 && 
                       this.nombre.length <= 15 && 
                       /^[a-zA-ZÁÉÍÓÚáéíóúÑñ\s]+$/.test(this.nombre);
    
    const apellidoValid = this.apellido.trim().length > 0 && 
                         this.apellido.length <= 15 && 
                         /^[a-zA-ZÁÉÍÓÚáéíóúÑñ\s]+$/.test(this.apellido);
    
    const telefonoValid = this.telefono.length === 9 && 
                         /^[0-9]{9}$/.test(this.telefono);
    
    return nombreValid && apellidoValid && telefonoValid;
  }
  
  updateProfile(): void {
    if (!this.userEmail) {
      return;
    }

    if (!this.isFormValid()) {
      this.messageService.clear();
      this.messageService.add({
        severity: 'error',
        summary: 'Error de validación',
        detail: 'Por favor, completa correctamente todos los campos',
        life: 3000
      });
      return;
    }
    
    const profileData: ModificarPerfilUsuario = {
      nombre: this.nombre.trim(),
      apellido: this.apellido.trim(),
      telefono: this.telefono
    };
    
    this.loading = true;
    
    this.userService.updateProfile(this.userEmail, profileData).subscribe({
      next: (updatedUser) => {
        this.messageService.clear();
        this.messageService.add({
          severity: 'success',
          summary: 'Éxito',
          detail: 'Perfil actualizado correctamente',
          life: 3000
        });
        
        this.securityService.getActualUser().subscribe();
        this.loading = false;
      },
      error: (error) => {
        this.messageService.clear();
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