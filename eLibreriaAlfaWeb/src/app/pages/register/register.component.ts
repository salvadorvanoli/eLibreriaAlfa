import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { RegisterFormComponent } from '../../components/register/register-form.component';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    RouterModule,
    RegisterFormComponent
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {

}
