import { Component, signal } from '@angular/core';
import { MessageService } from 'primeng/api';
import { Toast } from 'primeng/toast';
import { CategoryTreeComponent } from '../../shared/components/category-tree/category-tree.component';
import { ProductsCatalogComponent } from './components/products-catalog/products-catalog.component';
import { MessageComponent } from '../../shared/components/message/message.component';
import { ProductService } from '../../core/services/product.service';
import { ProductoSimpleDto } from '../../core/models/producto';

@Component({
  selector: 'app-catalog',
  standalone: true,
  imports: [
    Toast,
    CategoryTreeComponent,
    ProductsCatalogComponent,
    MessageComponent
  ],
  providers: [
    MessageService
  ],
  templateUrl: './catalog.component.html',
  styleUrl: './catalog.component.scss'
})
export class CatalogComponent {

  products!: ProductoSimpleDto[];
  selectedCategory: number = 0;
  searchText: string = '';
  order: string = "";

  constructor(
    private messageService: MessageService,
    private productService: ProductService
  ) {}

  ngOnInit() {
    this.getProducts();
  }

  getProducts(): void {
    this.productService.getAll().subscribe((data) => {
      this.products = data;
    });
  }
  
  filterProducts() {
    this.productService.getCatalogFiltered(
      this.selectedCategory,
      this.searchText,
      this.order
    ).subscribe({
      next: (response: any) => {
        if (response.length > 0) {
          this.products = response;
        } else {
          this.messageService.clear();
          this.messageService.add({ severity: 'error', summary: 'Error', detail: "No se encontraron productos acordes los filtros especificados", life: 4000 });
        }
      },
      error: (error: any) => {
        console.error('Error fetching filtered products:', error);
      }
    });
  }

  onCategorySelection(category: number | number[]) {
    this.selectedCategory = category as number;
    this.filterProducts();
  }

  onSearchTextChange(searchText: string) {
    this.searchText = searchText;
  }

  onSortChange(order: string) {
    this.order = order;
    this.filterProducts();
  }

  applyFilters() {
    this.filterProducts();
  }

  resetFilters() {
    this.searchText = '';
    this.order = '';
    this.filterProducts();
  }

}
