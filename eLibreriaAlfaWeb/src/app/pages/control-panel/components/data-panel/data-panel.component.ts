import { Component, effect, Input, signal, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MessageService } from 'primeng/api';
import { Toast } from 'primeng/toast';
import { DataView } from 'primeng/dataview';
import { SearchBarComponent } from '../../../../shared/components/inputs/search-bar/search-bar.component';
import { FormSelectInputComponent } from '../../../../shared/components/inputs/form-select-input/form-select-input.component';
import { PrimaryButtonComponent } from "../../../../shared/components/buttons/primary-button/primary-button.component";
import { ButtonModule } from 'primeng/button';
import { ItemRowComponent } from '../item-row/item-row.component';
import { MessageComponent } from '../../../../shared/components/message/message.component';
import { ControlPanelService } from '../../../../core/services/control-panel.service';
import { ElementoLista } from '../../../../core/models/elemento-lista';

@Component({
  selector: 'app-data-panel',
  standalone: true,
  imports: [
    CommonModule,
    Toast,
    DataView,
    SearchBarComponent,
    FormSelectInputComponent,
    PrimaryButtonComponent,
    ButtonModule,
    ItemRowComponent,
    MessageComponent
],
  providers: [
    MessageService
  ],
  templateUrl: './data-panel.component.html',
  styleUrl: './data-panel.component.scss'
})
export class DataPanelComponent {

  @ViewChild('dv') dataView: DataView | undefined;

  items!: ElementoLista[];
  searchText: string = '';
  order: string = "";
  sortOptions: { label: string, value: string }[] = [
    { label: "Más recientes", value: "desc" },
    { label: "Más antiguos", value: "asc" }
  ]

  // Attributes for pagination
  rows: number = 10;
  first: number = 0;
  rowsPerPageOptions: number[] = [10, 20, 30];

  @Input() itemType = signal('Usuario');
  @Input() page!: number;
  @Input() size!: number;

  constructor(
    private messageService: MessageService,
    private controlPanelService: ControlPanelService
  ) {
    effect(() => {
      this.resetFilters();
    });
  }

  getItems() {
    this.controlPanelService.getDataByType(this.itemType())?.subscribe((data) => {
      this.items = data;
    });
  }

  filterItems() {
    this.controlPanelService.getDataByTypeFiltered(
      this.itemType(),
      this.searchText,
      this.order
    )?.subscribe({
      next: (response: any) => {
        if (response.length > 0) {
          this.items = response;
        } else {
          this.messageService.clear();
          this.messageService.add({ severity: 'error', summary: 'Error', detail: "No se encontraron elementos acordes los filtros especificados", life: 4000 });
        }
      },
      error: (error: any) => {
        console.error('Error fetching filtered products:', error);
      }
    });
  }

  onItemTypeChange(itemType: string) {
    this.itemType.set(itemType);
  }

  resetFilters() {
    this.searchText = '';
    this.order = '';
    this.first = 0;
    if (this.dataView) {
      this.dataView.first = 0;
    }
    this.getItems();
  }

  applyFilters() {
    if (this.order != '' || this.searchText.trim() != '') {
      this.filterItems();
    }
  }

  onOrderChange(order: string) {
    this.order = order;
    this.filterItems();
  }

  onSearchTextChange() {
    if (this.searchText.trim() !== '' && this.searchText.length >= 3) {
      this.filterItems();
    }
  }
  
  getItemTitle(item: any): string {
    return this.itemType + " nro. " + item.id;  
  }

  getItemFirstAttribute(item: any): string {
    switch (this.itemType()) {
      case 'Usuario':
        return item.email;
      case 'Categoria':
      case 'Producto':
        return item.nombre;
      case 'Publicacion':
        return item.titulo;
      // case 'Pedido':
      //   return item.usuario.nombre;
      default:
        return '';
    }
  }

  getItemSecondAttribute(item: any): string {
    switch (this.itemType()) {
      case 'Usuario':
        return item.nombre + ' ' + item.apellido;
      case 'Categoria':
        return "";
      case 'Producto':
        return '$' + item.precio;
      case 'Publicacion':
        return item.titulo;
      // case 'Pedido':
      //   return item.usuario.nombre;
      default:
        return '';
    }
  }

  getItemImage(item: any): string {
    switch (this.itemType()) {
      case 'Usuario':
      case 'Categoria':
      // case 'Pedido':
        return ""
      case 'Producto':
        return item.imagenes ? item.imagenes[0] : '';
      case 'Publicacion':
        return item.imagen ? item.imagen : '';
      // case 'Pedido':
      //   return item.usuario.nombre;
      default:
        return '';
    }
  }

  onPageChange(event: any) {
    this.first = event.first;
    this.rows = event.rows;
  }
  
  onRowsChange(event: any) {
    this.rows = event.value;
  }

}
