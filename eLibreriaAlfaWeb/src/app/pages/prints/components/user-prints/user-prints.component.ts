import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PrintsTableComponent } from '../../../../shared/components/prints-table/prints-table.component';

@Component({
  selector: 'app-user-prints',
  standalone: true,
  imports: [
    CommonModule,
    PrintsTableComponent
  ],
  templateUrl: './user-prints.component.html',
  styleUrl: './user-prints.component.scss'
})
export class UserPrintsComponent {
  
}
