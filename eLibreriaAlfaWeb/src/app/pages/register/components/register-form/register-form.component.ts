import { Component, computed, signal } from '@angular/core';
import { InputTextModule } from 'primeng/inputtext';
import { FormsModule, ReactiveFormsModule  } from '@angular/forms';
import { PasswordModule } from 'primeng/password';
import { DividerModule } from 'primeng/divider';
import { ButtonModule } from 'primeng/button';
import { Message } from 'primeng/message';
import { MessageService } from 'primeng/api';
import { Toast } from 'primeng/toast';
import { UsuarioService } from '../../../../core/services/usuario.service';
import { AccesoUsuario, UsuarioSimple } from '../../../../core/models/usuario';

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
    DividerModule,
    Message,
    Toast
  ],
  providers: [MessageService],
  templateUrl: './register-form.component.html',
  styleUrl: './register-form.component.scss'
})
export class RegisterFormComponent {

  email = signal('');
  contrasenia = signal('');
  confirmarContrasenia = signal('');
  formSubmitted = signal(false);

  emailErrorMessage = computed(() => {
    return this.validateEmail();
  });
  
  contraseniaErrorMessage = computed(() => {
    return this.validateContrasenia();
  });

  confirmarContraseniaErrorMessage = computed(() => {
    return this.validateConfirmarContrasenia();
  });

  mediumRegex: string = '^.{6,}$';
  strongRegex: string = '^(?=.*\\d)(?=.*[!@#$%^&*()_+\\=\\[\\]{};\'":\\\\|,.<>\\/?-]).{8,}$';

  constructor(
    private messageService: MessageService,
    private usuarioService: UsuarioService
  ) {}

  register() {
    this.formSubmitted.set(true);
    if (!this.validateForm()) {

      const usuario: AccesoUsuario = {
        email: this.email(),
        contrasenia: this.contrasenia()
      };
      
      this.usuarioService.post(usuario).subscribe({
        next: (response: UsuarioSimple) => {
          this.messageService.add({ severity: 'success', summary: 'Registro exitoso', detail: "¡Usuario creado exitosamente!", life: 3000 });
        },
        error: (err) => {
          if (err.error.error !== undefined) {
            this.messageService.add({ severity: 'error', summary: 'Error', detail: err.error.error, life: 4000 });
          } else {
            this.messageService.add({ severity: 'error', summary: 'Error', detail: "No fue posible conectar con el servidor", life: 4000 });
          }
        }
      });

    } else {
      this.messageService.add({ severity: 'error', summary: 'Error', detail: "Datos ingresados inválidos", life: 4000 });
    }
  }

  validateEmail() {
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return this.formSubmitted() && !emailPattern.test(this.email());
  }

  validateContrasenia() {
    return this.formSubmitted() && this.contrasenia().length < 6;
  }

  validateConfirmarContrasenia() {
    return this.formSubmitted() && this.contrasenia() !== this.confirmarContrasenia();
  }

  validateForm() {
    return this.emailErrorMessage() || this.contraseniaErrorMessage() || this.confirmarContraseniaErrorMessage();
  }
}
