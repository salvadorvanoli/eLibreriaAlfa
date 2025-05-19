import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

interface Product {
  id: number;
  title: string;
  price: number;
  quantity: number;
  imageUrl: string;
}

@Component({
  selector: 'app-product-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './product-card.component.html',
  styles: [] 
})
export class ProductCardComponent {
  @Input() product: Product | undefined;
  @Input() readOnly = false; // Si es true, deshabilita la edición/eliminación
  @Output() removeProduct = new EventEmitter<number>();

  onRemove(): void {
    if (this.product) {
      this.removeProduct.emit(this.product.id);
    }
  }
  
  // Método para calcular el subtotal (precio × cantidad)
  get subtotal(): number {
    if (this.product) {
      return this.product.price * this.product.quantity;
    }
    return 0;
  }
}
