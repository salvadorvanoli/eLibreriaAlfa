import { Component, Input } from '@angular/core';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ConfirmDialog } from 'primeng/confirmdialog';
import { ToastModule } from 'primeng/toast';
import { PrimaryButtonComponent } from "../buttons/primary-button/primary-button.component";
import { ButtonSeverity } from 'primeng/button';

@Component({
  selector: 'app-confirm-dialog-button',
  standalone: true,
  imports: [
    ConfirmDialog,
    ToastModule,
    PrimaryButtonComponent
  ],
  providers: [
    ConfirmationService,
    MessageService
  ],
  templateUrl: './confirm-dialog-button.component.html',
  styleUrl: './confirm-dialog-button.component.scss'
})
export class ConfirmDialogButtonComponent {

  @Input() buttonLabel: string = 'Confirmar';
  @Input() buttonIcon: string = 'pi pi-check';
  @Input() buttonClasses: string = 'w-full';
  @Input() buttonSeverity: ButtonSeverity = 'primary';
  @Input() header: string = 'Confirmación';
  @Input() message: string = '¿Está seguro de que desea continuar?';
  @Input() icon: string = 'pi pi-exclamation-triangle';
  @Input() acceptButtonLabel: string = 'Aceptar';
  @Input() rejectButtonLabel: string = 'Cancelar';
  @Input() acceptMethod!: () => void;

  constructor(
    private confirmationService: ConfirmationService,
    private messageService: MessageService
  ) {}
  
  confirm() {
    this.confirmationService.confirm({
      header: this.header,
      message: this.message,
      icon: this.icon,
      rejectButtonProps: {
        label: this.rejectButtonLabel,
        severity: 'secondary'
      },
      acceptButtonProps: {
        label: this.acceptButtonLabel
      },
      accept: this.acceptMethod,
      reject: () => {
        this.messageService.clear();
        this.messageService.add({ severity: 'info', summary: 'Cancelado', detail: 'Operación cancelada', life: 4000 });
      }
    });
  }
}
