import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DatePickerComponent } from '../../../../shared/components/date-picker/date-picker.component';
import { DialogModule } from 'primeng/dialog';

@Component({
  selector: 'app-specify-order',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePickerComponent, DialogModule],
  templateUrl: './specify-order.component.html',
  styleUrls: ['./specify-order.component.scss']
})
export class SpecifyOrderComponent implements OnInit {
  @Output() submitOrder = new EventEmitter<string>();
  
  selectedDate: Date | null = null;
  showDateAlert: boolean = false;
  
  ngOnInit() {
    // Establecer una fecha predeterminada (mañana)
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    this.selectedDate = tomorrow;
  }
  
  onDateChange(event: Date) {
    console.log('Fecha seleccionada:', event);
    this.selectedDate = event;
  }
  
  clearDate() {
    this.selectedDate = null;
  }
  
  realizarPedido() {
    console.log('Fecha al realizar pedido:', this.selectedDate);
    
    if (!this.selectedDate) {
      this.showDateAlert = true;
      return;
    }
    
    // Formatear la fecha como YYYY-MM-DD para enviar al backend
    const day = this.selectedDate.getDate().toString().padStart(2, '0');
    const month = (this.selectedDate.getMonth() + 1).toString().padStart(2, '0');
    const year = this.selectedDate.getFullYear();
    
    const formattedDate = `${year}-${month}-${day}`;
    console.log('Fecha formateada:', formattedDate);
    
    // Emitir el evento con la fecha formateada
    this.submitOrder.emit(formattedDate);
  }
}