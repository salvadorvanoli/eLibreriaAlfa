import { Component, computed, EventEmitter, Input, Output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { InputTextModule } from 'primeng/inputtext';
import { Message } from 'primeng/message';

@Component({
  selector: 'app-form-select-input',
  standalone: true,
  imports: [
    CommonModule, 
    FormsModule,
    ReactiveFormsModule,
    InputTextModule,
    Message
],
  templateUrl: './form-select-input.component.html',
  styleUrls: ['./form-select-input.component.scss']
})
export class FormSelectInputComponent {

  value: string | null = '';

  @Input() options: { label: string, value: any }[] = [];
  @Input() placeholder: string = '';
  @Input() errorMessage: string = "";
  @Input() formSubmitted = signal(false);

  @Output() textValue = new EventEmitter<string>();
  @Output() isInputInvalid = new EventEmitter<boolean>();

  onValueChange(event: any) {
    this.textValue.emit(event);
    this.validateText();
  }

  showErrorMessage = computed(() => {
    return this.validateText() && this.formSubmitted();
  });

  validateText() {
    const isInvalid = !this.value || this.value === '';
    this.isInputInvalid.emit(isInvalid);
    return isInvalid;
  }

  setValue(value: string) {
    this.value = value;
    this.textValue.emit(value);
  }

  reset() {
    this.value = '';
    this.textValue.emit('');
    this.isInputInvalid.emit(false);
  }
}