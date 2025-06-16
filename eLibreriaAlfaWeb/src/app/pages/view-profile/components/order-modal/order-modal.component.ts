import { Component, EventEmitter, Input, Output, OnChanges, SimpleChanges, OnDestroy } from '@angular/core';
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
export class OrderModalComponent implements OnChanges, OnDestroy {
  EncargueEstado = EncargueEstado;
  
  @Input() visible: boolean = false;
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
  
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['visible']) {
      if (this.visible) {
        document.body.style.overflow = 'hidden';
      } else {
        document.body.style.overflow = 'auto';
      }
    }
  }
  
  closeModal(): void {
    document.body.style.overflow = 'auto';
    this.visible = false;
    this.close.emit();
  }
  
  confirmAction(): void {
    this.confirm.emit();
    this.closeModal();
  }

  ngOnDestroy(): void {
    document.body.style.overflow = 'auto';
  }
}
