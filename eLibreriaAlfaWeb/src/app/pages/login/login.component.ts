import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { NavbarComponent } from "../../shared/components/navbar/navbar.component";
import { FooterComponent } from "../../shared/components/footer/footer.component";
import { LoginFormComponent } from "./components/login-form/login-form.component";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    RouterModule,
    NavbarComponent,
    FooterComponent,
    LoginFormComponent
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {

}
