import { Component, Input, OnInit, EventEmitter, Output } from '@angular/core';
import { ImpresionService } from '../../../core/services/impresion.service';
import { SecurityService } from '../../../core/services/security.service';
import { TableModule } from 'primeng/table';
import { CommonModule } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { DropdownModule } from 'primeng/dropdown';
import { TagModule } from 'primeng/tag';
import { InputTextModule } from 'primeng/inputtext';
import { FormsModule } from '@angular/forms';
import { ToastModule } from 'primeng/toast';
import { TooltipModule } from 'primeng/tooltip';
import { DialogModule } from 'primeng/dialog';
import { MessageService } from 'primeng/api';
import { Impresion } from '../../../core/models/impresion';
import { DropdownFilterComponent } from '../dropdown-filter/dropdown-filter.component'; 

@Component({
  selector: 'app-prints-table',
  standalone: true,
  imports: [
    CommonModule,
    TableModule,
    ButtonModule,
    DropdownModule,
    TagModule,
    InputTextModule,
    FormsModule,
    ToastModule,
    TooltipModule,
    DialogModule,
    DropdownFilterComponent 
  ],
  providers: [MessageService],
  templateUrl: './prints-table.component.html',
  styleUrl: './prints-table.component.scss'
})
export class PrintsTableComponent implements OnInit {
    @Input() userRegistrado: boolean = false;
    @Output() impresionSelected = new EventEmitter<Impresion>();
    @Output() impresionDiscard = new EventEmitter<Impresion>();
    @Output() impresionEdit = new EventEmitter<Impresion>();
    
    impresiones: Impresion[] = [];
    filteredImpresiones: Impresion[] = []; 
    isLoading: boolean = false;
    
    estadoSeleccionado: string = ''; 
    
    showCancelDialog: boolean = false;
    selectedImpresionToCancel: Impresion | null = null;
    isCancelling: boolean = false;
    
    showDetailsDialog: boolean = false;
    selectedImpresionForDetails: Impresion | null = null;

    showEditStateDialog: boolean = false;
    selectedImpresionForEdit: Impresion | null = null;
    selectedNewState: string = '';
    isUpdatingState: boolean = false;

    estadosDisponibles = [
        { label: 'Pendiente', value: 'Pendiente' },
        { label: 'En proceso', value: 'En proceso' },
        { label: 'Completado', value: 'Completado' },
        { label: 'Entregado', value: 'Entregado' },
        { label: 'Cancelado', value: 'Cancelado' }
    ];

    constructor(
        private impresionService: ImpresionService,
        private securityService: SecurityService,
        private messageService: MessageService
    ) {}

    ngOnInit() {
        this.loadImpresiones();
    }

    filtrarImpresiones(): void {
        if (!this.estadoSeleccionado || this.estadoSeleccionado === '') {
            this.filteredImpresiones = [...this.impresiones];
        } else {
            this.filteredImpresiones = this.impresiones.filter(impresion => 
                impresion.estado?.toUpperCase() === this.estadoSeleccionado.toUpperCase()
            );
        }
    }

    onEstadoChange(): void {
        this.filtrarImpresiones();
    }

    loadImpresiones(): void {
        if (this.userRegistrado) {
            this.loadImpresionesByUser();
        } else {
            this.loadAllImpresiones();
        }
    }

    verDetalles(impresion: Impresion): void {
        this.selectedImpresionForDetails = impresion;
        this.showDetailsDialog = true;
    }

    descartarImpresion(impresion: Impresion): void {
        this.selectedImpresionToCancel = impresion;
        this.showCancelDialog = true;
    }

    editarEstado(impresion: Impresion): void {
        this.selectedImpresionForEdit = impresion;
        this.selectedNewState = impresion.estado;
        this.showEditStateDialog = true;
    }

    confirmarCambioEstado(): void {
        if (!this.selectedImpresionForEdit || !this.selectedNewState) return;

        this.isUpdatingState = true;

        this.impresionService.cambiarEstadoImpresion(
            this.selectedImpresionForEdit.id!, 
            this.selectedNewState
        ).subscribe({
            next: (response) => {
                this.messageService.add({
                    severity: 'success',
                    summary: 'Éxito',
                    detail: `Estado cambiado a "${this.selectedNewState}" correctamente`
                });
                
                this.loadImpresiones();
                this.cerrarModalEditarEstado();
            },
            error: (error) => {
                console.error('Error al cambiar estado:', error);
                this.messageService.add({
                    severity: 'error',
                    summary: 'Error',
                    detail: 'No se pudo cambiar el estado. Intente nuevamente.'
                });
                this.isUpdatingState = false;
            },
            complete: () => {
                this.isUpdatingState = false;
            }
        });
    }

    cerrarModalEditarEstado(): void {
        this.showEditStateDialog = false;
        this.selectedImpresionForEdit = null;
        this.selectedNewState = '';
        this.isUpdatingState = false;
    }

