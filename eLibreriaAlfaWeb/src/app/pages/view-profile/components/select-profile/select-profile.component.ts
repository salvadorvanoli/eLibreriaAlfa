import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DropdownModule } from 'primeng/dropdown';

interface ProfileOption {
  label: string;
  value: string;
  icon: string;
}

@Component({
  selector: 'app-select-profile',
  standalone: true,
  imports: [
    CommonModule,
    DropdownModule,
    FormsModule
  ],
  templateUrl: './select-profile.component.html',
  styleUrl: './select-profile.component.scss'
})
export class SelectProfileComponent {
  @Output() optionSelected = new EventEmitter<string>();

  selectedOption: ProfileOption = { 
    label: 'Ver información personal', 
    value: 'info', 
    icon: 'pi pi-user' 
  };

  options: ProfileOption[] = [
    { label: 'Ver información personal', value: 'info', icon: 'pi pi-user' },
    { label: 'Ver pedido actual', value: 'actual', icon: 'pi pi-shopping-cart' },
    { label: 'Ver historial de pedidos', value: 'historial', icon: 'pi pi-history' }
  ];

  ngOnInit() {
    this.optionSelected.emit(this.selectedOption.value);
  }

  onSelectionChange(option: ProfileOption): void {
    this.optionSelected.emit(option.value);
  }
}
