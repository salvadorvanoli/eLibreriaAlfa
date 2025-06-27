import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from '../../core/services/product.service';
import { ProductoDto } from '../../core/models/producto';
import { CarouselComponent } from './components/carousel/carousel.component';
import { ModalComponent } from '../../shared/components/modal/modal.component';
import { OrderService } from '../../core/services/order.services';
import { SecurityService } from '../../core/services/security.service';
import { FormsModule } from '@angular/forms';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-view-product',
  standalone: true,
  imports: [
    CommonModule, 
    CarouselComponent,
    ModalComponent,
    FormsModule,
    ToastModule
  ],
  providers: [MessageService],
  templateUrl: './view-product.component.html',
  styleUrls: ['./view-product.component.scss']
})

export class ViewProductComponent implements OnInit {
  producto?: ProductoDto;
  loading = true;
  error?: string;
  modalVisible = false;
  cantidad: number = 1;
  usuarioId?: number;
  addingToOrder = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private productService: ProductService,
    private orderService: OrderService,
    private securityService: SecurityService,
    private messageService: MessageService) {}

  ngOnInit() {
    this.securityService.getActualUser().subscribe({
      next: (usuario) => {
        if (usuario) {
          this.usuarioId = usuario.id;
        }
      },
      error: (error) => {
        console.error('Error al obtener el usuario actual:', error);
      }
    });

    this.route.params.subscribe(params => {
      const id = Number(params['id']);
      if (!id || isNaN(id)) {
        console.error('ID inválido:', params['id']);
        this.router.navigate(['/']);
        return;
      }
      this.productService.getById(id).subscribe({
        next: (producto: ProductoDto) => {
          this.producto = producto;
          this.loading = false;
        },
        error: error => {
          console.error('Error obteniendo el producto:', error);
          if (error.status === 404) {
            this.router.navigate(['/']); 
          } else {
            this.error = `Error al cargar el producto: ${error.message}`;
            this.loading = false;
          }
        }
      });
    });  
  }

  agregarAlPedido() {
    if (!this.usuarioId) {
      this.router.navigate(['/login']);
      return;
    }

    if (!this.producto || this.addingToOrder) return;
    
    this.addingToOrder = true;

    this.orderService.usuarioTieneEncargueEnCreacion(this.usuarioId).subscribe({
      next: (tieneEncargue) => {
        if (!tieneEncargue) {
          this.addingToOrder = false;
          this.messageService.clear();
          this.messageService.add({
            severity: 'warn',
            summary: 'Encargue no disponible',
            detail: 'No tienes un encargue en creación. Espere a que su pedido sea completado o cancele el actual.',
            life: 5000
          });
          return;
        }

        const productoEncargue = {
          producto: { id: this.producto!.id },
          cantidad: this.cantidad
        };

        this.orderService.agregarProductoAEncargue(this.usuarioId!, productoEncargue).subscribe({
          next: () => {
            this.modalVisible = true;
            this.addingToOrder = false;
            this.messageService.clear();
            this.messageService.add({
              severity: 'success',
              summary: 'Producto agregado',
              detail: 'El producto se agregó correctamente a tu pedido.',
              life: 3000
            });
          },
          error: (error) => {
            console.error('Error al agregar el producto al pedido:', error);
            this.addingToOrder = false;
            this.messageService.clear();
            if (error.status === 500) {
              this.messageService.add({
                severity: 'info',
                summary: 'Producto duplicado',
                detail: 'Ya tiene este producto en su pedido.',
                life: 4000
              });
            } else {
              this.messageService.add({
                severity: 'error',
                summary: 'Error',
                detail: 'Error al agregar el producto al pedido. Inténtalo nuevamente.',
                life: 5000
              });
            }
          }
        });
      },
      error: (error) => {
        console.error('Error al verificar encargue en creación:', error);
        this.addingToOrder = false;
        this.messageService.clear();
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Error al verificar el estado del encargue. Inténtalo nuevamente.',
          life: 5000
        });
      }
    });
  }

irAVerPedido() {
  this.modalVisible = false;
  this.router.navigate(['/perfil'], { queryParams: { section: 'actual' } });
}

}