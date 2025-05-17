import { Component } from '@angular/core';
import { CategoryTreeComponent } from '../../shared/components/category-tree/category-tree.component';
import { ProductsCatalogComponent } from './components/products-catalog/products-catalog.component';
import { MessageComponent } from '../../shared/components/message/message.component';
import { ProductService } from '../../core/services/product.service';
import { Producto } from '../../core/models/producto';

@Component({
  selector: 'app-catalog',
  standalone: true,
  imports: [
    CategoryTreeComponent,
    ProductsCatalogComponent,
    MessageComponent
  ],
  templateUrl: './catalog.component.html',
  styleUrl: './catalog.component.scss'
})
export class CatalogComponent {

  products!: Producto[];
  filteredProducts: Producto[] = [];

  constructor(
    private productService: ProductService
  ) {}

  ngOnInit() {
    this.productService.getAll().subscribe((data) => {
      this.products = data;
      this.filteredProducts = data;
    });
  }

}
