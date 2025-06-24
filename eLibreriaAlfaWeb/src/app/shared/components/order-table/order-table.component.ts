import { Component, Input, OnInit, EventEmitter, Output } from '@angular/core';
import { OrderService } from '../../../core/services/order.services';
import { SecurityService } from '../../../core/services/security.service';
import { UserService } from '../../../core/services/user.service';
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
import { Encargue } from '../../../core/models/encargue';
import { DropdownFilterComponent } from '../dropdown-filter/dropdown-filter.component';

@Component({
  selector: 'app-order-table',
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
  templateUrl: './order-table.component.html',
  styleUrl: './order-table.component.scss'
})
export class OrderTableComponent implements OnInit {
    @Input() userRegistrado: boolean = false;
    @Output() orderSelected = new EventEmitter<Encargue>();
    @Output() orderDiscard = new EventEmitter<Encargue>();
    @Output() orderEdit = new EventEmitter<Encargue>();
    
    orders: Encargue[] = [];
    filteredOrders: Encargue[] = [];
    estadoSeleccionado: string = '';
    
    isLoading: boolean = false;
    
    showCancelDialog: boolean = false;
    selectedOrderToCancel: Encargue | null = null;
    isCancelling: boolean = false;
    
    showDetailsDialog: boolean = false;
    selectedOrderForDetails: Encargue | null = null;

    showEditStateDialog: boolean = false;
    selectedOrderForEdit: Encargue | null = null;
    selectedNewState: string = '';
    isUpdatingState: boolean = false;

    showConfirmEditStateDialog: boolean = false;

    estadosDisponibles = [
        { label: 'Pendiente', value: 'PENDIENTE' },
        { label: 'Entregado', value: 'ENTREGADO' },
        { label: 'Completado', value: 'COMPLETADO' },
        { label: 'Cancelado', value: 'CANCELADO' }
    ];

    estadosFiltro = [
        { label: 'Todos', value: 'TODOS' },
        { label: 'Pendiente', value: 'PENDIENTE' },
        { label: 'Entregado', value: 'ENTREGADO' },
        { label: 'Completado', value: 'COMPLETADO' },
        { label: 'Cancelado', value: 'CANCELADO' }
    ];
    
    constructor(
        private orderService: OrderService,
        private securityService: SecurityService,
        private messageService: MessageService,
        private userService: UserService 
    ) {}

    ngOnInit() {
        this.loadOrders();
    }

    filtrarOrdenes(): void {
        if (!this.estadoSeleccionado || this.estadoSeleccionado === '') {
            this.filteredOrders = [...this.orders];
        } else {
            this.filteredOrders = this.orders.filter(order => 
                order.estado?.toUpperCase() === this.estadoSeleccionado.toUpperCase()
            );
        }
    }

    onEstadoChange(): void {
        this.filtrarOrdenes();
    }

    loadOrders(): void {
        this.isLoading = true;
        this.securityService.getActualUser().subscribe({
            next: (currentUser) => {
                if (currentUser) {
                    if (this.userRegistrado) {
                        this.orderService.listarEncarguesFinalizadosPorUsuario(currentUser.id).subscribe({
                            next: (orders) => {
                                this.orders = orders
                                    .map((order: Encargue) => ({
                                        ...order,
                                        fecha: new Date(order.fecha)
                                    }))
                                    .sort((a: Encargue, b: Encargue) => a.fecha.getTime() - b.fecha.getTime());
                                this.filteredOrders = [...this.orders]; 
                                this.isLoading = false;
                            },
                            error: () => {
                                this.orders = [];
                                this.filteredOrders = []; 
                                this.isLoading = false;
                            }
                        });
                    } else if (!this.userRegistrado) {
                        this.orderService.getEncargues().subscribe({
                            next: (response) => {
                                const rawOrders = response.encargues || response.content || [];
                                this.orders = rawOrders
                                    .map((order: Encargue) => ({
                                        ...order,
                                        fecha: new Date(order.fecha)
                                    }))
                                    .sort((a: Encargue, b: Encargue) => a.fecha.getTime() - b.fecha.getTime());
                                this.filteredOrders = [...this.orders]; 
                                this.isLoading = false;
                            },
                            error: () => {
                                this.orders = [];
                                this.filteredOrders = [];
                                this.isLoading = false;
                            }
                        });
                    }
                }
            }
        });
    }

    verDetalles(order: Encargue): void {
        this.selectedOrderForDetails = order;
        this.showDetailsDialog = true;
    }

    descartarOrder(order: Encargue): void {
        this.selectedOrderToCancel = order;
        this.showCancelDialog = true;
    }

