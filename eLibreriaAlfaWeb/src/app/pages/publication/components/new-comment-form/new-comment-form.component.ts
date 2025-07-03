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

  // Contadores de caracteres
  get tituloCharCount(): number {
    return this.titulo.length;
  }

  get textoCharCount(): number {
    return this.texto.length;
  }

  get tituloCharCountClass(): string {
    return this.tituloCharCount > 200 ? 'text-red-500' : this.tituloCharCount > 180 ? 'text-orange-500' : 'text-gray-500';
  }

  get textoCharCountClass(): string {
    return this.textoCharCount > 200 ? 'text-red-500' : this.textoCharCount > 180 ? 'text-orange-500' : 'text-gray-500';
  }

  formSubmitted = signal(false);
  textAreaPattern = /^[a-zA-Z0-9\s.,;:!?'"(){}[\]<>%&$#@!^*+=áéíóúÁÉÍÓÚñÑüÜ-]{1,200}$/;
  messagePattern = /^.{1,200}$/;

  constructor(
    private messageService: MessageService,
    private securityService: SecurityService,
    private publicationService: PublicationService
  ) {}
  enviarComentario(): void {
    this.formSubmitted.set(true);
    
    if (this.titulo.trim().length === 0 || this.titulo.trim().length > 200) {
      this.showError('El título debe tener entre 1 y 200 caracteres');
      return;
    }
    
    if (this.texto.trim().length === 0 || this.texto.trim().length > 200) {
      this.showError('El texto debe tener entre 1 y 200 caracteres');
      return;
    }
    
    if (this.validateForm()) {
      return;
    }

    if (!this.securityService.actualUser?.id) {
      this.showError('Debes estar autenticado para comentar');
      return;
    }

    const textoPlano = this.htmlToPlainText(this.texto.trim());
    
    if (textoPlano.length > 200) {
      this.showError('El texto del comentario excede los 200 caracteres permitidos');
      return;
    }

    const commentData = {
        fechaCreacion: new Date().toISOString(),
        usuario: {
        id: Number(this.securityService.actualUser.id) 
        },
        titulo: this.titulo.trim(),
        texto: textoPlano,
        publicacion: {
        id: Number(this.publicacionId)
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
    this.messageService.clear();
    this.messageService.add({
      severity: 'success',
      summary: 'Éxito',
      detail: message
    });
  }

  private showError(message: string): void {
    this.messageService.clear();
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
