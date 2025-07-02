import { Component, EventEmitter, Output, OnInit } from '@angular/core';
import { MenuModule } from 'primeng/menu';
import { PrimaryButtonComponent } from "../../../../shared/components/buttons/primary-button/primary-button.component";
import { SecurityService } from '../../../../core/services/security.service';

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
export class OptionsPanelComponent implements OnInit {

  showCreateButton: boolean = localStorage.getItem('selectedDataType') !== 'Pedido' && localStorage.getItem('selectedDataType') !== 'Impresión';

  @Output() modalIsVisible = new EventEmitter<boolean>();
  @Output() dataType = new EventEmitter<string>();
  
  selectedDataType: string = '';

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

  constructor(
    private securityService: SecurityService
  ) {}

  ngOnInit(): void {
    this.securityService.getActualUser().subscribe(usuarioActual => {
      if (usuarioActual && usuarioActual.rol === 'EMPLEADO') {
        this.items = this.items.filter(item => item.label === 'Pedidos' || item.label === 'Impresiones');
      }
    });
  }

  sendDataType(type: string) { 
    this.showCreateButton = (type !== 'Pedido' && type !== 'Impresión');
    this.selectedDataType = type;
    localStorage.setItem('selectedDataType', type);
    this.dataType.emit(type);
  }

  showModal() {
    this.modalIsVisible.emit(true);
  }

}
