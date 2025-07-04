import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ModalComponent } from '../../../../shared/components/modal/modal.component';
import { FormTextInputComponent } from "../../../../shared/components/inputs/form-text-input/form-text-input.component";
import { UserFormComponent } from "./components/user-form/user-form.component";
import { ProductFormComponent } from './components/product-form/product-form.component';
import { PublicationFormComponent } from './components/publication-form/publication-form.component';
import { CategoryFormComponent } from './components/category-form/category-form.component';

@Component({
  selector: 'app-control-panel-modal',
  standalone: true,
  imports: [
    ModalComponent,
    UserFormComponent,
    ProductFormComponent,
    PublicationFormComponent,
    CategoryFormComponent
],
  templateUrl: './control-panel-modal.component.html',
  styleUrl: './control-panel-modal.component.scss'
})
export class ControlPanelModalComponent {

  @Input() visible: boolean = false;
  @Input() dataType: string = '';
  @Input() item: any = null;
  @Input() title: string = '';
  @Input() breakpoints: any = {};
  @Input() classes: string = '';
  @Input() style: any = {};

  @Output() onClose = new EventEmitter<void>();
  @Output() reloadData = new EventEmitter<void>();
  @Output() itemDeleted = new EventEmitter<void>();

  onDataReloaded() {
    this.reloadData.emit();
  }

  onItemDeleted() {
    this.itemDeleted.emit();
  }
}
