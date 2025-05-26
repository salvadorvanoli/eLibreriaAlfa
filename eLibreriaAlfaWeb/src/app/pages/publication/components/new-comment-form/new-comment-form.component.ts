import { Component, Input, Output, EventEmitter, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Toast } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { AgregarComentario } from '../../../../core/models/publicacion';
import { SecurityService } from '../../../../core/services/security.service';
import { PublicationService } from '../../../../core/services/publication.service';
import { Message } from 'primeng/message';
import { FormTextInputComponent } from '../../../../shared/components/inputs/form-text-input/form-text-input.component';
import { FormTextareaInputComponent } from '../../../../shared/components/inputs/form-textarea-input/form-textarea-input.component';
import { PrimaryButtonComponent } from '../../../../shared/components/buttons/primary-button/primary-button.component';
import { ViewChild } from '@angular/core';

@Component({
  selector: 'app-new-comment-form',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    Toast,
    FormTextareaInputComponent,
    FormTextInputComponent,
    PrimaryButtonComponent
  ],
  providers: [MessageService],
  templateUrl: './new-comment-form.component.html',
  styleUrls: ['./new-comment-form.component.scss']
})
export class NewCommentFormComponent {

  @ViewChild('tituloInput') tituloInput: any;
  @ViewChild('textoInput') textoInput: any;
    
  @Input() publicacionId!: number;
  @Input() publicacionTitulo!: string;

  titulo: string = '';
  texto: string = '';

  isHeaderInvalid: boolean = false;
  isTextInvalid: boolean = false;

  formSubmitted = signal(false);

  textAreaPattern = /^[a-zA-Z0-9\s.,;:!?'"(){}[\]<>%&$#@!^*+=áéíóúÁÉÍÓÚñÑüÜ-]+$/;
  messagePattern = /^.{1,}$/;

  constructor(
    private messageService: MessageService,
    private securityService: SecurityService,
    private publicationService: PublicationService
  ) {}

  enviarComentario(): void {
    this.formSubmitted.set(true);
    
    if (this.validateForm()) {
      return;
    }

    if (!this.securityService.actualUser?.id) {
      this.showError('Debes estar autenticado para comentar');
      return;
    }

    const textoPlano = this.htmlToPlainText(this.texto.trim());

    const commentData = {
        fechaCreacion: new Date().toISOString(), // Convertir a string ISO
        usuario: {
        id: Number(this.securityService.actualUser.id) // Objeto con id
        },
        titulo: this.titulo.trim(),
        texto: textoPlano,
        publicacion: {
        id: Number(this.publicacionId) // Objeto con id
        }
    };

    this.publicationService.createComment(commentData).subscribe({
      next: () => {
        this.resetForm();
        this.showSuccess('Comentario agregado exitosamente');
        setTimeout(() => {
            window.location.reload();
        }, 2000);
      },
      error: (error) => {
        this.showError('Error al agregar el comentario. Inténtalo de nuevo.');
      }
    });
  }

  private htmlToPlainText(html: string): string {
    const tempDiv = document.createElement('div');
    tempDiv.innerHTML = html;
    
    return tempDiv.textContent || tempDiv.innerText || '';
  }   

  validateForm(): boolean {
    return this.isHeaderInvalid || this.isTextInvalid;
  }

  private showSuccess(message: string): void {
    this.messageService.add({
      severity: 'success',
      summary: 'Éxito',
      detail: message
    });
  }

  private showError(message: string): void {
    this.messageService.add({
      severity: 'error',
      summary: 'Error',
      detail: message
    });
  }

  private resetForm(): void {
    this.titulo = '';
    this.texto = '';

    this.tituloInput?.reset();
    this.textoInput?.reset();

    this.formSubmitted.set(false);
    this.isHeaderInvalid = false;
    this.isTextInvalid = false;
  }
}
