import { Component, computed, EventEmitter, Input, Output, signal, SimpleChanges } from '@angular/core';
import { Toast } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { UserService } from '../../../../../../core/services/user.service';
import { Usuario, UsuarioSimple } from '../../../../../../core/models/usuario';
import { ViewChild } from '@angular/core';

import { FormTextInputComponent } from '../../../../../../shared/components/inputs/form-text-input/form-text-input.component';
import { FormPasswordInputComponent } from '../../../../../../shared/components/inputs/form-password-input/form-password-input.component';
import { FormSelectInputComponent } from '../../../../../../shared/components/inputs/form-select-input/form-select-input.component';
import { PrimaryButtonComponent } from '../../../../../../shared/components/buttons/primary-button/primary-button.component';
import { Rol } from '../../../../../../core/models/rol';


@Component({
  selector: 'app-user-form',
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
  templateUrl: './user-form.component.html',
  styleUrl: './user-form.component.scss'
})
export class UserFormComponent {
  @ViewChild('emailInput') emailInput: any;
  @ViewChild('passwordInput') passwordInput: any;
  @ViewChild('roleSelect') roleSelect: any;
  @ViewChild('telephoneInput') telephoneInput: any;
  @ViewChild('nameInput') nameInput: any;
  @ViewChild('surnameInput') surnameInput: any;

  @Input() user: UsuarioSimple | null = null;

  @Output() reloadData = new EventEmitter<void>();

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

  constructor(
    private messageService: MessageService,
    private userService: UserService
  ) {}

  ngOnInit() {
    this.resetForm();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['user']) {
      this.manageForm();
    }
  }

  manageForm() {
    if (this.user != null) {
      this.loadForm();
    } else {
      this.resetForm();
    }
  }

  confirm() {
    this.formSubmitted.set(true);
    
    if (this.validateForm()) {
      this.messageService.clear();
      this.messageService.add({ 
        severity: 'error', 
        summary: 'Error', 
        detail: "Datos ingresados inválidos", 
        life: 4000
      });
    } else {
      this.user ? this.update() : this.create();
    }
  }

  create() {
    const usuario = this.createRequestDto();
    
    this.userService.create(usuario).subscribe({
      next: (response: Usuario) => this.handleSuccess('creado', response),
      error: (error) => this.handleError(error)
    });
  }

  update(){
    const usuario = this.createRequestDto();
    
    this.userService.put(this.user?.id!, usuario).subscribe({
      next: (response: Usuario) => this.handleSuccess('actualizado', response),
      error: (error) => this.handleError(error)
    });
  }

  validateForm() {
    return this.isEmailInvalid ||
      this.isPasswordInvalid ||
      this.isRoleInvalid ||
      this.isTelephoneInvalid ||
      this.isNameInvalid ||
      this.isSurnameInvalid;
  }

  loadForm() {
    if (this.user) {
      this.email = this.user.email;
      this.role = this.user.rol;
      this.telephone = this.user.telefono;
      this.name = this.user.nombre;
      this.surname = this.user.apellido;

      this.loadFormFields();
    }

    this.resetValidationState();
  }

  resetForm() {
    this.formSubmitted.set(false);

    this.resetFormData();
    this.resetValidationState();
    this.resetFormComponents();
  }

  private createRequestDto(): Usuario {
    const usuario: Usuario = {
      email: this.email,
      contrasenia: this.password,
      rol: this.role as Rol,
      telefono: this.telephone,
      nombre: this.name,
      apellido: this.surname
    };
    
    return usuario;
  }

  private handleSuccess(action: string, response: any) {
    this.messageService.clear();
    this.messageService.add({ 
      severity: 'success', 
      summary: 'Éxito', 
      detail: `¡Usuario ${action} exitosamente!`, 
      life: 4000 
    });

    this.resetForm();
    this.onDataReloaded();
  }

  private handleError(error: any) {
    const errorMessage = error?.error.error || error?.error.message || "No fue posible conectar con el servidor";
    this.messageService.clear();
    this.messageService.add({ 
      severity: 'error', 
      summary: 'Error', 
      detail: errorMessage, 
      life: 4000 
    });
  }

  private loadFormFields() {
    setTimeout(() => {
      this.emailInput?.setValue(this.email);
      this.passwordInput?.reset();
      this.roleSelect?.setValue(this.role);
      this.telephoneInput?.setValue(this.telephone);
      this.nameInput?.setValue(this.name);
      this.surnameInput?.setValue(this.surname);
    }, 0);
  }

  private resetValidationState() {
    this.isEmailInvalid = false;
    this.isPasswordInvalid = false;
    this.isRoleInvalid = false;
    this.isTelephoneInvalid = false;
    this.isNameInvalid = false;
    this.isSurnameInvalid = false;
  }

  private resetFormData() {
    this.email = '';
    this.password = '';
    this.role = '';
    this.telephone = '';
    this.name = '';
    this.surname = '';
  }

  private resetFormComponents() {
    this.emailInput?.reset();
    this.passwordInput?.reset();
    this.roleSelect?.reset();
    this.telephoneInput?.reset();
    this.nameInput?.reset();
    this.surnameInput?.reset();
  }

  private onDataReloaded() {
    this.reloadData.emit();
  }
}
