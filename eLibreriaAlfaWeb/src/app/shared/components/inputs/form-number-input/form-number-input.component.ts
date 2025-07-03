import { Component, Input, Output, EventEmitter, signal, computed } from '@angular/core';
import { InputNumber } from 'primeng/inputnumber';
import { FormsModule } from '@angular/forms';
import { Message } from 'primeng/message';
import { FloatLabel } from 'primeng/floatlabel';

@Component({
  selector: 'app-form-number-input',
  standalone: true,
  imports: [
    InputNumber,
    FormsModule,
    FloatLabel,
    Message
  ],
  templateUrl: './form-number-input.component.html',
  styleUrl: './form-number-input.component.scss'
})
export class FormNumberInputComponent {

  value = signal<number | null>(null);
  
  @Input() placeholder: string = "";
  @Input() min: number = 0;
  @Input() max: number | null = null;
  @Input() errorMessage: string = "";
  @Input() formSubmitted = signal(false);

  @Output() inputValue = new EventEmitter<number | null>();
  @Output() isInputInvalid = new EventEmitter<boolean>();

  onValueChange(event: any) {
    this.inputValue.emit(event);
    this.validateValue();
  }

  showErrorMessage = computed(() => {
    return this.validateValue() && this.formSubmitted();
  });

  validateValue() {
    const value = this.value() ? this.value()! : 0;
    const isInvalid = this.value() === null || (this.max !== null && value > this.max) || value < this.min;
    this.isInputInvalid.emit(isInvalid);
    return isInvalid;
  }

  setValue(newValue: number) {
    this.value.set(newValue);
    this.inputValue.emit(newValue);
  }

  reset() {
    this.value.set(null);
    this.isInputInvalid.emit(false);
    this.inputValue.emit(null);
  }
}
