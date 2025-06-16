import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from '../../core/services/product.service';
import { Producto } from '../../core/models/producto';
import { CarouselComponent } from './components/carousel/carousel.component';
import { ModalComponent } from '../../shared/components/modal/modal.component';
import { OrderService } from '../../core/services/order.services';
import { SecurityService } from '../../core/services/security.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-view-product',
  standalone: true,
  imports: [
    CommonModule, 
    CarouselComponent,
    ModalComponent,
    FormsModule
  ],
  templateUrl: './view-product.component.html',
  styleUrls: ['./view-product.component.scss']
})

export class ViewProductComponent implements OnInit {
  producto?: Producto;
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
    private securityService: SecurityService) {}

  ngOnInit() {
    // Obtener el ID del usuario actual
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
        next: (producto: Producto) => {
          this.producto = producto;
          this.loading = false;
        },
        error: error => {
          console.error('Error obteniendo el producto:', error);
          if (error.status === 404) {
            console.log('Producto no encontrado, redirigiendo al home...');
            this.router.navigate(['/']); // Redirige a la ruta raíz (home)
          } else {
            this.error = `Error al cargar el producto: ${error.message}`;
            this.loading = false;
          }
        }
      });
    });  
  }

  agregarAlPedido() {
    // Verificar si el usuario está autenticado
    if (!this.usuarioId) {
      this.router.navigate(['/login']);
      return;
    }

    if (!this.producto || this.addingToOrder) return;
    
    this.addingToOrder = true;

    // Crear el objeto producto_encargue según la estructura requerida
    const productoEncargue = {
      producto: { id: this.producto.id },
      cantidad: this.cantidad
    };

    // Llamar al servicio para agregar el producto al encargue
    this.orderService.agregarProductoAEncargue(this.usuarioId, productoEncargue).subscribe({
      next: () => {
        this.modalVisible = true;
        this.addingToOrder = false;
      },
      error: (error) => {
        console.error('Error al agregar el producto al pedido:', error);
        this.addingToOrder = false;
        // Aquí podrías mostrar un mensaje de error al usuario
      }
    });
  }

  irAVerPedido() {
    this.modalVisible = false;
    this.router.navigate(['/perfil']);  // Cambiado de '/pedido' a '/perfil'
  }
}