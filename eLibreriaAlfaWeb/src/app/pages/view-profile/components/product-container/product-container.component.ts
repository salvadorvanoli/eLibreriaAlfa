import { Component, Input, OnInit, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductCardComponent } from '../product-card/product-card.component';
import { PaginatorComponent } from '../../../../shared/components/paginator/paginator.component';
import { OrderService, EncargueEstado } from '../../../../core/services/order.services';
import { SecurityService } from '../../../../core/services/security.service';
import { ImageService } from '../../../../core/services/image.service'; // ← Agregar import
import { ProductoEncargue } from '../../../../core/models/producto-encargue';
import { catchError, finalize, switchMap } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-product-container',
  standalone: true,
  imports: [CommonModule, ProductCardComponent, PaginatorComponent],
  templateUrl: './product-container.component.html',
  styleUrls: ['./product-container.component.scss']
})
export class ProductContainerComponent implements OnInit {
  @Input() readOnly = false; // Si es true, deshabilita la edición/eliminación
  @Output() noProductsLeft = new EventEmitter<void>();
  @Output() encargueIdChange = new EventEmitter<number>();
  
  @Input() productos: ProductoEncargue[] = [];
  @Input() totalRecords = 0;
  @Input() estadoEncargue: EncargueEstado = EncargueEstado.EN_CREACION;
  
  // ID del encargue actual
  encargueId: number | null = null;
  
  // Propiedades para el paginador
  first = 0;
  rows = 5;
  rowsPerPageOptions = [5, 10, 20];
  
  // Estado de carga
  loading = false;
  
  constructor(
    private orderService: OrderService,
    private securityService: SecurityService,
    private imageService: ImageService // ← Inyectar ImageService
  ) {}
  
  ngOnInit() {
    // Si se proporcionan productos desde afuera, no cargar datos
    if (this.productos.length === 0) {
      this.cargarProductos();
    } else {
      // Si hay productos proporcionados, extraer el encargueId
      if (this.productos.length > 0 && this.productos[0].encargueId) {
        this.encargueId = this.productos[0].encargueId;
        this.encargueIdChange.emit(this.encargueId);
      }
    }
  }
  
  cargarProductos() {
    this.loading = true;
    
    this.securityService.getActualUser().subscribe({
      next: (usuario) => {
        if (usuario && usuario.id) {
          
          const pagina = Math.floor(this.first / this.rows);
          
          console.log(`Cargando página ${pagina} con ${this.rows} elementos por página para el estado ${this.estadoEncargue}`);
          
          this.orderService.listarProductosEncarguePorUsuarioYEstado(
            usuario.id,
            this.estadoEncargue, // Usar el estado proporcionado
            pagina,
            this.rows
          ).subscribe({
            next: (response) => {
              this.productos = response.content;
              this.totalRecords = response.totalElements;
              console.log('Productos cargados:', this.productos);
              console.log('Total de registros:', this.totalRecords);
              
              // Intentar obtener el encargueId si hay productos
              if (this.productos.length > 0) {
                // Usar la propiedad encargueId directamente, según la interfaz
                this.encargueId = this.productos[0].encargueId;
                console.log('EncargueId obtenido:', this.encargueId);
                // Emitir el encargueId al componente padre
                if (this.encargueId) {
                  this.encargueIdChange.emit(this.encargueId);
                }
              }
              this.loading = false;
            },
            error: (error) => {
              console.error('Error al cargar productos:', error);
              this.productos = [];
              this.totalRecords = 0;
              this.loading = false;
            }
          });
        } else {
          this.loading = false;
        }
      },
      error: (error) => {
        console.error('Error al obtener usuario:', error);
        this.loading = false;
      }
    });
  }
  
  /**
   * Obtiene la URL completa de la imagen del producto usando ImageService
   */
  getProductImageUrl(imageName: string | null): string {
    if (!imageName) {
      return 'https://developers.elementor.com/docs/assets/img/elementor-placeholder-image.png';
    }
    return this.imageService.getImageUrl(imageName);
  }
  
  // Método para manejar el cambio de página
  onPageChange(event: any) {
    console.log('Evento de paginación:', event);
    this.first = event.first;
    this.rows = event.rows;
    
    // Solo recargar si no tenemos productos predefinidos
    if (this.productos.length === 0 || !this.readOnly) {
      this.cargarProductos();
    }
  }
  
  // Método para eliminar un producto
  removeProduct(productoId: number) {
    if (!this.encargueId) {
      console.error('No se puede eliminar el producto porque no se encontró el ID del encargue');
      return;
    }
    
    console.log(`Eliminando producto ${productoId} del encargue ${this.encargueId}`);
    this.loading = true;
    
    this.orderService.eliminarProductoDeEncargue(this.encargueId, productoId).subscribe({
      next: () => {
        console.log('Producto eliminado correctamente');
        // Recargar productos para reflejar los cambios
        this.securityService.getActualUser().subscribe({
          next: (usuario) => {
            if (usuario && usuario.id) {
              this.orderService.listarProductosEncarguePorUsuarioYEstado(
                usuario.id,
                EncargueEstado.EN_CREACION,
                0,
                1
              ).subscribe({
                next: (response) => {
                  if (response.totalElements === 0) {
                    // Si no quedan productos, emitir el evento
                    console.log('No quedan productos en el encargue, notificando...');
                    this.noProductsLeft.emit();
                  } else {
                    // Solo recarga la lista si todavía hay productos
                    this.cargarProductos();
                  }
                  this.loading = false;
                },
                error: (error) => {
                  console.error('Error verificando productos restantes:', error);
                  this.loading = false;
                }
              });
            } else {
              this.loading = false;
            }
          },
          error: (error) => {
            console.error('Error obteniendo usuario:', error);
            this.loading = false;
          }
        });
      },
      error: (error) => {
        console.error('Error al eliminar producto:', error);
        this.loading = false;
      }
    });
  }
}
