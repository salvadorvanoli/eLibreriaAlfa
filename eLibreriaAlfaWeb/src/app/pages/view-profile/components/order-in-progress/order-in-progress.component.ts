import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductContainerComponent } from '../product-container/product-container.component';
import { SpecifyOrderComponent } from '../specify-order/specify-order.component';
import { TitleComponent } from '../title/title.component';
import { OrderService } from '../../../../core/services/order.services';
import { SecurityService } from '../../../../core/services/security.service';
import { catchError, finalize, of } from 'rxjs';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ToastModule } from 'primeng/toast';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ImageService } from '../../../../core/services/image.service'; // ← Agregar si necesitas imágenes

@Component({
  selector: 'app-order-in-progress',
  standalone: true,
  imports: [
    CommonModule, 
    TitleComponent, 
    ProductContainerComponent, 
    SpecifyOrderComponent,
    ConfirmDialogModule,
    ToastModule
  ],
  providers: [ConfirmationService, MessageService],
  templateUrl: './order-in-progress.component.html',
  styleUrls: ['./order-in-progress.component.scss']
})
export class OrderInProgressComponent {
  loading = false;
  encargueId: number | null = null;
  
  @Output() noProductsLeft = new EventEmitter<void>();
  @Output() orderSubmitted = new EventEmitter<void>();

  constructor(
    private orderService: OrderService,
    private securityService: SecurityService,
    private confirmationService: ConfirmationService,
    private messageService: MessageService,
    private imageService: ImageService // ← Inyectar si necesitas imágenes
  ) {}

  setEncargueId(id: number): void {
    this.encargueId = id;
    console.log('OrderInProgressComponent: Recibido encargueId:', id);
  }

  onNoProductsLeft(): void {
    console.log('OrderInProgressComponent: No quedan productos, notificando al componente padre');
    this.noProductsLeft.emit();
  }

  // Este método se llama cuando se hace clic en "Realizar pedido"
  onSubmitOrder(fecha: string): void {
    if (!this.encargueId) {
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: 'No se encontró el encargue a enviar.'
      });
      return;
    }

    // Mostrar diálogo de confirmación
    this.confirmationService.confirm({
      message: '¿Estás seguro de que deseas realizar este pedido?',
      header: 'Confirmación de Pedido',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.loading = true;
        
        // Llamar al servicio para marcar el encargue como enviado
        this.orderService.marcarEncargueComoEnviado(this.encargueId!, fecha)
          .pipe(
            catchError(error => {
              this.messageService.add({
                severity: 'error',
                summary: 'Error',
                detail: 'Error al enviar el pedido: ' + (error.message || 'Intente nuevamente')
              });
              return of(void 0); 
            }),
            finalize(() => {
              this.loading = false;
            })
          )
          .subscribe(() => {
            this.messageService.add({
              severity: 'success',
              summary: 'Éxito',
              detail: 'Pedido enviado correctamente'
            });
            
            // Emitir evento para actualizar la vista
            this.orderSubmitted.emit();
          });
      }
    });
  }

  /**
   * Método para obtener URL de imagen si lo necesitas
   */
  getProductImageUrl(imageName: string): string {
    if (!imageName) {
      return 'https://developers.elementor.com/docs/assets/img/elementor-placeholder-image.png';
    }
    return this.imageService.getImageUrl(imageName);
  }
}