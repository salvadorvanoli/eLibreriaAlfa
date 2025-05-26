import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductContainerComponent } from '../product-container/product-container.component';
import { ProductCardComponent } from '../product-card/product-card.component';
import { OrderService, EncargueEstado } from '../../../../core/services/order.services';

@Component({
  selector: 'app-order-modal',
  standalone: true,
  imports: [CommonModule, ProductContainerComponent],
  templateUrl: './order-modal.component.html',
  styleUrls: ['./order-modal.component.scss']
})
export class OrderModalComponent {
  // Hacer accesible EncargueEstado en el template
  EncargueEstado = EncargueEstado;
  
  @Input() visible = false;
  @Input() modalType: 'details' | 'cancel' = 'details';
  @Input() orderDetails: any = {};
  @Input() loading = false;
  
  @Output() close = new EventEmitter<void>();
  @Output() confirm = new EventEmitter<void>();
  
  get productos(): any[] {
    return this.orderDetails?.productos || [];
  }
  
  get totalRecords(): number {
    return this.orderDetails?.totalRecords || 0;
  }
  
  get encargueId(): number | null {
    return this.orderDetails?.encargueId || null;
  }
  
  closeModal() {
    this.visible = false;
    this.close.emit();
  }
  
  confirmAction() {
    this.confirm.emit();
    this.closeModal();
  }
}
