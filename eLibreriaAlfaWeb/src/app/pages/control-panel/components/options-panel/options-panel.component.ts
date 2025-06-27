import { Component, EventEmitter, Output } from '@angular/core';
import { MenuModule } from 'primeng/menu';
import { PrimaryButtonComponent } from "../../../../shared/components/buttons/primary-button/primary-button.component";

@Component({
  selector: 'app-options-panel',
  standalone: true,
  imports: [
    MenuModule,
    PrimaryButtonComponent
],
  templateUrl: './options-panel.component.html',
  styleUrl: './options-panel.component.scss'
})
export class OptionsPanelComponent {

  @Output() modalIsVisible = new EventEmitter<boolean>();

  items = [
    {
      label: 'Usuarios',
      icon: 'pi pi-user',
      command: () => this.sendDataType('Usuario')
    },
    {
      label: 'Categorías',
      icon: 'pi pi-sitemap',
      command: () => this.sendDataType('Categoría')
    },
    {
      label: 'Productos',
      icon: 'pi pi-box',
      command: () => this.sendDataType('Producto')
    },
    {
      label: 'Publicaciones',
      icon: 'pi pi-book',
      command: () => this.sendDataType('Publicación')
    },
    {
      label: 'Pedidos',
      icon: 'pi pi-shopping-cart',
      command: () => this.sendDataType('Pedido')
    },
    {
      label: 'Impresiones',
      icon: 'pi pi-print',
      command: () => this.sendDataType('Impresión')
    }
  ];

  @Output() dataType = new EventEmitter<string>();

  sendDataType(type: string) {
    localStorage.setItem('selectedDataType', type);
    this.dataType.emit(type);
  }

  showModal() {
    this.modalIsVisible.emit(true);
  }

}
