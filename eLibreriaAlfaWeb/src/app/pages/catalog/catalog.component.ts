import { Component } from '@angular/core';
import { CategoryTreeComponent } from '../../shared/components/category-tree/category-tree.component';
import { SearchBarComponent } from '../../shared/components/inputs/search-bar/search-bar.component';
import { ProductsCatalogComponent } from './components/products-catalog/products-catalog.component';
import { PaginatorComponent } from '../../shared/components/paginator/paginator.component';
import { ProductService } from '../../core/services/product.service';
import { Producto } from '../../core/models/producto';

@Component({
  selector: 'app-catalog',
  standalone: true,
  imports: [
    CategoryTreeComponent,
    SearchBarComponent,
    ProductsCatalogComponent,
    PaginatorComponent
  ],
  templateUrl: './catalog.component.html',
  styleUrl: './catalog.component.scss'
})
export class CatalogComponent {

  products: Producto[] = [];

  rowsPerPageOptions: number[] = [6, 12, 18];

  constructor(
    private productService: ProductService
  ) {}

  ngOnInit() {
    this.productService.getAll().subscribe((data) => {
      this.products = data;
    });
  }

}
