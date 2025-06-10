import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { Tag } from 'primeng/tag';
import { PrimaryButtonComponent } from '../../../../shared/components/buttons/primary-button/primary-button.component';
import { ElementoLista } from '../../../../core/models/elemento-lista';

@Component({
  selector: 'app-item-row',
  standalone: true,
  imports: [
    CommonModule,
    ButtonModule,
    Tag,
    PrimaryButtonComponent
  ],
  templateUrl: './item-row.component.html',
  styleUrl: './item-row.component.scss'
})
export class ItemRowComponent {

  @Input() item!: ElementoLista;
  @Input() first!: boolean;

  @Output() details = new EventEmitter<ElementoLista>();

}
