import { Component, Input, SimpleChanges } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CardModule } from 'primeng/card';
import { ImageService } from '../../../../core/services/image.service';
import { ProductoSimpleDto } from '../../../../core/models/producto';

@Component({
  selector: 'app-product-card',
  standalone: true,
  imports: [
    RouterModule,
    CardModule
  ],
  templateUrl: './product-card.component.html',
  styleUrl: './product-card.component.scss'
})
export class ProductCardComponent {

  productImageUrl: string = 'https://developers.elementor.com/docs/assets/img/elementor-placeholder-image.png';

  @Input() product!: ProductoSimpleDto;

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
