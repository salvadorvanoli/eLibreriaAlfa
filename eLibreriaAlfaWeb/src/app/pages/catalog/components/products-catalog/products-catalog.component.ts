import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DataView } from 'primeng/dataview';
import { SelectButton } from 'primeng/selectbutton';
import { FormsModule } from "@angular/forms";
import { CatalogFilterControlsComponent } from "../../components/catalog-filter-controls/catalog-filter-controls.component";
import { ProductRowComponent } from "../product-row/product-row.component";
import { ProductCardComponent } from "../product-card/product-card.component";
import { Producto } from '../../../../core/models/producto';

@Component({
  selector: 'app-products-catalog',
  standalone: true,
  imports: [
    CommonModule,
    DataView,
    SelectButton,
    FormsModule,
    CatalogFilterControlsComponent,
    ProductRowComponent,
    ProductCardComponent
],
  templateUrl: './products-catalog.component.html',
  styleUrl: './products-catalog.component.scss'
})
export class ProductsCatalogComponent {

  layout: 'list' | 'grid' = 'grid';
  options = ['list', 'grid'];
  rows: number = 6;
  first: number = 0;
  rowsPerPageOptions: number[] = [6, 12, 18];

  @Input() products!: Producto[];

  ngOnInit() {
    console.log(this.products);
  }

  onPageChange(event: any) {
    this.first = event.first;
    this.rows = event.rows;
  }
  
  onRowsChange(event: any) {
    this.rows = event.value;
  }
}
