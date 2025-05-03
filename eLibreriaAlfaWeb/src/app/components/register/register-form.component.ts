import { Component } from '@angular/core';
import { InputTextModule } from 'primeng/inputtext';
import { FormsModule, ReactiveFormsModule  } from '@angular/forms';
import { PasswordModule } from 'primeng/password';
import { DividerModule } from 'primeng/divider';
import { ButtonModule } from 'primeng/button';
import { UsuarioService } from '../../services/usuario.service';
import { AccesoUsuario, UsuarioSimple } from '../../models/usuario';

@Component({
  selector: 'app-register-form',
  standalone: true,
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
  contrasenia: string = '';
  confirmarContrasenia: string = '';

  errorMessage: string = '';
  successMessage: string = '';

  constructor(private usuarioService: UsuarioService) {}

  mediumRegex: string = '^.{6,}$';
  strongRegex: string = '^(?=.*\\d)(?=.*[!@#$%^&*()_+\\=\\[\\]{};\'":\\\\|,.<>\\/?-]).{8,}$';

  register() {
    if (this.validateForm()) {

      const usuario: AccesoUsuario = {
        email: this.email,
        contrasenia: this.contrasenia
      };
      
      this.usuarioService.post(usuario).subscribe({
        next: (response: UsuarioSimple) => {
          console.log('Usuario creado exitosamente:', response);
          // redirigir o mostrar mensaje de éxito
        },
        error: (err) => {
          console.error('Error al crear el usuario:', err);
          // mostrar mensaje de error
        }
      });

    } else {
      console.log('Registration failed. Please check your inputs.');
    }
  }

  validateEmail() {
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailPattern.test(this.email) ? true : "El correo electrónico no es válido";
  }

  validatePassword() {
    return this.contrasenia.length >= 6 ? true : "La contraseña debe tener al menos 6 caracteres";
  }

  validateConfirmPassword() {
    return this.contrasenia === this.confirmarContrasenia ? true : "Las contraseñas deben coincidir";
  }

  validateForm() {
    return this.validateEmail() && this.validatePassword() && this.validateConfirmPassword();
  }

  


}
