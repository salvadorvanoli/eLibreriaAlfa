import { Component, Input } from '@angular/core';
import { ProductCardComponent } from '../product-card/product-card.component';
import { Producto } from '../../../../core/models/producto';

@Component({
  selector: 'app-products-catalog',
  standalone: true,
  imports: [
    ProductCardComponent
  ],
  templateUrl: './products-catalog.component.html',
  styleUrl: './products-catalog.component.scss'
})
export class ProductsCatalogComponent {

  @Input() products!: Producto[];

  ngOnInit() {
    console.log(this.products);
  }
}
