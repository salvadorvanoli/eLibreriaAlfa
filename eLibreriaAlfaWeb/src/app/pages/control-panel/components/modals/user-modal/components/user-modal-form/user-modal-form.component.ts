import { Component, computed, Input, signal } from '@angular/core';
import { Toast } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { UserService } from '../../../../../../../core/services/user.service';
import { Usuario, UsuarioSimple } from '../../../../../../../core/models/usuario';
import { ViewChild } from '@angular/core';

import { FormTextInputComponent } from '../../../../../../../shared/components/inputs/form-text-input/form-text-input.component';
import { FormPasswordInputComponent } from '../../../../../../../shared/components/inputs/form-password-input/form-password-input.component';
import { FormSelectInputComponent } from '../../../../../../../shared/components/inputs/form-select-input/form-select-input.component';
import { PrimaryButtonComponent } from '../../../../../../../shared/components/buttons/primary-button/primary-button.component';
import { Rol } from '../../../../../../../core/models/rol';


@Component({
  selector: 'app-user-modal-form',
  standalone: true,
  imports: [
    Toast,
    FormTextInputComponent,
    FormPasswordInputComponent,
    FormSelectInputComponent,
    PrimaryButtonComponent
  ],
  providers: [
    MessageService
  ],
  templateUrl: './user-modal-form.component.html',
  styleUrl: './user-modal-form.component.scss'
})
export class UserModalFormComponent {
  @ViewChild('emailInput') emailInput: any;
  @ViewChild('passwordInput') passwordInput: any;
  @ViewChild('roleSelect') roleSelect: any;
  @ViewChild('telephoneInput') telephoneInput: any;
  @ViewChild('nameInput') nameInput: any;
  @ViewChild('surnameInput') surnameInput: any;

  email: string = '';
  password: string = '';
  role: string = "";
  telephone: string = '';
  name: string = '';
  surname: string = '';

  formSubmitted = signal(false);

  isEmailInvalid: boolean = false;
  isPasswordInvalid: boolean = false;
  isRoleInvalid: boolean = false;
  isTelephoneInvalid: boolean = false;
  isNameInvalid: boolean = false;
  isSurnameInvalid: boolean = false;

  emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  telephonePattern = /^09\d{7}$/;
  roleOptions: { label: string, value: Rol }[] = [
    { label: 'Cliente', value: Rol.CLIENTE },
    { label: 'Empleado', value: Rol.EMPLEADO },
    { label: 'Administrador', value: Rol.ADMINISTRADOR }
  ]
  namePattern = /^[a-zA-ZÀ-ÿ\s]{1,40}$/;

  @Input() user: UsuarioSimple | null = null;

  constructor(
    private messageService: MessageService,
    private userService: UserService
  ) {}

  confirm() {
    this.formSubmitted.set(true);
    if (!this.validateForm()) {
      switch (this.user) {
        case null:
          this.create();
          break;
        default:
          this.update();
          break;
      }
    } else {
      this.messageService.add({ severity: 'error', summary: 'Error', detail: "Datos ingresados inválidos", life: 4000 });
    }
  }

  create() {
    const usuario: Usuario = {
      email: this.email,
      contrasenia: this.password,
      rol: this.role as Rol,
      telefono: this.telephone,
      nombre: this.name,
      apellido: this.surname
    };
    
    this.userService.create(usuario).subscribe({
      next: (response: Usuario) => {
        this.messageService.add({ severity: 'success', summary: 'Operación exitosa', detail: "¡Usuario creado exitosamente!", life: 4000 });
        this.resetForm();
      },
      error: (err) => {
        if (err.error.error !== undefined) {
          this.messageService.add({ severity: 'error', summary: 'Error', detail: err.error.error, life: 4000 });
        } else {
          this.messageService.add({ severity: 'error', summary: 'Error', detail: "No fue posible conectar con el servidor", life: 4000 });
        }
      }
    });
  }

  update(){
    const usuario: Usuario = {
      email: this.email,
      contrasenia: this.password,
      rol: this.role as Rol,
      telefono: this.telephone,
      nombre: this.name,
      apellido: this.surname
    };

    this.userService.put(this.user?.id!, usuario).subscribe({
      next: (response: Usuario) => {
        this.messageService.add({ severity: 'success', summary: 'Operación exitosa', detail: "¡Usuario actualizado exitosamente!", life: 4000 });
        this.resetForm();
      },
      error: (err) => {
        if (err.error.error !== undefined) {
          this.messageService.add({ severity: 'error', summary: 'Error', detail: err.error.error, life: 4000 });
        } else {
          this.messageService.add({ severity: 'error', summary: 'Error', detail: "No fue posible conectar con el servidor", life: 4000 });
        }
      }
    });
  }

  validateForm() {
    return this.isEmailInvalid || this.isPasswordInvalid || this.isRoleInvalid || this.isTelephoneInvalid || this.isNameInvalid || this.isSurnameInvalid;
  }

  resetForm() {
    this.formSubmitted.set(false);

    this.email = '';
    this.password = '';
    this.role = '';
    this.telephone = '';
    this.name = '';
    this.surname = '';

    this.isEmailInvalid = false;
    this.isPasswordInvalid = false;
    this.isRoleInvalid = false;
    this.isTelephoneInvalid = false;
    this.isNameInvalid = false;
    this.isSurnameInvalid = false;

    this.emailInput?.reset();
    this.passwordInput?.reset();
    this.roleSelect?.reset();
    this.telephoneInput?.reset();
    this.nameInput?.reset();
    this.surnameInput?.reset();
  }
}
