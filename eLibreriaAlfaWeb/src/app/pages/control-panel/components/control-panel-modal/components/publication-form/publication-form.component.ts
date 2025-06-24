import { Component, computed, Input, signal, SimpleChanges } from '@angular/core';
import { Toast } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { PublicationService } from '../../../../../../core/services/publication.service';
import { PublicacionRequestDto, PublicacionConImagenDto } from '../../../../../../core/models/publicacion';
import { ViewChild } from '@angular/core';

import { FormTextInputComponent } from '../../../../../../shared/components/inputs/form-text-input/form-text-input.component';
import { FormTextareaInputComponent } from '../../../../../../shared/components/inputs/form-textarea-input/form-textarea-input.component';
import { ImageUploadInputComponent } from '../../../../../../shared/components/inputs/image-upload-input/image-upload-input.component';
import { PrimaryButtonComponent } from '../../../../../../shared/components/buttons/primary-button/primary-button.component';
import { ImageDto } from '../../../../../../core/models/image';

@Component({
  selector: 'app-publication-form',
  standalone: true,
  imports: [
    Toast,
    FormTextInputComponent,
    FormTextareaInputComponent,
    ImageUploadInputComponent,
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
  @ViewChild('imageInput') imageInput: any;

  @Input() publication: PublicacionConImagenDto | null = null;

  title: string = '';
  content: string = '';
  existingImage: ImageDto[] = [];
  newImage: File[] = [];
  imageToDelete: string[] = [];

  formSubmitted = signal(false);

  isTitleInvalid: boolean = false;
  isContentInvalid: boolean = false;
  isImageInvalid: boolean = false;

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
    
    if (this.validateForm()) {
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: "Datos ingresados inválidos",
        life: 4000
      });
      return;
    }
    
    this.publication ? this.update() : this.create();
  }
  
  create() {
    const formData = this.createFormData();
    
    this.publicationService.post(formData).subscribe({
      next: (response) => this.handleSuccess('creada', response),
      error: (error) => this.handleError(error)
    });
  }

  update() {
    const formData = this.createFormData();
    
    const publicationData = (this.publication as any)?.publicacion || this.publication;
    const publicationId = publicationData?.id;
    
    if (!publicationId) {
      this.messageService.add({ 
        severity: 'error', 
        summary: 'Error', 
        detail: 'ID de publicación no válido', 
        life: 4000 
      });
      return;
    }

    this.publicationService.put(publicationId, formData).subscribe({
      next: (response) => this.handleSuccess('actualizada', response),
      error: (error) => this.handleError(error)
    });
  }

  validateForm() {
    return this.isTitleInvalid ||
      this.isContentInvalid ||
      this.isImageInvalid;
  }
  
  loadForm() {
    if (this.publication) {
      
      this.title = this.publication.titulo || '';
      this.content = this.publication.contenido || '';
      this.existingImage = this.publication.imagen ? [this.publication.imagen] : [];

      if (this.existingImage) {
        setTimeout(() => {
          if (this.imageInput) {
            this.imageInput.setValue(this.existingImage);
          }
        }, 0);
      }

      this.loadFormFields();
      this.resetValidationState();
    }
  }

  resetForm() {
    this.formSubmitted.set(false);
    
    this.resetFormData();

    this.resetValidationState();
    
    this.resetFormComponents();
  }

  onRemoveImage(event: { image: any, index: number } | null) {
    if (!event?.image) return;
    
    const { image } = event;
    
    if (this.isExistingImage(image)) {
      this.imageToDelete.push(image.relativePath);
      this.existingImage = this.existingImage.filter(img => 
        img.relativePath !== image.relativePath
      );
    } else if (this.isNewImage(image)) {
      this.newImage = this.newImage.filter(file => file !== image.file);
    }
  }

  private createFormData(): FormData {
    const formData = new FormData();
    
    formData.append('titulo', this.title.trim());
    formData.append('contenido', this.content.trim());
    
    if (this.newImage.length > 0) {
      formData.append('imagen', this.newImage[0], this.newImage[0].name);
    }
    
    if (this.publication && this.imageToDelete.length > 0) {
      formData.append('imagenAEliminar', this.imageToDelete[0]);
    }
    
    return formData;
  }

  private handleSuccess(action: string, response: any) {
    this.messageService.add({
      severity: 'success',
      summary: 'Éxito',
      detail: `¡Publicación ${action} exitosamente!`,
      life: 4000
    });
    
    this.resetForm();
  }

  private handleError(error: any) {
    const errorMessage = error?.error.error || error?.error.message || "No fue posible conectar con el servidor";
    this.messageService.add({
      severity: 'error',
      summary: 'Error',
      detail: errorMessage,
      life: 4000
    });
  }

  private loadFormFields() {
    setTimeout(() => {
      this.titleInput?.setValue(this.title);
      this.contentInput?.setValue(this.content);
    }, 0);
  }

  private resetValidationState() {
    this.isTitleInvalid = false;
    this.isContentInvalid = false;
    this.isImageInvalid = false;
  }

  private resetFormData() {
    this.title = '';
    this.content = '';
    this.newImage = [];
    this.existingImage = [];
    this.imageToDelete = [];
  }

  private resetFormComponents() {
    this.titleInput?.reset();
    this.contentInput?.reset();
    this.imageInput?.reset();
  }

  private isExistingImage(image: { relativePath: string; isExisting: true }) {
    return image && ('relativePath' in image || 'filename' in image) && image.isExisting === true;
  }

  private isNewImage(image: { file: File; isExisting: false }) {
    return image && 'file' in image && image.isExisting === false;
  }
}
