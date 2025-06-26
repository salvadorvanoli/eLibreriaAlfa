import { Component, signal } from '@angular/core';
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

  modalIsVisible: boolean = false;

  selectedDataType = signal('Usuario');
  selectedItem: any = null;

  onDataTypeSelected(dataType: string) {
    this.selectedDataType.set(dataType);
    this.closeModal();
  }

  onItemSelected(item: any) {
    console.log('Selected item:', item);
    this.selectedItem = item;
    this.modalIsVisible = true;
  }

  closeModal() {
    this.selectedItem = null;
    this.modalIsVisible = false;
  }

}
