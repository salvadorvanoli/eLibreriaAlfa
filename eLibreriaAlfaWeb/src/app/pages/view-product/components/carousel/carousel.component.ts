import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { ProductService } from '../../../../core/services/product.service'; 
import { CarouselModule } from 'primeng/carousel';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';
import { CommonModule } from '@angular/common';
import { ImageService } from '../../../../core/services/image.service';
import { ProductoDto } from '../../../../core/models/producto';

@Component({
    selector: 'app-carousel',
    templateUrl: './carousel.component.html',
    standalone: true,
    imports: [CarouselModule, ButtonModule, TagModule, CommonModule],
    providers: [ProductService]
})
export class CarouselComponent {

    productImagesUrls: string[] = [];

    responsiveOptions: any[] = [
        {
            breakpoint: '1400px',
            numVisible: 1,
            numScroll: 1,
        },
        {
            breakpoint: '1199px',
            numVisible: 1,
            numScroll: 1,
        },
        {
            breakpoint: '767px',
            numVisible: 1,
            numScroll: 1,
        }
    ];

    @Input() product!: ProductoDto | undefined;

    constructor(
        private imageService: ImageService
    ) {}

    ngOnChanges(changes: SimpleChanges) {
        if (changes['product']) {
            this.loadProductImages();
        }
    }

    loadProductImages() {
        if (this.product && this.product.imagenes && this.product.imagenes.length > 0) {
            this.productImagesUrls = this.product.imagenes.map(image => {
                return this.imageService.getImageUrl(image);
            });
        } else {
            this.productImagesUrls = [];
        }
    }


}