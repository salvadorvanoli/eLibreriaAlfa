import { Component, EventEmitter, Input, Output, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { PrimaryButtonComponent } from '../../../../shared/components/buttons/primary-button/primary-button.component';
import { ImageService } from '../../../../core/services/image.service';
import { CategoriaSimpleDto } from '../../../../core/models/categoria';

@Component({
  selector: 'app-item-row',
  standalone: true,
  imports: [
    CommonModule,
    ButtonModule,
    PrimaryButtonComponent
  ],
  templateUrl: './item-row.component.html',
  styleUrl: './item-row.component.scss'
})
export class ItemRowComponent {
  @Input() item!: any;
  @Input() itemType!: string;
  @Input() first!: boolean;

  @Output() details = new EventEmitter<any>();

  constructor(
    private imageService: ImageService
  ) {}

  get itemImage(): string | null {
    try {
      switch(this.itemType) {
        case 'Producto':
          if (!this.item.imagenes || this.item.imagenes.length === 0)
            return null;
          const firstNonEmptyImageRelativePath = this.item.imagenes.find((img: string) => img && img.trim() !== '');
          return this.imageService.getImageUrl(firstNonEmptyImageRelativePath);
        case 'Publicación':
          if (!this.item.imagenUrl || this.item.imagenUrl.trim() === '')
            return null;
          return this.imageService.getImageUrl(this.item.imagenUrl);
        case 'Usuario':
        default:
          return null;
      }
    } catch (error) {
      return 'https://developers.elementor.com/docs/assets/img/elementor-placeholder-image.png';
    }
  }

  get itemUpperSubtitle(): string {
    switch(this.itemType) {
      case 'Usuario':
        return this.item.rol;
      case 'Producto':
        if (!this.item.categorias || this.item.categorias.length === 0)
          return 'Sin categorías';
        const concatenatedCategories = this.item.categorias.map((c: CategoriaSimpleDto) => c.nombre).join(' - ');
        return concatenatedCategories;
      case 'Publicación':
      default:
        return '';
    }
  }

  get itemTitle(): string {
    switch(this.itemType) {
      case 'Usuario':
        return this.item.nombre;
      case 'Producto':
        return this.item.nombre;
      case 'Publicación':
        return this.item.titulo;
      default:
        return '';
    }
  }

  get itemLowerSubtitle(): string {
    switch(this.itemType) {
      case 'Usuario':
        const fullname = this.item.nombre + ' ' + this.item.apellido;
        if (fullname.trim() == '')
          return "Sin nombre completo";
        return fullname;
      case 'Publicación':
        const commentsCount = this.item.comentarios ? this.item.comentarios.length : 0;
        return `${commentsCount} comentario${commentsCount !== 1 ? 's' : ''}`;
      case 'Producto':
      default:
        return '';
    }
  }

  get itemEndSubtitle(): string {
    switch(this.itemType) {
      case 'Usuario':
        const telephone = this.item.telefono;
        if (telephone == null || telephone == '')
          return "Sin teléfono"
        return this.item.telefono;
      case 'Producto':
        return this.item.precio ? `$${this.item.precio}` : '';
      case 'Publicación':
        const date = new Date(this.item.fechaCreacion);
        const options: Intl.DateTimeFormatOptions = {
          year: 'numeric',
          month: 'long',
          day: 'numeric',
          hour: '2-digit',
          minute: '2-digit',
          second: '2-digit'
        };
        const formattedDate = date.toLocaleDateString('es-ES', options);

        return formattedDate;
      default:
        return '';
    }
  }

  /*isProductoHabilitado(): boolean {
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
*/
}
