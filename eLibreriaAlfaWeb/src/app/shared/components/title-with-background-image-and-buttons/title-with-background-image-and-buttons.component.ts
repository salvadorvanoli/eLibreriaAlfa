import { Component, Input } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-title-with-background-image-and-buttons',
  standalone: true,
  imports: [
    RouterModule
  ],
  templateUrl: './title-with-background-image-and-buttons.component.html',
  styleUrl: './title-with-background-image-and-buttons.component.scss'
})

export class TitleWithBackgroundImageAndButtonsComponent {
  @Input() title: string = 'Título por defecto';
  @Input() description?: string;
}