    getEstadosDisponibles(estadoActual: string) {
        switch (estadoActual) {
            case 'PENDIENTE':
                return [
                    { label: 'Cancelado', value: 'CANCELADO' },
                    { label: 'Completado', value: 'COMPLETADO' },
                    { label: 'Entregado', value: 'ENTREGADO' }
                ];
            case 'COMPLETADO':
                return [
                    { label: 'Cancelado', value: 'CANCELADO' },
                    { label: 'Entregado', value: 'ENTREGADO' }
                ];
            case 'CANCELADO':
            case 'ENTREGADO':
                return []; 
            default:
                return [
                    { label: 'Pendiente', value: 'PENDIENTE' },
                    { label: 'Entregado', value: 'ENTREGADO' },
                    { label: 'Completado', value: 'COMPLETADO' },
                    { label: 'Cancelado', value: 'CANCELADO' }
                ];
        }
    }

    puedeModificarEstado(estado: string): boolean {
        return estado !== 'CANCELADO' && estado !== 'ENTREGADO';
    }

    editarEstado(order: Encargue): void {
        if (!this.puedeModificarEstado(order.estado)) {
            this.messageService.add({
                severity: 'warn',
                summary: 'Acción no permitida',
                detail: 'No se puede modificar el estado de órdenes canceladas o entregadas',
                life: 3000
            });
            return;
        }

        this.selectedOrderForEdit = order;
        this.selectedNewState = '';
        this.showEditStateDialog = true;
    }

    confirmarCambioEstado(): void {
        this.showConfirmEditStateDialog = true;
    }

    realizarCambioEstado(): void {
        if (!this.selectedOrderForEdit || !this.selectedNewState) return;

        this.isUpdatingState = true;
        this.orderService.cambiarEstadoEncargue(this.selectedOrderForEdit.id, this.selectedNewState)
            .subscribe({
                next: (res) => {
                    this.messageService.add({
                        severity: 'success',
                        summary: 'Éxito',
                        detail: `Estado cambiado a "${this.selectedNewState}" correctamente`
                    });
                    this.loadOrders();
                    this.cerrarModalEditarEstado();
                    this.showConfirmEditStateDialog = false;
                },
                error: (err) => {
                    let errorMsg = 'No se pudo cambiar el estado';
                    if (err?.error) {
                        if (typeof err.error === 'string') {
                            errorMsg = err.error;
                        } else if (err.error.message) {
                            errorMsg = err.error.message;
                        }
                    }
                    this.messageService.add({
                        severity: 'error',
                        summary: 'Error',
                        detail: errorMsg
                    });
                    this.isUpdatingState = false;
                    this.showConfirmEditStateDialog = false;
                }
            });
    }

    cancelarCambioEstado(): void {
        this.showConfirmEditStateDialog = false;
    }

    cerrarModalEditarEstado(): void {
        this.showEditStateDialog = false;
        this.selectedOrderForEdit = null;
        this.selectedNewState = '';
        this.isUpdatingState = false;
    }

    confirmarCancelacion(): void {
        if (!this.selectedOrderToCancel) return;

        this.isCancelling = true;

        setTimeout(() => {
            this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'Orden cancelada correctamente'
            });
            
            this.loadOrders();
            this.cerrarModalCancelacion();
        }, 1000);
    }

    cerrarModalCancelacion(): void {
        this.showCancelDialog = false;
        this.selectedOrderToCancel = null;
        this.isCancelling = false;
    }

    cerrarModalDetalles(): void {
        this.showDetailsDialog = false;
        this.selectedOrderForDetails = null;
    }

    getStatusSeverity(estado: string): "success" | "secondary" | "info" | "warning" | "danger" | "contrast" | undefined {
        switch (estado) {
            case 'COMPLETADO':
                return 'success';
            case 'ENTREGADO':
                return 'info';
            case 'EN_CREACION':
                return 'warning';
            case 'PENDIENTE':
                return 'warning'; 
            case 'CANCELADO':
                return 'danger';
            default:
                return 'secondary';
        }
    }

    getStatusIcon(estado: string): string {
        switch (estado) {
            case 'COMPLETADO':
                return 'pi pi-check-circle';
            case 'ENTREGADO':
                return 'pi pi-box';
            case 'EN_CREACION':
                return 'pi pi-pencil';
            case 'PENDIENTE':
                return 'pi pi-clock';
            case 'CANCELADO':
                return 'pi pi-times-circle';
            default:
                return 'pi pi-circle';
        }
    }

    getClienteNombreCompleto(order: Encargue): string {
        if ((order as any).clienteNombreCompleto) {
            return (order as any).clienteNombreCompleto;
        }

        const userId = (order as any).idUsuarioComprador;
        
        if (userId) {
            this.userService.getUserById(userId).subscribe({
                next: (usuario) => {
                    const nombreCompleto = `${usuario.nombre || ''} ${usuario.apellido || ''}`.trim();
                    (order as any).clienteNombreCompleto = nombreCompleto || 'Sin nombre';
                },
                error: (error) => {
                    (order as any).clienteNombreCompleto = 'Error al cargar';
                }
            });
            
            return 'Cargando...';
        }

        return 'Usuario desconocido';
    }
}