import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { TitleAndDescriptionComponent } from '../../shared/components/title-and-description/title-and-description.component';
import { LoginFormComponent } from "./components/login-form/login-form.component";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    RouterModule,
    TitleAndDescriptionComponent,
    LoginFormComponent
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {

}
