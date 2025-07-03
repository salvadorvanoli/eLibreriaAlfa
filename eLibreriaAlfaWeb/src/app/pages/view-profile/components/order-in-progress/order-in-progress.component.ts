import { Component, EventEmitter, Output, ViewChild } from '@angular/core';
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

  @ViewChild(ProductContainerComponent) productContainerComponent!: ProductContainerComponent;

  constructor(
    private orderService: OrderService,
    private securityService: SecurityService,
    private confirmationService: ConfirmationService,
    private messageService: MessageService,
    private imageService: ImageService 
  ) {}

  setEncargueId(id: number): void {
    this.encargueId = id;
  }

  onNoProductsLeft(): void {
    this.noProductsLeft.emit();
  }

  onSubmitOrder(fecha: string): void {
    if (!this.encargueId) {
      this.messageService.clear();
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: 'No se encontró el encargue a enviar.'
      });
      return;
    }

    // Verificar si todos los productos están deshabilitados
    const productosHabilitados = this.getProductosHabilitados();
    
    if (productosHabilitados.length === 0) {
      this.messageService.clear();
      this.messageService.add({
        severity: 'warn',
        summary: 'Productos Deshabilitados',
        detail: 'Todos los productos de su pedido han sido deshabilitados. No es posible procesar el pedido.'
      });
      return;
    }

    this.confirmationService.confirm({
      message: '¿Estás seguro de que deseas realizar este pedido?',
      header: 'Confirmación de Pedido',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.loading = true;
        
        this.orderService.marcarEncargueComoEnviado(this.encargueId!, fecha)
          .pipe(
            catchError(error => {
              this.messageService.clear();
              
              // Verificar si es error por productos deshabilitados del backend
              if (error.status === 400 && 
                  (error.error?.message?.includes('deshabilitado') || 
                   error.error?.message?.includes('inhabilitado') ||
                   error.error?.message?.includes('disabled'))) {
                this.messageService.add({
                  severity: 'warn',
                  summary: 'Productos Deshabilitados',
                  detail: 'Algunos productos de su pedido han sido deshabilitados recientemente. No es posible procesar el pedido.'
                });
              } else {
                this.messageService.add({
                  severity: 'error',
                  summary: 'Error',
                  detail: 'Error al enviar el pedido: ' + (error.message || 'Intente nuevamente')
                });
              }
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
              detail: 'Pedido enviado correctamente'
            });
            
            this.orderSubmitted.emit();
          });
      }
    });
  }

  getProductImageUrl(imageName: string): string {
    if (!imageName) {
      return 'https://developers.elementor.com/docs/assets/img/elementor-placeholder-image.png';
    }
    return this.imageService.getImageUrl(imageName);
  }

  // Método auxiliar para obtener productos habilitados
  private getProductosHabilitados(): any[] {
    // Acceder a los productos del ProductContainerComponent
    const productContainer = this.getProductContainer();
    if (!productContainer || !productContainer.productos) {
      return [];
    }
    
    return productContainer.productos.filter(producto => 
      producto.producto?.habilitado === true
    );
  }

  // Método para acceder al ProductContainerComponent
  private getProductContainer(): ProductContainerComponent | null {
    return this.productContainerComponent || null;
  }
}