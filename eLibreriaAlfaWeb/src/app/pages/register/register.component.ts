import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { TitleAndDescriptionComponent } from '../../shared/components/title-and-description/title-and-description.component';
import { RegisterFormComponent } from './components/register-form/register-form.component';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    RouterModule,
    TitleAndDescriptionComponent,
    RegisterFormComponent
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {

}
