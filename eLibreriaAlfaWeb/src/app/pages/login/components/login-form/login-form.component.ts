import { Component, signal } from '@angular/core';
import { Toast } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { SecurityService } from '../../../../core/services/security.service';
import { AccesoUsuario } from '../../../../core/models/usuario';
import { ViewChild } from '@angular/core';

import { FormTextInputComponent } from "../../../../shared/components/inputs/form-text-input/form-text-input.component";
import { FormPasswordInputComponent } from "../../../../shared/components/inputs/form-password-input/form-password-input.component";
import { PrimaryButtonComponent } from "../../../../shared/components/buttons/primary-button/primary-button.component";

@Component({
  selector: 'app-login-form',
  imports: [
    Toast,
    FormTextInputComponent,
    FormPasswordInputComponent,
    PrimaryButtonComponent
  ],
  providers: [MessageService],
  templateUrl: './login-form.component.html',
  styleUrl: './login-form.component.scss'
})
export class LoginFormComponent {

  @ViewChild('emailInput') emailInput: any;
  @ViewChild('passwordInput') passwordInput: any;

  email: string = '';
  password: string = '';
  formSubmitted = signal(false);

  isEmailInvalid: boolean = false;
  isPasswordInvalid: boolean = false;

  emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  constructor(
    private messageService: MessageService,
    private securityService: SecurityService
  ) {}

  login() { // TODO: Realizar la lógica de login
    this.formSubmitted.set(true);
    this.isPasswordInvalid = false;
    if (!this.validateForm()) {

      const usuario: AccesoUsuario = {
        email: this.email.trim(),
        contrasenia: this.password
      };
      
      this.securityService.auth(usuario).subscribe({
        next: () => {
          this.messageService.clear();
          this.messageService.add({ severity: 'success', summary: 'Operación exitosa', detail: "¡Has iniciado sesión exitosamente!", life: 4000 });
          this.resetForm();

          window.location.href = '/inicio';
        },
        error: (err) => {
          this.messageService.clear();
          if (err.error.error !== undefined) {
            this.messageService.add({ severity: 'error', summary: 'Error', detail: err.error.error, life: 4000 });
            if (err.error.error === "Contraseña incorrecta") {
              this.isPasswordInvalid = true;
            }
          } else {
            this.messageService.add({ severity: 'error', summary: 'Error', detail: "No fue posible conectar con el servidor", life: 4000 });
          }
        }
      });

    } else {
      this.messageService.clear();
      this.messageService.add({ severity: 'error', summary: 'Error', detail: "Datos ingresados inválidos", life: 4000 });
    }
  }

  resetForm() {
    this.emailInput?.reset();
    this.passwordInput?.reset();
    
    this.email = '';
    this.password = '';
    this.formSubmitted.set(false);
  }

  validateForm() {
    return this.isEmailInvalid;
  }
}
