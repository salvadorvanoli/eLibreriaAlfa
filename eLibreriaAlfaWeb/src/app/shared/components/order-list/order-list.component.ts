import { Component, Input } from '@angular/core';
import { OrderListModule } from 'primeng/orderlist';

@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [
    OrderListModule
  ],
  templateUrl: './order-list.component.html',
  styleUrl: './order-list.component.scss'
})
export class OrderListComponent {
  @Input() items!: any[] | null;
  @Input() isSelectionDisabled: boolean = true;
  @Input() itemLabelField!: string;

  getItemLabel(item: any): string {
    return item[this.itemLabelField];
  }
}
