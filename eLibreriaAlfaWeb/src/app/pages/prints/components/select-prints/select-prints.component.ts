import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DropdownModule } from 'primeng/dropdown';
import { FormsModule } from '@angular/forms';

interface PrintOption {
  label: string;
  value: string;
  icon: string;
}

@Component({
  selector: 'app-select-prints',
  standalone: true,
  imports: [
    CommonModule,
    DropdownModule,
    FormsModule
  ],
  templateUrl: './select-prints.component.html',
  styleUrl: './select-prints.component.scss'
})
export class SelectPrintsComponent {
  @Output() optionSelected = new EventEmitter<string>();

  selectedOption: PrintOption = { label: 'Ver mis impresiones', value: 'ver', icon: 'pi pi-list' };

  options: PrintOption[] = [
    { label: 'Solicitar impresión', value: 'solicitar', icon: 'pi pi-plus' },
    { label: 'Ver mis impresiones', value: 'ver', icon: 'pi pi-list' }
  ];

  ngOnInit() {
    this.optionSelected.emit(this.selectedOption.value);
  }

  onSelectionChange(option: PrintOption): void {
    this.optionSelected.emit(option.value);
  }
}
