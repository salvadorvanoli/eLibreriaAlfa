import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ButtonModule, ButtonSeverity } from 'primeng/button';

@Component({
  selector: 'app-primary-button',
  standalone: true,
  imports: [
    ButtonModule
  ],
  templateUrl: './primary-button.component.html',
  styleUrl: './primary-button.component.scss'
})
export class PrimaryButtonComponent {
  @Input() label: string = '';
  @Input() icon: string = '';
  @Input() classes: string = '';
  @Input() severity: ButtonSeverity = 'primary';

  @Output() onClick = new EventEmitter<Event>();

  handleClick(event: Event) {
    this.onClick.emit(event);
  }
}
