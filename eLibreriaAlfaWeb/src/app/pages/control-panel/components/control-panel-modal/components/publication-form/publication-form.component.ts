import { Component, computed, Input, signal, SimpleChanges } from '@angular/core';
import { Toast } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { PublicationService } from '../../../../../../core/services/publication.service';
import { Publicacion, PublicacionSimple, AgregarPublicacion } from '../../../../../../core/models/publicacion';
import { ViewChild } from '@angular/core';

import { FormTextInputComponent } from '../../../../../../shared/components/inputs/form-text-input/form-text-input.component';
import { FormTextareaInputComponent } from '../../../../../../shared/components/inputs/form-textarea-input/form-textarea-input.component';
import { PrimaryButtonComponent } from '../../../../../../shared/components/buttons/primary-button/primary-button.component';

@Component({
  selector: 'app-publication-form',
  standalone: true,
  imports: [
    Toast,
    FormTextInputComponent,
    FormTextareaInputComponent,
    PrimaryButtonComponent
  ],
  providers: [
    MessageService
  ],
  templateUrl: './publication-form.component.html',
  styleUrl: './publication-form.component.scss'
})
export class PublicationFormComponent {
  @ViewChild('titleInput') titleInput: any;
  @ViewChild('contentInput') contentInput: any;

  @Input() publication: PublicacionSimple | null = null;

  title: string = '';
  content: string = '';

  formSubmitted = signal(false);

  isTitleInvalid: boolean = false;
  isContentInvalid: boolean = false;

  titlePattern = /^.{1,200}$/;
  contentPattern = /^.{1,200}$/;

  private htmlToPlainText(html: string): string {
    const tempDiv = document.createElement('div');
    tempDiv.innerHTML = html;
    
    return tempDiv.textContent || tempDiv.innerText || '';
  }   
  get titleCharCount(): number {
    if (!this.title) return 0;
    return this.title?.length || 0;
  }

  get contentCharCount(): number {
    if (!this.content) return 0;
    return this.htmlToPlainText(this.content.trim()).length || 0;
  }

  get titleCharCountClass(): string {
    const count = this.titleCharCount;
    return count > 200 ? 'text-red-500' : count > 180 ? 'text-orange-500' : 'text-gray-500';
  }

  get contentCharCountClass(): string {
    const count = this.contentCharCount;
    return count > 200 ? 'text-red-500' : count > 180 ? 'text-orange-500' : 'text-gray-500';
  }

  constructor(
    private messageService: MessageService,
    private publicationService: PublicationService
  ) {}

  ngOnInit() {
    this.resetForm();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['publication']) {
      this.manageForm();
    }
  }

  manageForm() {
    if (this.publication != null) {
      this.loadForm();
    } else {
      this.resetForm();
    }
  }
  confirm() {
    this.formSubmitted.set(true);
    
    if (this.title.trim().length === 0 || this.title.trim().length > 200) {
      this.messageService.add({ severity: 'error', summary: 'Error', detail: 'El título debe tener entre 1 y 200 caracteres', life: 4000 });
      return;
    }
    
    if (this.htmlToPlainText(this.content.trim()).length === 0 || this.htmlToPlainText(this.content.trim()).length > 2000) {
      this.messageService.add({ severity: 'error', summary: 'Error', detail: 'El contenido no puede estar vacío', life: 4000 });
      return;
    }
    
    if (!this.validateForm()) {
      switch (this.publication) {
        case null:
          this.create();
          break;        default:
          const publicationData = (this.publication as any)?.publicacion || this.publication;
          this.update();
          break;
      }
    } else {
      this.messageService.add({ severity: 'error', summary: 'Error', detail: "Datos ingresados inválidos", life: 4000 });
    }
  }
  
  create() {
    const publicacion: AgregarPublicacion = {
      titulo: this.title.trim(),
      contenido: this.htmlToPlainText(this.content.trim()),
      fechaCreacion: new Date().toISOString(),
      comentarios: []
    };

    this.publicationService.createPublication(publicacion).subscribe({
      next: (response: string) => {
        console.log('Server response:', response);
        this.messageService.add({ severity: 'success', summary: 'Operación exitosa', detail: "¡Publicación creada exitosamente!", life: 4000 });
        this.resetForm();
      },
      error: (err) => {
        let errorMessage = "No fue posible conectar con el servidor";
        
        if (err.error && typeof err.error === 'string') {
          errorMessage = err.error;
        } else if (err.message) {
          errorMessage = err.message;
        }
        
        this.messageService.add({ severity: 'error', summary: 'Error', detail: errorMessage, life: 4000 });
      }
    });
  }  
  update() {
    const publicationData = (this.publication as any)?.publicacion || this.publication;
    const publicationId = publicationData?.id;
    
    if (!publicationId) {
      this.messageService.add({ severity: 'error', summary: 'Error', detail: 'No se puede actualizar: ID de publicación no válido', life: 4000 });
      return;
    }

    const publicacion: AgregarPublicacion = {
      titulo: this.title.trim(),
      contenido: this.htmlToPlainText(this.content.trim()),
      fechaCreacion: new Date().toISOString(),
      comentarios: []
    };

    this.publicationService.updatePublication(publicationId, publicacion).subscribe({
      next: (response: string) => {
        this.messageService.add({ severity: 'success', summary: 'Operación exitosa', detail: "¡Publicación actualizada exitosamente!", life: 4000 });
        this.resetForm();
      },
      error: (err) => {
        console.log('Error details:', err);
        let errorMessage = "No fue posible conectar con el servidor";
        
        if (err.error && typeof err.error === 'string') {
          errorMessage = err.error;
        } else if (err.message) {
          errorMessage = err.message;
        }
        
        this.messageService.add({ severity: 'error', summary: 'Error', detail: errorMessage, life: 4000 });
      }
    });
  }

  validateForm() {
    return this.isTitleInvalid || this.isContentInvalid;
  }  loadForm() {
    if (this.publication) {
      
      const publicationData = (this.publication as any).publicacion || this.publication;
      
      this.title = publicationData.titulo;
      this.content = publicationData.contenido;

      this.isTitleInvalid = false;
      this.isContentInvalid = false;

      if (this.titleInput?.setValue) {
        this.titleInput.setValue(this.title);
      }
      
      if (this.contentInput?.setValue) {
        this.contentInput.setValue(this.content);
      }
    }
  }

  resetForm() {
    this.formSubmitted.set(false);

    this.title = '';
    this.content = '';

    this.isTitleInvalid = false;
    this.isContentInvalid = false;

    this.titleInput?.reset();
    this.contentInput?.reset();
  }
}
