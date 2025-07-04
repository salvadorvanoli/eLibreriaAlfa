import { Component, effect, EventEmitter, Input, Output, signal, ViewChild } from '@angular/core';
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
import { PublicationService } from '../../../../core/services/publication.service';
import { ProductService } from '../../../../core/services/product.service';
import { ProductoSimpleDto } from '../../../../core/models/producto';

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
  @ViewChild('searchBar') searchBar!: SearchBarComponent;
  @ViewChild('orderSelect') orderSelect!: FormSelectInputComponent;

  items!: any[];
  searchText: string = '';
  order: string = "";
  sortOptions: { label: string, value: string }[] = [
    { label: "Más recientes", value: "desc" },
    { label: "Más antiguos", value: "asc" }
  ]

  rows: number = 10;
  first: number = 0;
  rowsPerPageOptions: number[] = [10, 20, 30];

  @Input() itemType = signal('Usuario');
  @Input() page!: number;
  @Input() size!: number;

  @Output() itemSelected = new EventEmitter<any>();

  constructor(
    private messageService: MessageService,
    private controlPanelService: ControlPanelService,
    private publicationService: PublicationService,
    private productService: ProductService
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
          this.messageService.add({ 
            severity: 'error', 
            summary: 'Error', 
            detail: "No se encontraron elementos acordes los filtros especificados", 
            life: 4000 
          });
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
    this.resetChildComponents();
    this.searchText = '';
    this.order = '';
    this.first = 0;
    if (this.dataView) {
      this.dataView.first = 0;
    }
    this.getItems();
  }

  applyFilters() {
    this.filterItems();
  }

  onOrderChange(order: string) {
    this.order = order;
    this.filterItems();
  }

  onSearchTextChange(text: string) {
    this.searchText = text;
  }

  sendDetails(item: any) {
    const id = this.itemType() === 'Usuario' ? item.email : item.id;
    this.controlPanelService.getElementByTypeAndId(this.itemType(), id)?.subscribe({
      next: (response: any) => {
        this.itemSelected.emit(response);
      },
      error: (error: any) => {
        this.messageService.clear();
        this.messageService.add({ 
          severity: 'error', 
          summary: 'Error', 
          detail: "No se pudo obtener el elemento seleccionado", 
          life: 4000 
        });
      }
    });
  }
  
  onReloadData() {
    this.filterItems();
  }

  onPageChange(event: any) {
    this.first = event.first;
    this.rows = event.rows;
  }
  
  onRowsChange(event: any) {
    this.rows = event.value;
  }

  private resetChildComponents() {
    this.searchBar?.reset();
    this.orderSelect?.reset();
  }

  handleDelete(event: {action: string, item: any}): void {
    switch (event.action) {
      case 'eliminar':
        this.eliminarPublicacion(event.item);
        break;
      case 'inhabilitar':
        this.inhabilitarProducto(event.item);
        break;
      case 'habilitar':
        this.habilitarProducto(event.item);
        break;
    }
  }

  private eliminarPublicacion(item: any): void {
    this.publicationService.delete(item.id).subscribe({
      next: (response) => {
        this.messageService.add({
          severity: 'success',
          summary: 'Éxito',
          detail: 'Publicación eliminada correctamente'
        });
        this.getItems();
      },
      error: (error) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'No se pudo eliminar la publicación'
        });
      }
    });
  }

  private inhabilitarProducto(item: any): void {
    this.productService.disable(item.id).subscribe({
      next: (response: ProductoSimpleDto) => {
        this.messageService.add({
          severity: 'success',
          summary: 'Éxito',
          detail: 'Producto deshabilitado correctamente'
        });
        this.getItems();
      },
      error: (error) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'No se pudo deshabilitar el producto'
        });
      }
    });
  }

  private habilitarProducto(item: any): void {
    this.productService.enable(item.id).subscribe({
      next: (response: ProductoSimpleDto) => {
        this.messageService.add({
          severity: 'success',
          summary: 'Éxito',
          detail: 'Producto habilitado correctamente'
        });
        this.getItems();
      },
      error: (error) => {
        if (error?.error?.message?.includes('categorías') || 
            error?.error?.error?.includes('categorías') ||
            error?.status === 400) {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'No se puede habilitar el producto porque no tiene categorías asociadas'
          });
        } else {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'No se pudo habilitar el producto'
          });
        }
      }
    });
  }
}
