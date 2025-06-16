import { Component, Input, Output, EventEmitter } from '@angular/core';
import { DropdownModule } from 'primeng/dropdown';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dropdown-filter',
  standalone: true,
  imports: [CommonModule, DropdownModule, FormsModule],
  templateUrl: './dropdown-filter.component.html',
  styleUrl: './dropdown-filter.component.scss'
})
export class DropdownFilterComponent {
  @Input() registeredUser: boolean = false;
  @Input() type: 'prints' | 'orders' = 'prints';
  @Input() selectedState: string = '';
  @Input() placeholder: string = 'Todos';

  @Output() selectedStateChange = new EventEmitter<string>(); 

  estadosPrints = [
    { label: 'Todos', value: '' },
    { label: 'Pendiente', value: 'PENDIENTE' },
    { label: 'Completado', value: 'COMPLETADO' },
    { label: 'Entregado', value: 'ENTREGADO' },
    { label: 'Cancelado', value: 'CANCELADO' }
  ];

  estadosOrders = [
    { label: 'Todos', value: '' },
    { label: 'Pendiente', value: 'PENDIENTE' },
    { label: 'Completado', value: 'COMPLETADO' },
    { label: 'Entregado', value: 'ENTREGADO' },
    { label: 'Cancelado', value: 'CANCELADO' }
  ];

  get estadosFiltro() {
    if (this.type === 'prints') {
      return this.estadosPrints;
    } else {
      if (this.registeredUser) {
        return this.estadosOrders.filter(e =>
          ['', 'COMPLETADO', 'ENTREGADO', 'CANCELADO'].includes(e.value)
        );
      } else {
        return this.estadosOrders;
      }
    }
  }

  onStateChange(value: string) {
    this.selectedState = value;
    this.selectedStateChange.emit(value);
  }
}
