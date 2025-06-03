import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DropdownModule } from 'primeng/dropdown';
import { SecurityService } from '../../../../core/services/security.service';
import { ImpresionService } from '../../../../core/services/impresion.service';
import { Impresion } from '../../../../core/models/impresion';
import { Subscription } from 'rxjs';

interface ImpresionDisplay extends Impresion {
  displayName: string;
}

@Component({
  selector: 'app-user-prints',
  standalone: true,
  imports: [CommonModule, FormsModule, DropdownModule],
  templateUrl: './user-prints.component.html',
  styleUrl: './user-prints.component.scss'
})
export class UserPrintsComponent implements OnInit, OnDestroy {
  selectedFile: ImpresionDisplay | null = null;
  files: ImpresionDisplay[] = [];
  isLoading: boolean = false;
  errorMessage: string = '';
  
  private impresionCreatedSubscription?: Subscription;

  constructor(
    private securityService: SecurityService,
    private impresionService: ImpresionService
  ) {}

  ngOnInit(): void {
    this.loadUserPrints();
    

    this.impresionCreatedSubscription = this.impresionService.impresionCreated$.subscribe(() => {
      console.log('Nueva impresión creada, recargando lista...');
      this.loadUserPrints();
    });
  }

  ngOnDestroy(): void {

    if (this.impresionCreatedSubscription) {
      this.impresionCreatedSubscription.unsubscribe();
    }
  }

  loadUserPrints(): void {
    this.securityService.getActualUser().subscribe({
      next: (currentUser) => {
        if (currentUser?.id) {
          this.isLoading = true;
          this.errorMessage = '';
          
          this.impresionService.getImpresionesByUsuario(currentUser.id).subscribe({
            next: (impresiones) => {
              console.log('=== DEBUG IMPRESIONES ===');
              console.log('impresiones raw:', impresiones);
              
              this.files = impresiones.map(imp => ({
                ...imp,
                displayName: this.getFileName(imp)
              }));
              this.isLoading = false;
              
              console.log('Lista actualizada con', this.files.length, 'impresiones');
            },
            error: (error) => {
              console.error('Error al cargar impresiones:', error);
              this.errorMessage = 'Error al cargar las impresiones';
              this.files = [];
              this.isLoading = false;
            }
          });
        } else {
          this.errorMessage = 'Usuario no autenticado';
          this.isLoading = false;
        }
      },
      error: (error) => {
        console.error('Error al obtener usuario:', error);
        this.errorMessage = 'Error al verificar autenticación';
        this.isLoading = false;
      }
    });
  }

  onFileSelected(file: ImpresionDisplay): void {
    this.selectedFile = file;
    console.log('Archivo seleccionado:', file);
  }

  getStatusClass(estado: string): string {
    switch (estado) {
      case 'Completado':
        return 'status-completado';
      case 'En proceso':
        return 'status-proceso';
      case 'Pendiente':
        return 'status-pendiente';
      default:
        return '';
    }
  }

  getStatusIcon(estado: string): string {
    switch (estado) {
      case 'Completado':
        return 'pi pi-check text-green-500 mr-2 text-lg'; 
      case 'En proceso':
        return 'pi pi-refresh text-yellow-500 mr-2 text-lg';   
      case 'Pendiente':
        return 'pi pi-times text-red-500 mr-2 text-lg'; 
      default:
        return 'pi pi-circle text-gray-500 mr-2 text-lg';
    }
  }

  getColorText(color: boolean): string {
    return color ? 'Color' : 'Blanco y Negro';
  }

  getFileName(impresion: Impresion): string {
    return impresion.nombreArchivo || `Impresión_${impresion.id}`;
  }
}
