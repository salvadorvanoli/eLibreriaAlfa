import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ModalComponent } from '../../../../../shared/components/modal/modal.component';
import { FormTextInputComponent } from "../../../../../shared/components/inputs/form-text-input/form-text-input.component";
import { UserModalFormComponent } from "./components/user-modal-form/user-modal-form.component";

@Component({
  selector: 'app-user-modal',
  standalone: true,
  imports: [
    ModalComponent,
    UserModalFormComponent
],
  templateUrl: './user-modal.component.html',
  styleUrl: './user-modal.component.scss'
})
export class UserModalComponent {

  @Input() visible: boolean = false;
  @Input() title: string = '';
  @Input() breakpoints: any = {};
  @Input() classes: string = '';
  @Input() style: any = {};

  @Output() onClose = new EventEmitter<void>();

}
