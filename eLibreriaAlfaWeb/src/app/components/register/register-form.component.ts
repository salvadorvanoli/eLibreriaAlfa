import { Component } from '@angular/core';
import { InputTextModule } from 'primeng/inputtext';
import { FormsModule, ReactiveFormsModule  } from '@angular/forms';
import { PasswordModule } from 'primeng/password';
import { DividerModule } from 'primeng/divider';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'app-register-form',
  imports: [
    InputTextModule,
    PasswordModule,
    ButtonModule,
    FormsModule,
    ReactiveFormsModule,
    PasswordModule,
    DividerModule
],
  templateUrl: './register-form.component.html',
  styleUrl: './register-form.component.scss'
})
export class RegisterFormComponent {

  email: string = '';
  password: string = '';
  confirmPassword: string = '';

  mediumRegex: string = '^.{6,}$';
  strongRegex: string = '^(?=.*\\d)(?=.*[!@#$%^&*()_+\\=\\[\\]{};\'":\\\\|,.<>\\/?-]).{8,}$';

}