    confirmarCancelacion(): void {
        if (!this.selectedImpresionToCancel) return;

        this.isCancelling = true;
        
        this.impresionService.cambiarEstadoImpresion(
            this.selectedImpresionToCancel.id!, 
            'Cancelado'
        ).subscribe({
            next: (response) => {
                this.messageService.add({
                    severity: 'success',
                    summary: 'Éxito',
                    detail: 'Impresión cancelada correctamente'
                });
                
                this.loadImpresiones();
                this.cerrarModalCancelacion();
            },
            error: (error) => {
                console.error('Error al cancelar impresión:', error);
                this.messageService.add({
                    severity: 'error',
                    summary: 'Error',
                    detail: 'No se pudo cancelar la impresión. Intente nuevamente.'
                });
                this.isCancelling = false;
            },
            complete: () => {
                this.isCancelling = false;
            }
        });
    }

    cerrarModalCancelacion(): void {
        this.showCancelDialog = false;
        this.selectedImpresionToCancel = null;
        this.isCancelling = false;
    }

    cerrarModalDetalles(): void {
        this.showDetailsDialog = false;
        this.selectedImpresionForDetails = null;
    }

    getStatusSeverity(estado: string): "success" | "secondary" | "info" | "warning" | "danger" | "contrast" | undefined {
        switch (estado) {
            case 'Completado':
                return 'success';
            case 'En proceso':
                return 'secondary';
            case 'Pendiente':
                return 'warning';
            case 'Entregado':
                return 'info';
            case 'Cancelado':
                return 'danger';
            default:
                return 'secondary';
        }
    }

    getStatusIcon(estado: string): string {
        switch (estado) {
            case 'Completado':
                return 'pi pi-check-circle';
            case 'En proceso':
                return 'pi pi-spin pi-spinner';
            case 'Pendiente':
                return 'pi pi-clock';
            case 'Entregado':
                return 'pi pi-send';
            case 'Cancelado':
                return 'pi pi-times-circle';
            default:
                return 'pi pi-circle';
        }
    }

    descargarArchivo(impresion: Impresion): void {
        if (!impresion.nombreArchivo) {
            this.messageService.add({
                severity: 'warning',
                summary: 'Advertencia',
                detail: 'No hay archivo asociado a esta impresión'
            });
            return;
        }

        this.impresionService.downloadFile(impresion.nombreArchivo).subscribe({
            next: (blob) => {
                
                const url = window.URL.createObjectURL(blob);
                const link = document.createElement('a');
                link.href = url;
                
                link.download = impresion.nombreArchivo;
                
                document.body.appendChild(link);
                link.click();
                document.body.removeChild(link);
                
                window.URL.revokeObjectURL(url);
            },
            error: (error) => {
                console.error('❌ Error al descargar archivo:', error);
                this.messageService.add({
                    severity: 'error',
                    summary: 'Error',
                    detail: 'No se pudo descargar el archivo. Intente nuevamente.'
                });
            }
        });
    }

    formatFileName(filename: string | undefined): string {
        if (!filename) return 'Sin nombre';
        
        return filename.length > 37 ? filename.substring(37) : filename;
    }

    private loadImpresionesByUser(): void {
        this.isLoading = true;
        
        this.securityService.getActualUser().subscribe({
            next: (currentUser) => {
                if (currentUser?.id) {
                    this.impresionService.getImpresionesByUsuario(currentUser.id).subscribe({
                        next: (impresiones) => {
                            this.impresiones = impresiones;
                            this.filteredImpresiones = [...impresiones]; 
                            this.isLoading = false;
                        },
                        error: () => {
                            this.isLoading = false;
                        }
                    });
                } else {
                    this.isLoading = false;
                }
            },
            error: () => {
                this.isLoading = false;
            }
        });
    }

    private loadAllImpresiones(): void {
        this.isLoading = true;
        
        this.impresionService.getAllImpresiones().subscribe({
            next: (response) => {
                this.impresiones = response.impresiones || response || [];
                this.filteredImpresiones = [...this.impresiones]; 
                this.isLoading = false;
            },
            error: (error) => {
                console.error('Error al cargar todas las impresiones:', error);
                this.messageService.add({
                    severity: 'error',
                    summary: 'Error',
                    detail: 'No se pudieron cargar las impresiones.'
                });
                this.isLoading = false;
            }
        });
    }

    getClienteNombreCompleto(impresion: Impresion): string {
        if (impresion.usuario) {
            return `${impresion.usuario.nombre} ${impresion.usuario.apellido}`;
        }
        if (impresion.usuarioSimple) {
            return `${impresion.usuarioSimple.nombre} ${impresion.usuarioSimple.apellido}`;
        }
        return 'Usuario desconocido';
    }
}