import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TitleComponent } from '../title/title.component';
import { OrderModalComponent } from '../order-modal/order-modal.component';
import { OrderService, EncargueEstado } from '../../../../core/services/order.services';
import { SecurityService } from '../../../../core/services/security.service';
import { catchError, finalize } from 'rxjs/operators';
import { of } from 'rxjs';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';

@Component({
  selector: 'app-order-submitted',
  standalone: true,
  imports: [
    CommonModule,
    OrderModalComponent,
    ToastModule
  ],
  providers: [MessageService],
  templateUrl: './order-submitted.component.html',
  styleUrls: ['./order-submitted.component.scss']
})
export class OrderSubmittedComponent implements OnInit {
  showModal: boolean = false;
  modalType: 'details' | 'cancel' = 'details';
  orderDetails: any = null;
  loading = false;
  encargueId: number | null = null;
  

  productos: any[] = [];
  totalRecords: number = 0;
  usuarioId: number | null = null;

  constructor(
    private orderService: OrderService,
    private securityService: SecurityService,
    private messageService: MessageService
  ) {}

  ngOnInit(): void {
    this.securityService.getActualUser().subscribe(usuario => {
      if (usuario && usuario.id) {
        this.usuarioId = usuario.id;
      }
    });
  }

  verPedido() {
    this.loading = true;
    this.modalType = 'details';
    
    this.securityService.getActualUser().pipe(
      catchError(error => {
        console.error('Error obteniendo usuario:', error);
        this.loading = false;
        return of(null);
      })
    ).subscribe(usuario => {
      if (usuario && usuario.id) {
        this.orderService.listarProductosEncarguePorUsuarioYEstado(
          usuario.id,
          EncargueEstado.PENDIENTE,
          0, 
          10 
        ).pipe(
          catchError(error => {
            console.error('Error cargando productos del encargue:', error);
            this.loading = false;
            return of({ content: [], totalElements: 0 });
          }),
          finalize(() => {
            this.loading = false;
          })
        ).subscribe(response => {
          this.productos = response.content;
          this.totalRecords = response.totalElements;
          
          if (this.productos.length > 0) {
            this.encargueId = this.productos[0].encargueId;
          }
          
          this.orderDetails = {
            productos: this.productos,
            totalRecords: this.totalRecords,
            encargueId: this.encargueId
          };
          
          this.showModal = true;
        });
      } else {
        this.loading = false;
      }
    });
  }

  cancelarPedido() {
    this.modalType = 'cancel';
    this.showModal = true;
  }

  onModalClose() {
    this.showModal = false;
  }

  onModalConfirm() {
    if (this.modalType === 'cancel') {
      if (!this.usuarioId) {
        this.messageService.clear();
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'No se pudo determinar el usuario actual'
        });
        return;
      }

      this.loading = true;
      this.orderService.cancelarEncargueEnviado(this.usuarioId)
        .pipe(
          catchError(error => {
            this.messageService.clear();
            this.messageService.add({
              severity: 'error',
              summary: 'Error',
              detail: 'Error al cancelar el pedido: ' + (error.message || 'Intente nuevamente')
            });
            return of(void 0);
          }),
          finalize(() => {
            this.loading = false;
          })
        )
        .subscribe(() => {
          this.messageService.clear();
          this.messageService.add({
            severity: 'success',
            summary: 'Éxito',
            detail: 'Pedido cancelado correctamente'
          });
          
          setTimeout(() => {
            window.location.reload();
          }, 1500);
        });
    }
    this.showModal = false;
  }
}