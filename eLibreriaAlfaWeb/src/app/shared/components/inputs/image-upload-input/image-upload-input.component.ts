import { Component, computed, EventEmitter, Input, Output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Message } from 'primeng/message';
import { FileUploadModule } from 'primeng/fileupload';
import { ButtonModule } from 'primeng/button';
import { BadgeModule } from 'primeng/badge';
import { TooltipModule } from 'primeng/tooltip';
import { CommonModule } from '@angular/common';
import { ImageService } from '../../../../core/services/image.service';
import { ImageDto } from '../../../../core/models/image';

interface ImageItem {
  id: string;
  relativePath: string;
  originalName: string;
  size: number;
  preview: string;
  file?: File;
  isExisting: boolean;
}

@Component({
  selector: 'app-image-upload-input',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    Message,
    FileUploadModule,
    ButtonModule,
    BadgeModule,
    TooltipModule
  ],
  templateUrl: './image-upload-input.component.html',
  styleUrl: './image-upload-input.component.scss'
})
export class ImageUploadInputComponent {
  // Señal principal que maneja todas las imágenes
  images = signal<ImageItem[]>([]);

  @Input() placeholder: string = "Seleccionar imágenes";
  @Input() maxFiles: number = 5;
  @Input() maxFileSize: number = 5242880;
  @Input() acceptedTypes: string = "image/*";
  @Input() required: boolean = false;
  @Input() errorMessage: string = "Debe seleccionar al menos una imagen";
  @Input() formSubmitted = signal(false);

  @Output() imagesValue = new EventEmitter<File[]>();
  @Output() isInputInvalid = new EventEmitter<boolean>();
  @Output() removedImages = new EventEmitter<{ image: ImageItem, index: number } | null>();

  constructor(private imageService: ImageService) {}

  // Computed para mostrar error
  showErrorMessage = computed(() => {
    const hasError = this.required && this.images().length === 0;
    return hasError && this.formSubmitted();
  });

  // Computed para archivos nuevos
  newFiles = computed(() => {
    return this.images()
      .filter(img => !img.isExisting && img.file)
      .map(img => img.file!);
  });

  onFileSelect(event: any) {
    const files: File[] = event.files || event.currentFiles || [];
    
    const validFiles: File[] = [];
    const errors: string[] = [];

    for (const file of files) {
      const validation = this.imageService.validateImageFile(file);
      if (validation.isValid) {
        validFiles.push(file);
      } else {
        errors.push(`${file.name}: ${validation.error}`);
      }
    }

    if (errors.length > 0) {
      console.error('Errores de validación:', errors);
      return;
    }

    if (this.images().length + validFiles.length > this.maxFiles) {
      console.error(`No puede subir más de ${this.maxFiles} imágenes`);
      return;
    }

    this.processNewFiles(validFiles);
  }

  private async processNewFiles(files: File[]) {
    const newImageItems: ImageItem[] = [];

    for (const file of files) {
      try {
        const preview = await this.imageService.generateImagePreview(file);
        
        const imageItem: ImageItem = {
          id: `new_${Date.now()}_${Math.random()}`,
          relativePath: file.name,
          originalName: file.name,
          size: file.size,
          preview: preview,
          file: file,
          isExisting: false
        };

        newImageItems.push(imageItem);
      } catch (error) {
        console.error('Error al procesar imagen:', file.name, error);
      }
    }

    const currentImages = this.images();
    this.images.set([...currentImages, ...newImageItems]);
    
    this.emitChanges();
  }

  removeImage(index: number) {
    const currentImages = this.images();
    const imageToRemove = currentImages[index];
    
    this.removedImages.emit({ image: imageToRemove, index });
    
    const updatedImages = currentImages.filter((_, i) => i !== index);
    this.images.set(updatedImages);
    
    this.emitChanges();
  }

  private emitChanges() {
    this.imagesValue.emit(this.newFiles());
    
    const isInvalid = this.required && this.images().length === 0;
    this.isInputInvalid.emit(isInvalid);
  }

  setValue(existingImages: ImageDto[] | null) {
    if (!existingImages?.length) {
      this.images.set([]);
      this.emitChanges();
      return;
    }

    const imageItems: ImageItem[] = existingImages.map((img, index) => {
      const preview = this.imageService.getImageUrl(img.relativePath);
      
      return {
        id: `existing_${img.relativePath}_${index}`,
        relativePath: img.relativePath,
        originalName: img.originalName || img.relativePath,
        size: img.size || 0,
        preview: preview,
        file: undefined,
        isExisting: true
      };
    });

    console.log('ImageItems generados:', imageItems);
    this.images.set(imageItems);
    this.emitChanges();
  }

  reset() {
    this.images.set([]);
    this.emitChanges();
  }

  formatFileSize(bytes: number): string {
    return this.imageService.formatFileSize(bytes);
  }
}