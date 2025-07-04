import { Component, EventEmitter, Output, signal, ViewChild } from '@angular/core';
import { TitleAndDescriptionComponent } from '../../shared/components/title-and-description/title-and-description.component';
import { OptionsPanelComponent } from './components/options-panel/options-panel.component';
import { DataPanelComponent } from './components/data-panel/data-panel.component';
import { ControlPanelModalComponent } from './components/control-panel-modal/control-panel-modal.component';
import { PrintsTableComponent } from '../../shared/components/prints-table/prints-table.component'; 
import { OrderTableComponent } from '../../shared/components/order-table/order-table.component';
import { CategoryTreePanelComponent } from './components/category-tree-panel/category-tree-panel.component';

@Component({
  selector: 'app-control-panel',
  standalone: true,
  imports: [
    TitleAndDescriptionComponent,
    OptionsPanelComponent,
    DataPanelComponent,
    ControlPanelModalComponent,
    PrintsTableComponent,
    OrderTableComponent,
    CategoryTreePanelComponent
  ],
  templateUrl: './control-panel.component.html',
  styleUrl: './control-panel.component.scss'
})
export class ControlPanelComponent {
  @ViewChild('categoryTreePanel') categoryTreePanel!: CategoryTreePanelComponent;
  @ViewChild('dataPanel') dataPanel!: DataPanelComponent;

  modalIsVisible: boolean = false;

  selectedDataType = signal('Usuario');
  selectedItem: any = null;

  private dataTypes = [
    'Usuario',
    'Categoría',
    'Producto',
    'Publicación',
    'Pedido',
    'Impresión'
  ]

  ngOnInit() {
    const storedDataType = localStorage.getItem('selectedDataType');
    if (storedDataType && this.dataTypes.includes(storedDataType)) {
      this.selectedDataType.set(storedDataType);
    } else {
      this.selectedDataType.set('Usuario');
    }
  }

  onDataTypeSelected(dataType: string) {
    this.selectedDataType.set(dataType);
    this.closeModal();
  }

  onItemSelected(item: any) {
    console.log('Selected item:', item);
    this.selectedItem = item;
    this.modalIsVisible = true;
  }

  onDataReloaded() {
    if (this.selectedDataType() === 'Categoría') {
      this.categoryTreePanel.onReloadData();
    } else {
      this.dataPanel.onReloadData();
    }
  }

  onItemDeleted() {
    this.closeModal();
  }

  closeModal() {
    this.selectedItem = null;
    this.modalIsVisible = false;
  }

}
