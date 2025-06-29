import { Component, EventEmitter, Input, Output, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { Tag } from 'primeng/tag';
import { PrimaryButtonComponent } from '../../../../shared/components/buttons/primary-button/primary-button.component';
import { ImageService } from '../../../../core/services/image.service';
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
    itemImageUrl: string = 'https://developers.elementor.com/docs/assets/img/elementor-placeholder-image.png';
    
    @Input() itemType?: string;
    @Input() item!: ElementoLista;
    @Input() first!: boolean;

    @Output() details = new EventEmitter<ElementoLista>();
    @Output() actionExecuted = new EventEmitter<{action: string, item: ElementoLista}>();

    constructor(private imageService: ImageService) {}

    ngOnChanges(changes: SimpleChanges) {
        if (changes['item'] && this.item?.imagen) {
            this.itemImageUrl = this.imageService.getImageUrl(this.item.imagen);
        }
    }

    isProductoHabilitado(): boolean {
        return this.itemType === 'Producto' ? this.item.habilitado : true;
    }

    descartarOInhabilitar(): void {
        if (this.itemType === 'Publicación') {
            this.actionExecuted.emit({
                action: 'eliminar',
                item: this.item
            });
        } else if (this.itemType === 'Producto') {
            this.actionExecuted.emit({
                action: 'inhabilitar',
                item: this.item
            });
        }
    }

    habilitarProducto(): void {
        this.actionExecuted.emit({
            action: 'habilitar',
            item: this.item
        });
    }

}
