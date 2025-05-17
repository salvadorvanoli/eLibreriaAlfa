import { Component } from '@angular/core';
import { CategoryTreeComponent } from '../../shared/components/category-tree/category-tree.component';
import { CatalogFilterControlsComponent } from "./components/catalog-filter-controls/catalog-filter-controls.component";
import { ProductsCatalogComponent } from './components/products-catalog/products-catalog.component';
import { PaginatorComponent } from '../../shared/components/paginator/paginator.component';
import { MessageComponent } from '../../shared/components/message/message.component';
import { ProductService } from '../../core/services/product.service';
import { Producto } from '../../core/models/producto';

@Component({
  selector: 'app-catalog',
  standalone: true,
  imports: [
    CategoryTreeComponent,
    CatalogFilterControlsComponent,
    ProductsCatalogComponent,
    PaginatorComponent,
    MessageComponent
  ],
  templateUrl: './catalog.component.html',
  styleUrl: './catalog.component.scss'
})
export class CatalogComponent {

  products!: Producto[];
  filteredProducts: Producto[] = [];


  rowsPerPageOptions: number[] = [6, 12, 18];

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
