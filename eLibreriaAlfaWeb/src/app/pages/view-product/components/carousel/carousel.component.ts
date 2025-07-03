import { Component, Input, OnInit, SimpleChanges, ChangeDetectorRef } from '@angular/core';
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
        private imageService: ImageService,
        private cdr: ChangeDetectorRef
    ) {}

    ngOnChanges(changes: SimpleChanges) {
        if (changes['product']) {
            this.loadProductImages();
        }
    }

    loadProductImages() {
        const placeholder = 'https://developers.elementor.com/docs/assets/img/elementor-placeholder-image.png';
        if (this.product && this.product.imagenes && this.product.imagenes.length > 0) {
            const urls = this.product.imagenes
                .filter(img => !!img)
                .map(image => this.imageService.getImageUrl(image));
            const validUrls: string[] = [];
            let checked = 0;

            urls.forEach((url, idx) => {
                const img = new window.Image();
                img.onload = () => {
                    validUrls[idx] = url;
                    checked++;
                    if (checked === urls.length) {
                        this.productImagesUrls = validUrls.filter(Boolean).length > 0 ? validUrls.filter(Boolean) : [placeholder];
                        this.cdr.detectChanges();
                    }
                };
                img.onerror = () => {
                    validUrls[idx] = '';
                    checked++;
                    if (checked === urls.length) {
                        this.productImagesUrls = validUrls.filter(Boolean).length > 0 ? validUrls.filter(Boolean) : [placeholder];
                        this.cdr.detectChanges();
                    }
                };
                img.src = url;
            });

            if (urls.length === 0) {
                this.productImagesUrls = [placeholder];
            }
        } else {
            this.productImagesUrls = [placeholder];
        }
    }


}