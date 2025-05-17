import { Component } from '@angular/core';
import { SearchBarComponent } from '../../../../shared/components/inputs/search-bar/search-bar.component';

@Component({
  selector: 'app-catalog-filter-controls',
  standalone: true,
  imports: [
    SearchBarComponent
  ],
  templateUrl: './catalog-filter-controls.component.html',
  styleUrl: './catalog-filter-controls.component.scss'
})
export class CatalogFilterControlsComponent {

}
