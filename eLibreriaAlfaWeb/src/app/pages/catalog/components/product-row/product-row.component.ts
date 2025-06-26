import { Component, Input, SimpleChanges } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ImageService } from '../../../../core/services/image.service';
import { ProductoSimpleDto } from '../../../../core/models/producto';

@Component({
  selector: 'app-product-row',
  standalone: true,
  imports: [
    RouterModule,
    CommonModule
  ],
  templateUrl: './product-row.component.html',
  styleUrl: './product-row.component.scss'
})
export class ProductRowComponent {

  productImageUrl: string = 'https://developers.elementor.com/docs/assets/img/elementor-placeholder-image.png';

  @Input() product!: ProductoSimpleDto;
  @Input() first!: boolean;

  constructor(
    private imageService: ImageService
  ) {}

  ngOnChanges(changes: SimpleChanges) {
    if (changes['product']) {
      if (this.product && this.product.imagenes && this.product.imagenes.length > 0) {
        this.productImageUrl = this.imageService.getImageUrl(this.product.imagenes[0]);
      }
    }
  }

  onImageError(event: any): void {
    event.target.src = this.productImageUrl = 'https://developers.elementor.com/docs/assets/img/elementor-placeholder-image.png';
  }
}
